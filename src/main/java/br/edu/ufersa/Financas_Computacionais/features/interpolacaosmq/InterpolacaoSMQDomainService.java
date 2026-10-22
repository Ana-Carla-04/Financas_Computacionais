package br.edu.ufersa.Financas_Computacionais.features.interpolacaosmq;
import org.springframework.stereotype.Service;

@Service
public class InterpolacaoSMQDomainService {

    public double calcular(TipoMetodoInterpolacao metodo,
                           double[] x,
                           double[] y,
                           double valor) {
        return switch (metodo) {
            case SPLINE_CUBICA -> new SplineCubica(x, y).interpolate(valor);
            case MENORES_QUADRADOS -> new MenoresQuadrados(x, y).predict(valor);
        };
    }
}


