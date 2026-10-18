package br.edu.ufersa.Financas_Computacionais.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.JacobiService;

public class Jacobi {

	private static final JacobiService SERVICE = new JacobiService();

	public static double[] resolverSistema(double[][] A, double[] b) {
		return SERVICE.resolverSistema(A, b);
	}

	public static double[] resolverSistema(double[][] A, double[] b,
										   double[] chuteInicial,
										   double tolerancia, int maxIteracoes) {
		return SERVICE.resolverSistema(A, b, chuteInicial, tolerancia, maxIteracoes);
	}
}
