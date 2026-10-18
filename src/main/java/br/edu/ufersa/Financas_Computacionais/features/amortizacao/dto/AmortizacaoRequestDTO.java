package br.edu.ufersa.Financas_Computacionais.features.amortizacao.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record AmortizacaoRequestDTO(
        @NotBlank(message = "Informe SAC ou PRICE.")
        @Pattern(regexp = "(?i)SAC|PRICE", message = "O sistema deve ser SAC ou PRICE.")
        String sistema,
        @DecimalMin(value = "0.0", inclusive = false,
                message = "O valor financiado deve ser positivo.")
        double valorFinanciado,
        @DecimalMin(value = "0.0", message = "A taxa de juros nao pode ser negativa.")
        double taxaJuros,
        @Positive(message = "O numero de meses deve ser positivo.")
        int meses
) {
        @AssertTrue(message = "Os valores financeiros devem ser finitos.")
        public boolean isEntradaFinita() {
                return Double.isFinite(valorFinanciado) && Double.isFinite(taxaJuros);
        }
}