package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SistemaLinearRequestDTO(
        @NotNull(message = "Informe a matriz do sistema.")
        @Size(min = 1, message = "A matriz nao pode ser vazia.")
        double[][] matriz,
        @NotNull(message = "Informe o vetor de termos independentes.")
        @Size(min = 1, message = "O vetor nao pode ser vazio.")
        double[] termosIndependentes,
        double[] chuteInicial,
        @Positive(message = "A tolerancia deve ser positiva.")
        Double tolerancia,
        @Positive(message = "O limite de iteracoes deve ser positivo.")
        Integer maxIteracoes
) {
    @AssertTrue(message = "A matriz deve ser quadrada e compativel com o vetor, com valores finitos.")
    public boolean isSistemaValido() {
        if (matriz == null || termosIndependentes == null
                || matriz.length == 0 || termosIndependentes.length != matriz.length) {
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
        for (double valor : termosIndependentes) {
            if (!Double.isFinite(valor)) {
                return false;
            }
        }
        if (chuteInicial != null) {
            if (chuteInicial.length != matriz.length) {
                return false;
            }
            for (double valor : chuteInicial) {
                if (!Double.isFinite(valor)) {
                    return false;
                }
            }
        }
        return (tolerancia == null && maxIteracoes == null && chuteInicial == null)
            || (tolerancia != null && maxIteracoes != null);
    }
}