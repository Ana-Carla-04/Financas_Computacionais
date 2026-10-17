package br.edu.ufersa.Financas_Computacionais.features.precificacao.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

// Lançada quando a probabilidade neutra ao risco sai do intervalo de 0 a 1.
public class ProbabilidadeInvalidaException extends ParametroInvalidoException {

    public ProbabilidadeInvalidaException(String mensagem) {
        super(mensagem);
    }
}
