package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

// Classificação de um sistema linear quadrado Ax = b.
// tipo: "SPD" (solução única), "SPI" (infinitas soluções) ou "SI" (sem solução).
public record ClassificacaoSistemaResponseDTO(
        String tipo,
        String descricao,
        double determinante,
        int posto
) {
}