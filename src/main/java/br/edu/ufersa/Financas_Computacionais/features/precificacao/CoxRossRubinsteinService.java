package br.edu.ufersa.Financas_Computacionais.features.precificacao;

import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.CoxRossRubinsteinRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.dto.CoxRossRubinsteinResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.exception.ParametrosPrecificacaoInvalidosException;
import br.edu.ufersa.Financas_Computacionais.features.precificacao.exception.ProbabilidadeInvalidaException;
import org.springframework.stereotype.Service;

@Service
public class CoxRossRubinsteinService {

    // Parâmetros da árvore calculados a partir da entrada (antes eram campos
    // da classe, preenchidos no construtor).
    private record ParametrosArvore(
            double deltaT,
            double subida,
            double descida,
            double probabilidadeSubida) {
    }

    // Calcula os parâmetros da árvore e o preço da call e da put.
    public CoxRossRubinsteinResponseDTO calcular(CoxRossRubinsteinRequestDTO request) {

        ParametrosArvore parametros = calcularParametros(request);

        return new CoxRossRubinsteinResponseDTO(
                parametros.deltaT(),
                parametros.subida(),
                parametros.descida(),
                parametros.probabilidadeSubida(),
                precificar(request, parametros, true),
                precificar(request, parametros, false)
        );
    }

    public double precificarCall(CoxRossRubinsteinRequestDTO request) {
        return precificar(request, calcularParametros(request), true);
    }

    public double precificarPut(CoxRossRubinsteinRequestDTO request) {
        return precificar(request, calcularParametros(request), false);
    }

    private ParametrosArvore calcularParametros(CoxRossRubinsteinRequestDTO request) {

        if (request.precoInicial() <= 0 || request.precoExercicio() <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "Os preços devem ser positivos.");
        }

        if (request.tempo() <= 0 || request.volatilidade() <= 0
                || request.periodos() <= 0) {
            throw new ParametrosPrecificacaoInvalidosException(
                    "Tempo, volatilidade e períodos devem ser positivos.");
        }

        double deltaT = request.tempo() / request.periodos();
        double subida = Math.exp(request.volatilidade() * Math.sqrt(deltaT));
        double descida = 1.0 / subida;

        double probabilidadeSubida =
                (Math.exp(request.taxaJuros() * deltaT) - descida)
                        / (subida - descida);

        if (probabilidadeSubida < 0 || probabilidadeSubida > 1) {
            throw new ProbabilidadeInvalidaException(
                    "Probabilidade inválida. Verifique os parâmetros.");
        }

        return new ParametrosArvore(deltaT, subida, descida, probabilidadeSubida);
    }

    private double precificar(CoxRossRubinsteinRequestDTO request,
                              ParametrosArvore parametros,
                              boolean call) {

        int periodos = request.periodos();
        double subida = parametros.subida();
        double descida = parametros.descida();
        double probabilidadeSubida = parametros.probabilidadeSubida();

        double[] valores = new double[periodos + 1];

        // Calcula os valores da opção no vencimento.
        for (int j = 0; j <= periodos; j++) {

            double precoAtivo = request.precoInicial()
                    * Math.pow(subida, j)
                    * Math.pow(descida, periodos - j);

            if (call) {
                valores[j] = Math.max(
                        precoAtivo - request.precoExercicio(), 0);
            } else {
                valores[j] = Math.max(
                        request.precoExercicio() - precoAtivo, 0);
            }
        }

        // Calcula os valores anteriores por indução retroativa.
        double desconto = Math.exp(-request.taxaJuros() * parametros.deltaT());

        for (int i = periodos - 1; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {

                valores[j] = desconto * (
                        probabilidadeSubida * valores[j + 1]
                                + (1 - probabilidadeSubida) * valores[j]
                );
            }
        }

        return valores[0];
    }
}
