package br.edu.ufersa.Financas_Computacionais.features.juros;

import br.edu.ufersa.Financas_Computacionais.features.juros.dto.JurosRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.dto.JurosResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.juros.exception.ParametrosJurosInvalidosException;
import org.springframework.stereotype.Service;

/**
 * Juros simples e compostos, valor futuro e valor presente.
 *
 *   Simples:  M = C (1 + i n)        Composto: M = C (1 + i)^n
 *   Valor presente = inverso do valor futuro, no mesmo regime.
 *
 * A taxa i é decimal (0,02 = 2%) e está na mesma unidade do prazo n.
 */
@Service
public class JurosService {

    public double jurosSimples(double capital, double taxa, double periodos) {
        validar(capital, taxa, periodos);
        return capital * taxa * periodos;
    }

    public double montanteSimples(double capital, double taxa, double periodos) {
        validar(capital, taxa, periodos);
        return conferir(capital * (1.0 + taxa * periodos));
    }

    public double montanteComposto(double capital, double taxa, double periodos) {
        validar(capital, taxa, periodos);
        return conferir(capital * Math.pow(1.0 + taxa, periodos));
    }

    public double jurosCompostos(double capital, double taxa, double periodos) {
        return montanteComposto(capital, taxa, periodos) - capital;
    }

    public double valorFuturo(double valorPresente, double taxa, double periodos, String regime) {
        return ehSimples(regime)
                ? montanteSimples(valorPresente, taxa, periodos)
                : montanteComposto(valorPresente, taxa, periodos);
    }

    public double valorPresente(double valorFuturo, double taxa, double periodos, String regime) {
        validar(valorFuturo, taxa, periodos);
        double fator = ehSimples(regime)
                ? 1.0 + taxa * periodos
                : Math.pow(1.0 + taxa, periodos);
        return conferir(valorFuturo / conferir(fator));
    }

    // Taxa equivalente composta: (1 + i)^fator - 1. Ex.: mensal para anual, fator = 12.
    public double taxaEquivalente(double taxa, double fator) {
        if (!Double.isFinite(taxa) || taxa <= -1.0 || !Double.isFinite(fator) || fator <= 0.0) {
            throw new ParametrosJurosInvalidosException(
                    "A taxa deve ser maior que -1 e o fator deve ser positivo.");
        }
        return conferir(Math.pow(1.0 + taxa, fator)) - 1.0;
    }

    public JurosResponseDTO calcularValorFuturo(JurosRequestDTO request) {
        double vf = valorFuturo(request.valor(), request.taxa(), request.periodos(), request.regime());
        return new JurosResponseDTO("VALOR_FUTURO", request.regime().toUpperCase(),
                request.valor(), request.taxa(), request.periodos(), vf, vf - request.valor());
    }

    public JurosResponseDTO calcularValorPresente(JurosRequestDTO request) {
        double vp = valorPresente(request.valor(), request.taxa(), request.periodos(), request.regime());
        return new JurosResponseDTO("VALOR_PRESENTE", request.regime().toUpperCase(),
                request.valor(), request.taxa(), request.periodos(), vp, request.valor() - vp);
    }

    private boolean ehSimples(String regime) {
        if ("SIMPLES".equalsIgnoreCase(regime)) {
            return true;
        }
        if ("COMPOSTO".equalsIgnoreCase(regime)) {
            return false;
        }
        throw new ParametrosJurosInvalidosException("O regime deve ser SIMPLES ou COMPOSTO.");
    }

    private void validar(double valor, double taxa, double periodos) {
        if (!Double.isFinite(valor) || valor <= 0.0) {
            throw new ParametrosJurosInvalidosException("O valor deve ser positivo.");
        }
        if (!Double.isFinite(taxa) || taxa < 0.0) {
            throw new ParametrosJurosInvalidosException("A taxa nao pode ser negativa.");
        }
        if (!Double.isFinite(periodos) || periodos < 0.0) {
            throw new ParametrosJurosInvalidosException("O numero de periodos nao pode ser negativo.");
        }
    }

    private double conferir(double resultado) {
        if (!Double.isFinite(resultado)) {
            throw new ParametrosJurosInvalidosException("O resultado excede o limite numerico.");
        }
        return resultado;
    }
}