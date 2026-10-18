package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.shared.validation.ValidadorMatriz;

public class JacobiService {

	private static final double TOLERANCIA_PADRAO = 1e-10;
	private static final int ITERACOES_MAXIMAS_PADRAO = 1000;
	private static final double TOLERANCIA_DIAGONAL = 1e-12;

	public double[] resolverSistema(double[][] A, double[] b) {
		ValidadorMatriz.validarSistemaFinito(A, b);
		return resolverSistema(A, b, new double[A.length],
				TOLERANCIA_PADRAO, ITERACOES_MAXIMAS_PADRAO);
	}

	public double[] resolverSistema(double[][] A, double[] b,
									double[] chuteInicial, double tolerancia,
									int maxIteracoes) {
		ValidadorMatriz.validarSistemaFinito(A, b);
		validarParametros(A, chuteInicial, tolerancia, maxIteracoes);

		int n = A.length;
		double[] atual = chuteInicial.clone();

		for (int iteracao = 0; iteracao < maxIteracoes; iteracao++) {
			double[] proximo = new double[n];

			for (int i = 0; i < n; i++) {
				double soma = 0.0;
				for (int j = 0; j < n; j++) {
					if (j != i) {
						soma += A[i][j] * atual[j];
					}
				}

				proximo[i] = (b[i] - soma) / A[i][i];
				if (!Double.isFinite(proximo[i])) {
					throw new ArithmeticException(
							"A iteracao de Jacobi produziu um valor nao finito."
					);
				}
			}

			if (distanciaMaxima(atual, proximo) <= tolerancia) {
				return proximo;
			}
			atual = proximo;
		}

		throw new IllegalStateException(
				"O metodo de Jacobi nao convergiu em " + maxIteracoes + " iteracoes."
		);
	}

	private void validarParametros(double[][] A, double[] chuteInicial,
								   double tolerancia, int maxIteracoes) {
		if (chuteInicial == null || chuteInicial.length != A.length) {
			throw new IllegalArgumentException(
					"O chute inicial deve ter o mesmo tamanho do sistema."
			);
		}
		for (double valor : chuteInicial) {
			if (!Double.isFinite(valor)) {
				throw new IllegalArgumentException(
						"O chute inicial deve conter apenas valores finitos."
				);
			}
		}
		if (!Double.isFinite(tolerancia) || tolerancia <= 0.0) {
			throw new IllegalArgumentException(
					"A tolerancia deve ser um numero finito maior que zero."
			);
		}
		if (maxIteracoes <= 0) {
			throw new IllegalArgumentException(
					"O limite de iteracoes deve ser maior que zero."
			);
		}
		for (int i = 0; i < A.length; i++) {
			if (Math.abs(A[i][i]) < TOLERANCIA_DIAGONAL) {
				throw new IllegalArgumentException(
						"A diagonal principal nao pode conter zeros."
				);
			}
		}
	}

	private double distanciaMaxima(double[] primeiro, double[] segundo) {
		double maior = 0.0;
		for (int i = 0; i < primeiro.length; i++) {
			maior = Math.max(maior, Math.abs(segundo[i] - primeiro[i]));
		}
		return maior;
	}
}
