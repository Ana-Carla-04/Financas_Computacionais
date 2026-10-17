package br.edu.ufersa.Financas_Computacionais.shared.exception;

// Base de todas as exceções de entrada inválida do projeto.
// Herda de IllegalArgumentException de propósito: quem já tratava
// IllegalArgumentException continua funcionando, e as exceções específicas
// de cada feature passam a dizer qual foi o problema.
public class ParametroInvalidoException extends IllegalArgumentException {

    public ParametroInvalidoException(String mensagem) {
        super(mensagem);
    }
}
