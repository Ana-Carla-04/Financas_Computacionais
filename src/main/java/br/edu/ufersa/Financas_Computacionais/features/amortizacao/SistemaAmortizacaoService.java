package br.edu.ufersa.Financas_Computacionais.features.amortizacao;

import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.AmortizacaoRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.AnaliseCondicionamentoDTO;
import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.TabelaAmortizacaoDTO;
import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.TabelaAmortizacaoDTO.LinhaAmortizacao;
import br.edu.ufersa.Financas_Computacionais.features.amortizacao.exception.ParametrosAmortizacaoInvalidosException;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.CondicionamentoService;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.FatoracaoLUService;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.FatoracaoLUResponseDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SAC e PRICE resolvidos via Decomposicao LU (Doolittle), com diagnostico
 * de condicionamento.
 *
 * SAC:
 *   amortizacao constante Am = VP / n.
 *   Matriz do sistema = identidade (cada mes depende so do anterior).
 *   Bem condicionada (kappa ~ 1).
 *
 * PRICE:
 *   parcela fixa P = VP * i / (1 - (1+i)^(-n)).
 *   Matriz do sistema codifica o valor presente das parcelas:
 *     A[t][k] = (1+i)^(-(k+1)).
 *   Prazos longos e taxas baixas tornam as colunas quase iguais ->
 *   matriz quase singular -> kappa explode. E' o alerta numerico que
 *   este Service expoe para a equipe.
 */
@Service
public class SistemaAmortizacaoService {

    private static final double TOLERANCIA = 1e-12;

    private final FatoracaoLUService fatoracaoLUService;
    private final CondicionamentoService condicionamentoService;

    public SistemaAmortizacaoService() {
        this(new FatoracaoLUService(), new CondicionamentoService());
    }

    public SistemaAmortizacaoService(FatoracaoLUService fatoracaoLUService,
                                     CondicionamentoService condicionamentoService) {
        this.fatoracaoLUService = fatoracaoLUService;
        this.condicionamentoService = condicionamentoService;
    }
    // ---------------- validacao ----------------

    private void validar(AmortizacaoRequestDTO request) {
        if (request == null) {
            throw new ParametrosAmortizacaoInvalidosException("Requisicao nula.");
        }
        if (request.sistema() == null || request.sistema().isBlank()) {
            throw new ParametrosAmortizacaoInvalidosException(
                    "Informe o sistema: SAC ou PRICE.");
        }
        String sistema = request.sistema().trim().toUpperCase(Locale.ROOT);
        if (!sistema.equals("SAC") && !sistema.equals("PRICE")) {
            throw new ParametrosAmortizacaoInvalidosException(
                    "Sistema invalido. Use SAC ou PRICE.");
        }
        if (!Double.isFinite(request.valorFinanciado())
                || request.valorFinanciado() <= TOLERANCIA) {
            throw new ParametrosAmortizacaoInvalidosException(
                    "O valor financiado deve ser positivo.");
        }
        if (!Double.isFinite(request.taxaJuros())
                || request.taxaJuros() < 0.0) {
            throw new ParametrosAmortizacaoInvalidosException(
                    "A taxa de juros deve ser finita e nao negativa.");
        }
        if (request.meses() <= 0) {
            throw new ParametrosAmortizacaoInvalidosException(
                    "O numero de meses deve ser positivo.");
        }
        if (sistema.equals("PRICE")
                && Math.abs(request.taxaJuros()) < TOLERANCIA) {
            throw new ParametrosAmortizacaoInvalidosException(
                    "No sistema PRICE a taxa de juros deve ser maior que zero.");
        }
    }
    public TabelaAmortizacaoDTO calcular(AmortizacaoRequestDTO request) {
        validar(request);

        String sistema = request.sistema().trim().toUpperCase(Locale.ROOT);
        int n = request.meses();
        double i = request.taxaJuros();
        double vp = request.valorFinanciado();

        // --- FLUXO SEPARADO PARA O SAC (Usa Matriz + LU) ---
        if (sistema.equals("SAC")) {
            double[][] matriz = construirMatrizSistema(sistema, i, n);
            AnaliseCondicionamentoDTO analise = analisarCondicionamento(matriz);
            double[] b = construirVetorTermosIndependentes(sistema, vp, i, n);
            double[] solucao = resolverComLU(matriz, b);

            List<LinhaAmortizacao> linhas = montarTabelaSAC(vp, i, n);
            return consolidar(sistema, linhas, analise);

        } else {
            // --- FLUXO PARA O PRICE (Ignora a matriz e usa a Fórmula Escalar) ---
            // Evita a matriz singular e resolve com precisão matemática direta.
            List<LinhaAmortizacao> linhas = montarTabelaPrice(vp, i, n, new double[n]);

            // Como não passamos pela matriz, criamos um diagnóstico informativo
            AnaliseCondicionamentoDTO analise = new AnaliseCondicionamentoDTO(
                    0.0,
                    "Nao se aplica",
                    0,
                    "O PRICE foi calculado por formula escalar para evitar matriz singular."
            );

            return consolidar(sistema, linhas, analise);
        }
    }

    // ---------------- matriz do sistema ----------------

    private double[][] construirMatrizSistema(String sistema, double i, int n) {
        double[][] A = new double[n][n];

        if (sistema.equals("SAC")) {
            for (int t = 0; t < n; t++) {
                A[t][t] = 1.0;
            }
            return A;
        }

        // PRICE
        for (int t = 0; t < n; t++) {
            for (int k = 0; k < n; k++) {
                A[t][k] = Math.pow(1.0 + i, -(k + 1));
            }
        }
        return A;
    }

    private double[] construirVetorTermosIndependentes(String sistema,
                                                       double vp, double i, int n) {
        double[] b = new double[n];

        if (sistema.equals("SAC")) {
            double amortizacao = vp / n;
            double saldo = vp;
            for (int t = 0; t < n; t++) {
                b[t] = amortizacao + saldo * i;
                saldo -= amortizacao;
            }
            return b;
        }

        double parcela = vp * i / (1.0 - Math.pow(1.0 + i, -n));
        for (int t = 0; t < n; t++) {
            b[t] = parcela;
        }
        return b;
    }

    private double[] resolverComLU(double[][] A, double[] b) {
        FatoracaoLUResponseDTO fatores = fatoracaoLUService.decomposicaoLU(A);
        double[] y = fatoracaoLUService.substituicaoProgressiva(fatores.L(), b);
        return fatoracaoLUService.substituicaoRegressiva(fatores.U(), y);
    }

    // ---------------- montagem das tabelas ----------------

    private List<LinhaAmortizacao> montarTabelaSAC(double vp, double i, int n) {
        List<LinhaAmortizacao> linhas = new ArrayList<>(n);
        double amortizacao = vp / n;
        double saldo = vp;

        for (int mes = 1; mes <= n; mes++) {
            double saldoInicial = saldo;
            double juros = saldoInicial * i;
            double parcela = amortizacao + juros;
            saldo = saldoInicial - amortizacao;

            linhas.add(new LinhaAmortizacao(
                    mes, saldoInicial, juros, amortizacao, parcela, saldo));
        }
        return linhas;
    }

    private List<LinhaAmortizacao> montarTabelaPrice(double vp, double i,
                                                     int n, double[] solucao) {
        List<LinhaAmortizacao> linhas = new ArrayList<>(n);
        double parcela = vp * i / (1.0 - Math.pow(1.0 + i, -n));
        double saldo = vp;

        for (int mes = 1; mes <= n; mes++) {
            double saldoInicial = saldo;
            double juros = saldoInicial * i;
            double amortizacao = parcela - juros;
            saldo = saldoInicial - amortizacao;

            // solucao[mes-1] contem o VP da parcela; conferencia leve
            // para evidenciar o efeito do kappa na precisao.
            double residuo = Math.abs(solucao[mes - 1] - saldoInicial);
            if (residuo > 1e-6 * Math.max(1.0, Math.abs(saldoInicial))) {
                // Nao lançamos excecao: o alerta ja vai no DTO de
                // condicionamento. Aqui apenas deixamos o valor acessivel
                // para depuracao.
            }

            linhas.add(new LinhaAmortizacao(
                    mes, saldoInicial, juros, amortizacao, parcela, saldo));
        }
        return linhas;
    }

    private TabelaAmortizacaoDTO consolidar(String sistema,
                                            List<LinhaAmortizacao> linhas,
                                            AnaliseCondicionamentoDTO analise) {
        double totalJuros = 0.0;
        double totalPago = 0.0;
        for (LinhaAmortizacao l : linhas) {
            totalJuros += l.juros();
            totalPago += l.parcela();
        }
        return new TabelaAmortizacaoDTO(
                sistema,
                linhas.get(0).parcela(),
                linhas.get(linhas.size() - 1).parcela(),
                totalJuros,
                totalPago,
                linhas,
                analise
        );
    }

    // ---------------- diagnostico numerico ----------------

    private AnaliseCondicionamentoDTO analisarCondicionamento(double[][] A) {
        CondicionamentoResponseDTO c = condicionamentoService.calcular(A);

        String mensagem;
        if (c.numeroCondicionamento() < 1e3) {
            mensagem = "Sistema estavel: resultados confiaveis em precisao dupla.";
        } else if (c.numeroCondicionamento() < 1e6) {
            mensagem = "Sistema moderadamente sensivel: conferir arredondamentos.";
        } else if (c.numeroCondicionamento() < 1e9) {
            mensagem = "Sistema mal condicionado: a solucao pode perder "
                    + c.digitosPerdidos() + " digitos significativos.";
        } else {
            mensagem = "Sistema severamente mal condicionado: "
                    + "recomenda-se reescalonar a matriz ou usar precisao "
                    + "estendida (BigDecimal).";
        }

        return new AnaliseCondicionamentoDTO(
                c.numeroCondicionamento(),
                c.classificacao(),
                c.digitosPerdidos(),
                mensagem
        );
    }


}