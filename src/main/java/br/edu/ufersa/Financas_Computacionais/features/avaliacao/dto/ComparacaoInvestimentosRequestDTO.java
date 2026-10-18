package br.edu.ufersa.Financas_Computacionais.features.avaliacao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record ComparacaoInvestimentosRequestDTO(
        @NotNull @Valid InvestimentoRequestDTO primeiro,
        @NotNull @Valid InvestimentoRequestDTO segundo
) {
        @AssertTrue(message = "Os investimentos devem usar a mesma taxa de desconto.")
        public boolean isTaxaDescontoConsistente() {
                return primeiro == null || segundo == null
                                || Double.compare(primeiro.taxaDesconto(), segundo.taxaDesconto()) == 0;
        }
}