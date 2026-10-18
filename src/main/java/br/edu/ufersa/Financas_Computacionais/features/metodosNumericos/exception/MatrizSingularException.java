package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

public class MatrizSingularException extends ParametroInvalidoException {

    public MatrizSingularException() {
        super("A matriz e singular; nao e possivel calcular o "
                + "numero de condicionamento.");
    }
}