
package br.edu.ufersa.Financas_Computacionais.precificacao;

public class CoxRossRubinstein {

    private final double precoInicial;
    private final double precoExercicio;
    private final double tempo;
    private final double taxaJuros;
    private final double volatilidade;
    private final int periodos;

    private final double deltaT;
    private final double subida;
    private final double descida;
    private final double probabilidadeSubida;

    public CoxRossRubinstein(
            double precoInicial,
            double precoExercicio,
            double tempo,
            double taxaJuros,
            double volatilidade,
            int periodos) {

        if (precoInicial <= 0 || precoExercicio <= 0) {
            throw new IllegalArgumentException(
                    "Os preços devem ser positivos.");
        }

        if (tempo <= 0 || volatilidade <= 0 || periodos <= 0) {
            throw new IllegalArgumentException(
                    "Tempo, volatilidade e períodos devem ser positivos.");
        }

        this.precoInicial = precoInicial;
        this.precoExercicio = precoExercicio;
        this.tempo = tempo;
        this.taxaJuros = taxaJuros;
        this.volatilidade = volatilidade;
        this.periodos = periodos;

        this.deltaT = tempo / periodos;
        this.subida = Math.exp(volatilidade * Math.sqrt(deltaT));
        this.descida = 1.0 / subida;

        this.probabilidadeSubida =
                (Math.exp(taxaJuros * deltaT) - descida)
                        / (subida - descida);

        if (probabilidadeSubida < 0 || probabilidadeSubida > 1) {
            throw new IllegalArgumentException(
                    "Probabilidade inválida. Verifique os parâmetros.");
        }
    }

    public double getDeltaT() {
        return deltaT;
    }

    public double getSubida() {
        return subida;
    }

    public double getDescida() {
        return descida;
    }

    public double getProbabilidadeSubida() {
        return probabilidadeSubida;
    }

    public double precificarCall() {
        return precificar(true);
    }

    public double precificarPut() {
        return precificar(false);
    }

    private double precificar(boolean call) {

        double[] valores = new double[periodos + 1];

        // Calcula os valores da opção no vencimento.
        for (int j = 0; j <= periodos; j++) {

            double precoAtivo = precoInicial
                    * Math.pow(subida, j)
                    * Math.pow(descida, periodos - j);

            if (call) {
                valores[j] = Math.max(
                        precoAtivo - precoExercicio, 0);
            } else {
                valores[j] = Math.max(
                        precoExercicio - precoAtivo, 0);
            }
        }

        // Calcula os valores anteriores por indução retroativa.
        double desconto = Math.exp(-taxaJuros * deltaT);

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

    public static void main(String[] args) {

        CoxRossRubinstein modelo = new CoxRossRubinstein(
                100.0,  // Preço atual do ativo
                100.0,  // Preço de exercício
                1.0,    // Tempo até o vencimento (anos)
                0.05,   // Taxa de juros: 5% ao ano
                0.20,   // Volatilidade: 20% ao ano
                100     // Número de períodos
        );

        System.out.println("Modelo Cox-Ross-Rubinstein");
        System.out.println("--------------------------");

        System.out.printf("Delta t: %.6f%n", modelo.getDeltaT());
        System.out.printf("Fator de subida: %.6f%n",
                modelo.getSubida());
        System.out.printf("Fator de descida: %.6f%n",
                modelo.getDescida());
        System.out.printf("Probabilidade de subida: %.6f%n",
                modelo.getProbabilidadeSubida());

        System.out.printf("Preço da opção Call: %.2f%n",
                modelo.precificarCall());

        System.out.printf("Preço da opção Put: %.2f%n",
                modelo.precificarPut());
    }
}

