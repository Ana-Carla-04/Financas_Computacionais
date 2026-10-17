package br.edu.ufersa.Financas_Computacionais.features.precificacao.dto;

// Resultado do modelo Cox-Ross-Rubinstein: os parâmetros da árvore
// (antes, os getters da classe CoxRossRubinstein) e o preço das duas opções.
public record CoxRossRubinsteinResponseDTO(
        double deltaT,
        double subida,
        double descida,
        double probabilidadeSubida,
        double precoCall,
        double precoPut) {
}
