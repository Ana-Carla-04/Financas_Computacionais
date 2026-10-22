package br.edu.ufersa.Financas_Computacionais.features.interpolacao.dto;

import br.edu.ufersa.Financas_Computacionais.features.interpolacao.TipoMetodoInterpolacao;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// Responsável por receber a requisição http
// User passa uma lista de prazos, uma lista de taxas, o prazoDesejado (x) e o método de cálculo

public record InterpolacaoRequest(
                @NotNull(message = "Informe os prazos conhecidos.")
                @Size(min = 2, message = "Informe ao menos dois pontos de interpolacao.")
        List<Double> prazos,
                @NotNull(message = "Informe as taxas conhecidas.")
                @Size(min = 2, message = "Informe ao menos duas taxas.")
        List<Double> taxas,
                @NotNull(message = "Informe o prazo desejado.")
        Double prazoDesejado,
                @NotNull(message = "Informe LAGRANGE ou NEWTON.")
        TipoMetodoInterpolacao metodo
) {
        @AssertTrue(message = "Prazos e taxas devem ter o mesmo tamanho, valores finitos e prazos distintos.")
        public boolean isEntradaValida() {
                if (prazos == null || taxas == null || prazoDesejado == null
                                || prazos.size() < 2 || prazos.size() != taxas.size()
                                || !Double.isFinite(prazoDesejado)) {
                        return false;
                }
                for (int i = 0; i < prazos.size(); i++) {
                        Double prazo = prazos.get(i);
                        Double taxa = taxas.get(i);
                        if (prazo == null || taxa == null
                                        || !Double.isFinite(prazo) || !Double.isFinite(taxa)) {
                                return false;
                        }
                        for (int j = 0; j < i; j++) {
                                if (prazo.doubleValue() == prazos.get(j).doubleValue()) {
                                        return false;
                                }
                        }
                }
                return true;
        }
}
