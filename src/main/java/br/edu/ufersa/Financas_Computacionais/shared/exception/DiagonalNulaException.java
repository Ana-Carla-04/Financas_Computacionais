package br.edu.ufersa.Financas_Computacionais.shared.exception;

// Lançada quando uma substituição (progressiva ou regressiva) encontra um
// elemento nulo, ou quase nulo, na diagonal da matriz triangular.
public class DiagonalNulaException extends ParametroInvalidoException {

    public DiagonalNulaException(String mensagem) {
        super(mensagem);
    }
}
