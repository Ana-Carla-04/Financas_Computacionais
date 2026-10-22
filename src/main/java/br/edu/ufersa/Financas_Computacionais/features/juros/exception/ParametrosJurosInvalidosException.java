package br.edu.ufersa.Financas_Computacionais.features.juros.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

public class ParametrosJurosInvalidosException extends ParametroInvalidoException {

    public ParametrosJurosInvalidosException(String mensagem) {
        super(mensagem);
    }

}