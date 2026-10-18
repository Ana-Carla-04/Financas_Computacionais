package br.edu.ufersa.Financas_Computacionais.features.amortizacao;

import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.AmortizacaoRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.TabelaAmortizacaoDTO;

/**
 * Porta de entrada da feature amortizacao. Mesmo padrao dos demais
 * Controllers do projeto: recebe a chamada, delega ao Service e
 * apresenta o resultado (aqui, console). Pode ser chamado pelo Main
 * ou pelo MetodosNumericosController.
 */
public class SistemaAmortizacaoController {

    private final SistemaAmortizacaoService service;

    public SistemaAmortizacaoController() {
        this(new SistemaAmortizacaoService());
    }

    public SistemaAmortizacaoController(SistemaAmortizacaoService service) {
        this.service = service;
    }

    public TabelaAmortizacaoDTO calcular(AmortizacaoRequestDTO request) {
        return service.calcular(request);
    }

    public void demonstrar() {
        AmortizacaoRequestDTO sac = new AmortizacaoRequestDTO(
                "SAC", 100_000.0, 0.01, 360);
        AmortizacaoRequestDTO price = new AmortizacaoRequestDTO(
                "PRICE", 100_000.0, 0.01, 360);

        imprimir(service.calcular(sac));
        System.out.println();
        imprimir(service.calcular(price));
    }

    private void imprimir(TabelaAmortizacaoDTO t) {
        System.out.printf("=== %s ===%n", t.sistema());
        System.out.printf("Parcela inicial: R$ %.2f%n", t.parcelaInicial());
        System.out.printf("Parcela final:   R$ %.2f%n", t.parcelaFinal());
        System.out.printf("Total de juros:  R$ %.2f%n", t.totalJuros());
        System.out.printf("Total pago:      R$ %.2f%n", t.totalPago());
        System.out.printf("kappa(A) = %.4e | %s%n",
                t.condicionamento().kappa(),
                t.condicionamento().classificacao());
        System.out.println("Alerta: " + t.condicionamento().mensagem());
    }
}