package br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq;

import java.util.Arrays;

public class SplineCubica {
    private final double[] x;
    private final double[] a, b, c, d;

    public SplineCubica(double[] x, double[] y) {
        if (x.length != y.length || x.length < 2) {
            throw new IllegalArgumentException(
                    "São necessários pelo menos dois pontos."
            );
        }

        int n = x.length;
        this.x = x.clone();

        // Coeficientes de cada segmento
        a = new double[n - 1];
        b = new double[n - 1];
        c = new double[n];
        d = new double[n - 1];

        double[] h = new double[n - 1];

        for (int i = 0; i < n - 1; i++) {
            if (!Double.isFinite(x[i]) ||
                    !Double.isFinite(y[i]) ||
                    x[i + 1] <= x[i]) {
                throw new IllegalArgumentException(
                        "Os x devem ser finitos e estritamente crescentes."
                );
            }
            h[i] = x[i + 1] - x[i];
        }

        if (!Double.isFinite(y[n - 1])) {
            throw new IllegalArgumentException(
                    "Os y devem ser finitos."
            );
        }

        // Sistema linear para calcular c
        double[] alpha = new double[n];
        double[] l = new double[n];
        double[] mu = new double[n];
        double[] z = new double[n];

        for (int i = 1; i < n - 1; i++) {
            alpha[i] =
                    3.0 * (y[i + 1] - y[i]) / h[i]
                            - 3.0 * (y[i] - y[i - 1]) / h[i - 1];
        }

        // Condição natural na primeira extremidade
        l[0] = 1.0;
        mu[0] = 0.0;
        z[0] = 0.0;

        // Eliminação progressiva
        for (int i = 1; i < n - 1; i++) {
            l[i] = 2.0 * (x[i + 1] - x[i - 1])
                    - h[i - 1] * mu[i - 1];

            mu[i] = h[i] / l[i];
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i];
        }

        // Condição natural na última extremidade
        l[n - 1] = 1.0;
        z[n - 1] = 0.0;
        c[n - 1] = 0.0;

        // Retrosubstituição e cálculo dos coeficientes
        for (int j = n - 2; j >= 0; j--) {
            c[j] = z[j] - mu[j] * c[j + 1];

            a[j] = y[j];
            b[j] = (y[j + 1] - y[j]) / h[j]
                    - h[j] * (c[j + 1] + 2.0 * c[j]) / 3.0;

            d[j] = (c[j + 1] - c[j]) / (3.0 * h[j]);
        }
    }

    public double interpolate(double value) {
        if (!Double.isFinite(value) ||
                value < x[0] || value > x[x.length - 1]) {
            throw new IllegalArgumentException(
                    "O valor está fora do intervalo da spline."
            );
        }

        // Encontra o segmento que contém value
        int i = Arrays.binarySearch(x, value);

        if (i >= 0) {
            return i == x.length - 1
                    ? a[i - 1] + b[i - 1] * (value - x[i - 1])
                    + c[i - 1] * Math.pow(value - x[i - 1], 2)
                    + d[i - 1] * Math.pow(value - x[i - 1], 3)
                    : a[i];
        }

        i = -i - 2;

        double dx = value - x[i];

        // Avalia o polinômio cúbico do segmento
        return a[i] + dx * (b[i] + dx * (c[i] + dx * d[i]));
    }
}
