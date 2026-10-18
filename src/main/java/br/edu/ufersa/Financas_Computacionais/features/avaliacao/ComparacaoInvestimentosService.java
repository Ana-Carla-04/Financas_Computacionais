package br.edu.ufersa.Financas_Computacionais.features.avaliacao;

import java.util.OptionalDouble;

public class ComparacaoInvestimentosService {

    private static final int ITERACOES_MAXIMAS_TIR = 200;
    private static final double TOLERANCIA_TIR = 1e-10;

    public record ResultadoInvestimento(String nome, double vpl,
                                        OptionalDouble tir,
                                        OptionalDouble paybackSimples,
                                        OptionalDouble paybackDescontado) {
    }

    public record Comparacao(ResultadoInvestimento primeiro,
                             ResultadoInvestimento segundo,
                             String recomendacao) {
    }

    public Comparacao comparar(String nomePrimeiro, double[] fluxosPrimeiro,
                               String nomeSegundo, double[] fluxosSegundo,
                               double taxaDesconto) {
        ResultadoInvestimento primeiro = analisar(
                nomePrimeiro, fluxosPrimeiro, taxaDesconto);
        ResultadoInvestimento segundo = analisar(
                nomeSegundo, fluxosSegundo, taxaDesconto);

        double diferenca = primeiro.vpl() - segundo.vpl();
        double escala = Math.max(1.0,
                Math.max(Math.abs(primeiro.vpl()), Math.abs(segundo.vpl())));
        String recomendacao;
        if (Math.abs(diferenca) <= 1e-9 * escala) {
            recomendacao = "Empate tecnico pelo VPL";
        } else {
            recomendacao = diferenca > 0.0 ? primeiro.nome() : segundo.nome();
        }

        return new Comparacao(primeiro, segundo, recomendacao);
    }

    public ResultadoInvestimento analisar(String nome, double[] fluxos,
                                          double taxaDesconto) {
        validarNome(nome);
        validarFluxos(fluxos);
        validarTaxa(taxaDesconto);

        return new ResultadoInvestimento(
                nome,
                calcularVpl(fluxos, taxaDesconto),
                calcularTir(fluxos),
                calcularPaybackSimples(fluxos),
                calcularPaybackDescontado(fluxos, taxaDesconto)
        );
    }

    public double calcularVpl(double[] fluxos, double taxaDesconto) {
        validarFluxos(fluxos);
        validarTaxa(taxaDesconto);

        double vpl = 0.0;
        for (int periodo = 0; periodo < fluxos.length; periodo++) {
            vpl += fluxos[periodo] / Math.pow(1.0 + taxaDesconto, periodo);
        }
        if (!Double.isFinite(vpl)) {
            throw new ArithmeticException("O calculo do VPL produziu um valor nao finito.");
        }
        return vpl;
    }

    public OptionalDouble calcularTir(double[] fluxos) {
        validarFluxos(fluxos);
        if (contarMudancasDeSinal(fluxos) != 1) {
            return OptionalDouble.empty();
        }

        double inferior = -0.999999;
        double superior = 1.0;
        double vplInferior;
        double vplSuperior;
        try {
            vplInferior = calcularVpl(fluxos, inferior);
            vplSuperior = calcularVpl(fluxos, superior);
            while (mesmoSinal(vplInferior, vplSuperior) && superior < 1e12) {
                superior = superior * 2.0 + 1.0;
                vplSuperior = calcularVpl(fluxos, superior);
            }
        } catch (ArithmeticException ex) {
            return OptionalDouble.empty();
        }

        if (mesmoSinal(vplInferior, vplSuperior)) {
            return OptionalDouble.empty();
        }

        for (int iteracao = 0; iteracao < ITERACOES_MAXIMAS_TIR; iteracao++) {
            double meio = inferior + (superior - inferior) / 2.0;
            double vplMeio = calcularVpl(fluxos, meio);

            if (Math.abs(vplMeio) <= TOLERANCIA_TIR
                    || superior - inferior <= TOLERANCIA_TIR) {
                return OptionalDouble.of(meio);
            }

            if (mesmoSinal(vplInferior, vplMeio)) {
                inferior = meio;
                vplInferior = vplMeio;
            } else {
                superior = meio;
            }
        }

        return OptionalDouble.of(inferior + (superior - inferior) / 2.0);
    }

    public OptionalDouble calcularPaybackSimples(double[] fluxos) {
        validarFluxos(fluxos);
        return calcularPayback(fluxos, 0.0);
    }

    public OptionalDouble calcularPaybackDescontado(double[] fluxos,
                                                     double taxaDesconto) {
        validarFluxos(fluxos);
        validarTaxa(taxaDesconto);
        return calcularPayback(fluxos, taxaDesconto);
    }

    private OptionalDouble calcularPayback(double[] fluxos, double taxaDesconto) {
        double saldo = fluxos[0];
        if (saldo >= 0.0) {
            return OptionalDouble.of(0.0);
        }

        for (int periodo = 1; periodo < fluxos.length; periodo++) {
            double fluxoAtual = fluxos[periodo]
                    / Math.pow(1.0 + taxaDesconto, periodo);
            double saldoAnterior = saldo;
            saldo += fluxoAtual;

            if (saldo >= 0.0 && fluxoAtual > 0.0) {
                return OptionalDouble.of(
                        periodo - 1.0 + (-saldoAnterior / fluxoAtual)
                );
            }
        }
        return OptionalDouble.empty();
    }

    private int contarMudancasDeSinal(double[] fluxos) {
        int sinalAnterior = 0;
        int mudancas = 0;
        for (double fluxo : fluxos) {
            int sinalAtual = fluxo > 0.0 ? 1 : fluxo < 0.0 ? -1 : 0;
            if (sinalAtual != 0) {
                if (sinalAnterior != 0 && sinalAtual != sinalAnterior) {
                    mudancas++;
                }
                sinalAnterior = sinalAtual;
            }
        }
        return mudancas;
    }

    private boolean mesmoSinal(double primeiro, double segundo) {
        return Math.copySign(1.0, primeiro) == Math.copySign(1.0, segundo);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do investimento e obrigatorio.");
        }
    }

    private void validarFluxos(double[] fluxos) {
        if (fluxos == null || fluxos.length < 2) {
            throw new IllegalArgumentException(
                    "Informe o investimento inicial e pelo menos um fluxo futuro."
            );
        }
        for (double fluxo : fluxos) {
            if (!Double.isFinite(fluxo)) {
                throw new IllegalArgumentException(
                        "Os fluxos de caixa devem ser todos finitos."
                );
            }
        }
    }

    private void validarTaxa(double taxaDesconto) {
        if (!Double.isFinite(taxaDesconto) || taxaDesconto <= -1.0) {
            throw new IllegalArgumentException(
                    "A taxa deve ser finita e maior que -100%."
            );
        }
    }
}