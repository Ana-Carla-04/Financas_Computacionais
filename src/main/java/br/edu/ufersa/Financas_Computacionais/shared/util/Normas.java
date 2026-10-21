package br.edu.ufersa.Financas_Computacionais.shared.util;

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         // Normas vetoriais e matriciais usadas na análise de erro e de condicionamento.
public final class Normas {

    private Normas() {
    }

    // ||x||_1 = soma dos módulos
    public static double vetorial1(double[] x) {
        double soma = 0.0;
        for (double v : x) {
            soma += Math.abs(v);
        }
        return soma;
    }

    // ||x||_2 = raiz da soma dos quadrados (euclidiana)
    public static double vetorial2(double[] x) {
        double soma = 0.0;
        for (double v : x) {
            soma += v * v;
        }
        return Math.sqrt(soma);
    }

    // ||x||_inf = maior módulo
    public static double vetorialInfinito(double[] x) {
        double maior = 0.0;
        for (double v : x) {
            maior = Math.max(maior, Math.abs(v));
        }
        return maior;
    }

    // ||A||_1 = maior soma de módulos por coluna
    public static double matricial1(double[][] A) {
        double maior = 0.0;
        for (int j = 0; j < A[0].length; j++) {
            double soma = 0.0;
            for (double[] linha : A) {
                soma += Math.abs(linha[j]);
            }
            maior = Math.max(maior, soma);
        }
        return maior;
    }

    // ||A||_inf = maior soma de módulos por linha
    public static double matricialInfinito(double[][] A) {
        double maior = 0.0;
        for (double[] linha : A) {
            double soma = 0.0;
            for (double v : linha) {
                soma += Math.abs(v);
            }
            maior = Math.max(maior, soma);
        }
        return maior;
    }

    // ||A||_F = raiz da soma dos quadrados de todos os elementos
    public static double matricialFrobenius(double[][] A) {
        double soma = 0.0;
        for (double[] linha : A) {
            for (double v : linha) {
                soma += v * v;
            }
        }
        return Math.sqrt(soma);
    }
}