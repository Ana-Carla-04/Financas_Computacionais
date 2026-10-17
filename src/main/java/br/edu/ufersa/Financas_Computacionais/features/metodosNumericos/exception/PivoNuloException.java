package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.exception;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;

// Lançada pela fatoração LU quando um pivô é nulo, ou muito próximo de zero.
// O método usado não faz pivoteamento, então não consegue continuar.
public class PivoNuloException extends ParametroInvalidoException {

    public PivoNuloException(int posicao) {
        super("Pivo nulo ou muito proximo de zero na posicao "
                + posicao + ". O metodo sem pivoteamento nao pode continuar.");
    }
}
