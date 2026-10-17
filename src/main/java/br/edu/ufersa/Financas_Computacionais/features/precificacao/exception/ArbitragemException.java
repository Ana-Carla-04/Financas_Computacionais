package br.edu.ufersa.Financas_Computacionais.features.precificacao.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

// Lançada quando os parâmetros do modelo binomial violam a condição usual de
// ausência de arbitragem: d < exp(r * deltaT) < u.
public class ArbitragemException extends ParametroInvalidoException {

    public ArbitragemException(String mensagem) {
        super(mensagem);
    }
}
