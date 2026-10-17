package br.edu.ufersa.Financas_Computacionais.features.precificacao.dto;

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
        double S0,
        double K,
        double r,
        double T,
        int N,
        double u,
        double d,
        double p) {
}
