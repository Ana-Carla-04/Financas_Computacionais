package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

// Lançada pelo Cholesky quando a matriz não é definida positiva, ou quando o
// cálculo fica numericamente instável.
public class MatrizNaoDefinidaPositivaException extends ParametroInvalidoException {

    public MatrizNaoDefinidaPositivaException() {
        super("A matriz nao e definida positiva "
                + "ou o calculo e numericamente instavel.");
    }
}
