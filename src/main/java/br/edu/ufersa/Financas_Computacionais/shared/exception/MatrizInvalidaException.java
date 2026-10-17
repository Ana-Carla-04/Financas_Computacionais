package br.edu.ufersa.Financas_Computacionais.shared.exception;

// Lançada quando a matriz (ou o vetor do sistema) recebida não serve para o
// cálculo: nula, vazia, não quadrada, com valores não finitos ou com vetor
// de tamanho diferente da matriz.
public class MatrizInvalidaException extends ParametroInvalidoException {

    public MatrizInvalidaException(String mensagem) {
        super(mensagem);
    }
}
