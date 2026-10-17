package br.edu.ufersa.Financas_Computacionais.features.precificacao.dto;

// Parâmetros do modelo Cox-Ross-Rubinstein.
// Antes eram os argumentos do construtor da classe CoxRossRubinstein.
public record CoxRossRubinsteinRequestDTO(
        double precoInicial,
        double precoExercicio,
        double tempo,
        double taxaJuros,
        double volatilidade,
        int periodos) {
}
