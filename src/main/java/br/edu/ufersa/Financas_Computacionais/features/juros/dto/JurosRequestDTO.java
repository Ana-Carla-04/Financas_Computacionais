package br.edu.ufersa.Financas_Computacionais.features.juros.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Entrada de valor futuro e valor presente.
//  - valor: capital inicial (valor futuro) ou montante desejado (valor presente);
//  - taxa: taxa por período, em decimal (0,02 = 2%);
//  - periodos: número de períodos, na mesma unidade da taxa.
public record JurosRequestDTO(
        @NotBlank(message = "Informe SIMPLES ou COMPOSTO.")
        @Pattern(regexp = "(?i)SIMPLES|COMPOSTO", message = "O regime deve ser SIMPLES ou COMPOSTO.")
        String regime,
        @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser positivo.")
        double valor,
        @DecimalMin(value = "0.0", message = "A taxa nao pode ser negativa.")
        double taxa,
        @DecimalMin(value = "0.0", message = "O numero de periodos nao pode ser negativo.")
        double periodos
) {
    @AssertTrue(message = "Os valores financeiros devem ser finitos.")
    public boolean isEntradaFinita() {
        return Double.isFinite(valor) && Double.isFinite(taxa) && Double.isFinite(periodos);
    }
}