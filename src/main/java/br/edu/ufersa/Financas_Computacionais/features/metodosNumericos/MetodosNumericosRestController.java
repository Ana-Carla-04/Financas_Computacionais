package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.MatrizRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.SistemaLinearRequestDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metodos-numericos")
public class MetodosNumericosRestController {

    private final FatoracaoLUService fatoracaoLUService;
    private final FatoracaoCholeskyService fatoracaoCholeskyService;
    private final JacobiService jacobiService;
    private final GaussSeidelService gaussSeidelService;
    private final CondicionamentoService condicionamentoService;

    public MetodosNumericosRestController(FatoracaoLUService fatoracaoLUService,
                                          FatoracaoCholeskyService fatoracaoCholeskyService,
                                          JacobiService jacobiService,
                                          GaussSeidelService gaussSeidelService,
                                          CondicionamentoService condicionamentoService) {
        this.fatoracaoLUService = fatoracaoLUService;
        this.fatoracaoCholeskyService = fatoracaoCholeskyService;
        this.jacobiService = jacobiService;
        this.gaussSeidelService = gaussSeidelService;
        this.condicionamentoService = condicionamentoService;
    }

    @PostMapping("/condicionamento")
    public CondicionamentoResponseDTO calcularCondicionamento(
            @Valid @RequestBody MatrizRequestDTO request) {
        return condicionamentoService.calcular(request.matriz());
    }

    @PostMapping("/sistemas/{metodo}")
    public double[] resolverSistema(@PathVariable String metodo,
                                    @Valid @RequestBody SistemaLinearRequestDTO request) {
        return switch (metodo.toLowerCase()) {
            case "lu" -> fatoracaoLUService.resolverSistema(
                    request.matriz(), request.termosIndependentes());
            case "cholesky" -> fatoracaoCholeskyService.resolverSistema(
                    request.matriz(), request.termosIndependentes());
            case "jacobi" -> request.tolerancia() == null
                    ? jacobiService.resolverSistema(request.matriz(), request.termosIndependentes())
                    : jacobiService.resolverSistema(request.matriz(), request.termosIndependentes(),
                    request.chuteInicial() == null
                            ? new double[request.matriz().length] : request.chuteInicial(),
                    request.tolerancia(), request.maxIteracoes());
            case "gauss-seidel" -> request.tolerancia() == null
                    ? gaussSeidelService.resolverSistema(
                    request.matriz(), request.termosIndependentes())
                    : gaussSeidelService.resolverSistema(request.matriz(),
                    request.termosIndependentes(),
                    request.chuteInicial() == null
                            ? new double[request.matriz().length] : request.chuteInicial(),
                    request.tolerancia(), request.maxIteracoes());
            default -> throw new IllegalArgumentException(
                    "Metodo desconhecido. Use lu, cholesky, jacobi ou gauss-seidel.");
        };
    }
}