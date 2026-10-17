package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

// Lançada pelo Cholesky quando a matriz não é simétrica.
public class MatrizNaoSimetricaException extends ParametroInvalidoException {

    public MatrizNaoSimetricaException() {
        super("A matriz deve ser simetrica.");
    }
}
