package br.edu.ufersa.Financas_Computacionais.features.avaliacao;

public class ComparacaoInvestimentos {

    private static final ComparacaoInvestimentosService SERVICE =
            new ComparacaoInvestimentosService();

    private ComparacaoInvestimentos() {
    }

    public static ComparacaoInvestimentosService.Comparacao comparar(
            String nomePrimeiro, double[] fluxosPrimeiro,
            String nomeSegundo, double[] fluxosSegundo,
            double taxaDesconto) {
        return SERVICE.comparar(nomePrimeiro, fluxosPrimeiro,
                nomeSegundo, fluxosSegundo, taxaDesconto);
    }
}