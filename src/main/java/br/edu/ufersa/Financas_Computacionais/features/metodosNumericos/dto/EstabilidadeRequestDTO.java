package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Entrada da análise de sensibilidade: sistema Ax = b e a perturbação δb.
public record EstabilidadeRequestDTO(
        @NotNull(message = "Informe a matriz do sistema.")
        @Size(min = 1, message = "A matriz nao pode ser vazia.")
        double[][] matriz,
        @NotNull(message = "Informe o vetor de termos independentes.")
        @Size(min = 1, message = "O vetor nao pode ser vazio.")
        double[] termosIndependentes,
        @NotNull(message = "Informe a perturbacao em b.")
        @Size(min = 1, message = "A perturbacao nao pode ser vazia.")
        double[] perturbacao
) {
    @AssertTrue(message = "A matriz deve ser quadrada e compativel com os vetores, com valores finitos.")
    public boolean isSistemaValido() {
        if (matriz == null || termosIndependentes == null || perturbacao == null
                || matriz.length == 0
                || termosIndependentes.length != matriz.length
                || perturbacao.length != matriz.length) {
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
        for (int i = 0; i < matriz.length; i++) {
            if (!Double.isFinite(termosIndependentes[i]) || !Double.isFinite(perturbacao[i])) {
                return false;
            }
        }
        return true;
    }
}