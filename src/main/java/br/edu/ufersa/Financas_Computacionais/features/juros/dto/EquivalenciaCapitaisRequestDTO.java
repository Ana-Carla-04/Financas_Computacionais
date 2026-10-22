package br.edu.ufersa.Financas_Computacionais.features.juros.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Quanto aplicar hoje em cada conta j (taxa composta taxas[j]) para ter
// metas[m] na data prazos[m]? São k contas, k datas e k metas.
public record EquivalenciaCapitaisRequestDTO(
        @NotNull(message = "Informe as taxas das contas.")
        @Size(min = 1, message = "Informe ao menos uma conta.")
        double[] taxas,
        @NotNull(message = "Informe os prazos das metas.")
        @Size(min = 1, message = "Informe ao menos um prazo.")
        double[] prazos,
        @NotNull(message = "Informe as metas.")
        @Size(min = 1, message = "Informe ao menos uma meta.")
        double[] metas
) {
    @AssertTrue(message = "Taxas, prazos e metas devem ter o mesmo tamanho, valores finitos, "
            + "taxas maiores que -1 e prazos nao negativos.")
    public boolean isEntradaValida() {
        if (taxas == null || prazos == null || metas == null
                || taxas.length == 0
                || prazos.length != taxas.length
                || metas.length != taxas.length) {
            return false;
        }
        for (int i = 0; i < taxas.length; i++) {
            if (!Double.isFinite(taxas[i]) || taxas[i] <= -1.0
                    || !Double.isFinite(prazos[i]) || prazos[i] < 0.0
                    || !Double.isFinite(metas[i])) {
                return false;
            }
        }
        return true;
    }
}