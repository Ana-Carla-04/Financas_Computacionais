package br.edu.ufersa.Financas_Computacionais.features.precificacao.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;

// Parâmetros da precificação pela árvore binomial.
// Substitui os oito parâmetros soltos que precificarCall e precificarPut
// recebiam. Os nomes seguem a notação usada nas fórmulas:
//
// S0 = preço atual do ativo;
// K  = preço de exercício;
// r  = taxa livre de risco anual;
// T  = tempo até o vencimento, em anos;
// N  = número de períodos;
// u  = fator de alta;
// d  = fator de baixa;
// p  = probabilidade neutra ao risco de alta.
public record ArvoreBinomialRequestDTO(
        @DecimalMin(value = "0.0", inclusive = false)
        double S0,
        @DecimalMin(value = "0.0", inclusive = false)
        double K,
        double r,
        @DecimalMin(value = "0.0", inclusive = false)
        double T,
        @Positive
        int N,
        @DecimalMin(value = "0.0", inclusive = false)
        double u,
        @DecimalMin(value = "0.0", inclusive = false)
        double d,
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        double p) {

        @AssertTrue(message = "Todos os parametros devem ser finitos.")
        public boolean isEntradaFinita() {
                return Double.isFinite(S0) && Double.isFinite(K) && Double.isFinite(r)
                                && Double.isFinite(T) && Double.isFinite(u) && Double.isFinite(d)
                                && Double.isFinite(p);
        }
}
