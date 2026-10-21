
public class MenoresQuadrados {
    private final double a;
    private final double b;

    public MenoresQuadrados(double[] x, double[] y) {
        if (x.length != y.length || x.length < 2) {
            throw new IllegalArgumentException(
                    "São necessários pelo menos dois pontos."
            );
        }

        int n = x.length;
        double meanX = 0;
        double meanY = 0;

        for (int i = 0; i < n; i++) {
            if (!Double.isFinite(x[i]) ||
                    !Double.isFinite(y[i])) {
                throw new IllegalArgumentException(
                        "Os dados devem ser finitos."
                );
            }

            meanX += x[i] / n;
            meanY += y[i] / n;
        }

        double numerator = 0;
        double denominator = 0;

        for (int i = 0; i < n; i++) {
            double dx = x[i] - meanX;
            double dy = y[i] - meanY;

            numerator += dx * dy;
            denominator += dx * dx;
        }

        if (denominator == 0) {
            throw new IllegalArgumentException(
                    "Os valores de x não podem ser todos iguais."
            );
        }

        this.a = numerator / denominator;
        this.b = meanY - a * meanX;
    }

    public double predict(double x) {
        return a * x + b;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }
}
