package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MatrizRequestDTO(
        @NotNull(message = "Informe a matriz.")
        @Size(min = 1, message = "A matriz nao pode ser vazia.")
        double[][] matriz
) {
    @AssertTrue(message = "A matriz deve ser quadrada e conter valores finitos.")
    public boolean isMatrizValida() {
        if (matriz == null || matriz.length == 0) {
            return false;
        }
        for (double[] linha : matriz) {
            if (linha == null || linha.length != matriz.length) {
                return false;
            }
            for (double valor : linha) {
                if (!Double.isFinite(valor)) {
                    return false;
                }
            }
        }
        return true;
    }
}