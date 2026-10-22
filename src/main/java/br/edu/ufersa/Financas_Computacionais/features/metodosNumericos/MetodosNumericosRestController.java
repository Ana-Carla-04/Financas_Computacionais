package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoNormasResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.ClassificacaoSistemaResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.EliminacaoGaussResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.MatrizRequestDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.NormasResponseDTO;
import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.SistemaLinearRequestDTO;
import br.edu.ufersa.Financas_Computacionais.shared.util.Normas;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        private final EliminacaoGaussService eliminacaoGaussService;

    public MetodosNumericosRestController(FatoracaoLUService fatoracaoLUService,
                                          FatoracaoCholeskyService fatoracaoCholeskyService,
                                          JacobiService jacobiService,
                                          GaussSeidelService gaussSeidelService,
                                                                                  CondicionamentoService condicionamentoService,
                                                                                  EliminacaoGaussService eliminacaoGaussService) {
        this.fatoracaoLUService = fatoracaoLUService;
        this.fatoracaoCholeskyService = fatoracaoCholeskyService;
        this.jacobiService = jacobiService;
        this.gaussSeidelService = gaussSeidelService;
        this.condicionamentoService = condicionamentoService;
        this.eliminacaoGaussService = eliminacaoGaussService;
    }

    @PostMapping("/condicionamento")
    public CondicionamentoResponseDTO calcularCondicionamento(
            @Valid @RequestBody MatrizRequestDTO request) {
        return condicionamentoService.calcular(request.matriz());
    }

        @PostMapping("/condicionamento/{norma}")
        public CondicionamentoNormasResponseDTO calcularCondicionamentoPorNorma(
                    @PathVariable("norma") String norma,
                    @Valid @RequestBody MatrizRequestDTO request) {
                TipoNorma tipoNorma;
                try {
                        tipoNorma = TipoNorma.valueOf(norma.trim().toUpperCase());
                } catch (IllegalArgumentException exception) {
                        throw new IllegalArgumentException("Norma desconhecida. Use UM, INFINITO ou FROBENIUS.");
                }
                return condicionamentoService.calcular(request.matriz(), tipoNorma);
        }

        @PostMapping("/normas")
        public NormasResponseDTO calcularNormas(@Valid @RequestBody MatrizRequestDTO request) {
                double[][] matriz = request.matriz();
                return new NormasResponseDTO(
                                Normas.matricial1(matriz),
                                Normas.matricialInfinito(matriz),
                                Normas.matricialFrobenius(matriz)
                );
        }

        @PostMapping("/classificacao")
        public ClassificacaoSistemaResponseDTO classificarSistema(
                        @Valid @RequestBody SistemaLinearRequestDTO request) {
                return eliminacaoGaussService.classificar(
                                request.matriz(), request.termosIndependentes());
        }

        @PostMapping("/eliminacao-gauss")
        public EliminacaoGaussResponseDTO eliminarGauss(
                        @Valid @RequestBody SistemaLinearRequestDTO request,
                    @RequestParam(name = "pivoteamento", defaultValue = "false") boolean pivoteamento) {
                return eliminacaoGaussService.resolver(
                                request.matriz(), request.termosIndependentes(), pivoteamento);
        }

    @PostMapping("/sistemas/{metodo}")
        public double[] resolverSistema(@PathVariable("metodo") String metodo,
                                    @Valid @RequestBody SistemaLinearRequestDTO request) {
        return switch (metodo.toLowerCase()) {
            case "lu" -> fatoracaoLUService.resolverSistema(
                    request.matriz(), request.termosIndependentes());
            case "cholesky" -> fatoracaoCholeskyService.resolverSistema(
                    request.matriz(), request.termosIndependentes());
            case "gauss" -> eliminacaoGaussService.resolver(
                    request.matriz(), request.termosIndependentes(), false).solucao();
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