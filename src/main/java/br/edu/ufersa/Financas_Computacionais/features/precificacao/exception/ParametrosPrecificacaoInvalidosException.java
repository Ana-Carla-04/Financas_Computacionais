package br.edu.ufersa.Financas_Computacionais.features.precificacao.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

// Lançada quando um parâmetro de precificação está fora do que o modelo aceita:
// preço inicial ou de exercício não positivo, taxa não finita, vencimento ou
// número de períodos não positivos, fatores u e d inconsistentes, e assim por diante.
public class ParametrosPrecificacaoInvalidosException extends ParametroInvalidoException {

    public ParametrosPrecificacaoInvalidosException(String mensagem) {
        super(mensagem);
    }
}
