package br.edu.ufersa.Financas_Computacionais.features.amortizacao;

import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.AmortizacaoRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto.TabelaAmortizacaoDTO;

public class SistemaAmortizacao {

    private static final SistemaAmortizacaoController CONTROLLER =
            new SistemaAmortizacaoController();

    private SistemaAmortizacao() {
    }

    public static TabelaAmortizacaoDTO calcular(AmortizacaoRequestDTO request) {
        return CONTROLLER.calcular(request);
    }

    public static TabelaAmortizacaoDTO sac(double valorFinanciado,
                                           double taxaJuros, int meses) {
        return CONTROLLER.calcular(new AmortizacaoRequestDTO(
                "SAC", valorFinanciado, taxaJuros, meses));
    }

    public static TabelaAmortizacaoDTO price(double valorFinanciado,
                                             double taxaJuros, int meses) {
        return CONTROLLER.calcular(new AmortizacaoRequestDTO(
                "PRICE", valorFinanciado, taxaJuros, meses));
    }
}