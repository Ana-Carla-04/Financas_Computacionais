package br.edu.ufersa.Financas_Computacionais.features.avaliacao.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record InvestimentoRequestDTO(
        @NotBlank(message = "O nome do investimento e obrigatorio.")
        String nome,
        @NotNull(message = "Informe os fluxos de caixa.")
        @Size(min = 2, message = "Informe o investimento inicial e ao menos um fluxo futuro.")
        double[] fluxosCaixa,
        @DecimalMin(value = "-1.0", inclusive = false,
                message = "A taxa de desconto deve ser maior que -100%.")
        double taxaDesconto
) {
        @AssertTrue(message = "Os fluxos de caixa e a taxa devem ser finitos.")
        public boolean isEntradaFinita() {
                if (!Double.isFinite(taxaDesconto) || fluxosCaixa == null) {
                        return false;
                }
                for (double fluxo : fluxosCaixa) {
                        if (!Double.isFinite(fluxo)) {
                                return false;
                        }
                }
                return true;
        }
}