package br.edu.ufersa.Financas_Computacionais.features.precificacao.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

// Parâmetros do modelo Cox-Ross-Rubinstein.
// Antes eram os argumentos do construtor da classe CoxRossRubinstein.
public record CoxRossRubinsteinRequestDTO(
        @DecimalMin(value = "0.0", inclusive = false)
        double precoInicial,
        @DecimalMin(value = "0.0", inclusive = false)
        double precoExercicio,
        @DecimalMin(value = "0.0", inclusive = false)
        double tempo,
        double taxaJuros,
        @DecimalMin(value = "0.0", inclusive = false)
        double volatilidade,
        @Positive
        int periodos) {

        @AssertTrue(message = "Todos os parametros financeiros devem ser finitos.")
        public boolean isEntradaFinita() {
                return Double.isFinite(precoInicial) && Double.isFinite(precoExercicio)
                                && Double.isFinite(tempo) && Double.isFinite(taxaJuros)
                                && Double.isFinite(volatilidade);
        }
}
