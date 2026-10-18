package br.edu.ufersa.Financas_Computacionais.features.amortizacao.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

public class ParametrosAmortizacaoInvalidosException extends ParametroInvalidoException {

    public ParametrosAmortizacaoInvalidosException(String mensagem) {
        super(mensagem);
    }
}