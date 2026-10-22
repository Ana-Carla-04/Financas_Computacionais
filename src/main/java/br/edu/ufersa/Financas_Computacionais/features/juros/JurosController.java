package br.edu.ufersa.Financas_Computacionais.features.juros;

import br.edu.ufersa.Financas_Computacionais.features.juros.dto.EquivalenciaCapitaisResponseDTO;
import br.edu.ufersa.Financas_Computacionais.shared.util.ImpressaoConsole;

/**
 * Porta de entrada da feature juros no console. Mesmo padrão dos demais
 * Controllers do projeto: delega ao Service e apresenta o resultado.
 */
public class JurosController {

    private final JurosService jurosService;
    private final EquivalenciaCapitaisService equivalenciaService;

    public JurosController() {
        this(new JurosService(), new EquivalenciaCapitaisService());
    }

    public JurosController(JurosService jurosService,
                           EquivalenciaCapitaisService equivalenciaService) {
        this.jurosService = jurosService;
        this.equivalenciaService = equivalenciaService;
    }

    public void demonstrar() {
        double capital = 10_000.0;
        double taxa = 0.02;
        double meses = 12;

        System.out.println("=== Juros simples x compostos (C = 10000, i = 2% a.m., n = 12) ===");
        System.out.printf("Montante simples:   R$ %.2f%n", jurosService.montanteSimples(capital, taxa, meses));
        System.out.printf("Montante composto:  R$ %.2f%n", jurosService.montanteComposto(capital, taxa, meses));
        System.out.printf("Taxa anual equivalente a 2%% a.m.: %.4f%%%n",
                100 * jurosService.taxaEquivalente(taxa, 12));

        double vf = jurosService.valorFuturo(capital, taxa, meses, "COMPOSTO");
        System.out.printf("Valor futuro (composto):   R$ %.2f%n", vf);
        System.out.printf("Valor presente de volta:   R$ %.2f%n",
                jurosService.valorPresente(vf, taxa, meses, "COMPOSTO"));

        System.out.println();
        System.out.println("=== Equivalencia de capitais (Gauss + condicionamento) ===");
        System.out.println("As metas sao arredondadas em centavos; veja o quanto isso afeta o resultado.");
        double[] aplicacoes = {4_000.0, 3_000.0};
        double[] prazos = {12, 24};
        demonstrarEquivalencia("Taxas bem diferentes (1% e 3%)",
                new double[]{0.01, 0.03}, prazos, aplicacoes);
        demonstrarEquivalencia("Taxas quase iguais (1% e 1,01%)",
                new double[]{0.01, 0.0101}, prazos, aplicacoes);
    }

    private void demonstrarEquivalencia(String titulo, double[] taxas, double[] prazos,
                                        double[] aplicacoes) {
        double[] metas = new double[taxas.length];
        for (int m = 0; m < metas.length; m++) {
            for (int j = 0; j < taxas.length; j++) {
                metas[m] += aplicacoes[j] * Math.pow(1.0 + taxas[j], prazos[m]);
            }
            metas[m] = Math.round(metas[m] * 100.0) / 100.0;
        }

        EquivalenciaCapitaisResponseDTO r = equivalenciaService.calcular(taxas, prazos, metas);

        System.out.println("--- " + titulo + " ---");
        ImpressaoConsole.imprimirVetor("Metas (centavos)", metas);
        ImpressaoConsole.imprimirVetor("Aplicacoes originais", aplicacoes);
        ImpressaoConsole.imprimirVetor("Aplicacoes recuperadas", r.aplicacoes());
        System.out.printf("kappa_inf = %.4e | %s | digitos perdidos ~ %d%n",
                r.numeroCondicionamento(), r.classificacao(), r.digitosPerdidos());
        System.out.println("Alerta: " + r.alerta());
    }
}