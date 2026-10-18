package br.edu.ufersa.Financas_Computacionais.features.metodosNumericos;

import br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.dto.CondicionamentoResponseDTO;

/**
 * Porta de entrada da parte de condicionamento dentro da feature
 * metodosNumericos — mesmo padrão de PrecificacaoController e
 * MetodosNumericosController.
 */
public class CondicionamentoController {

    private final CondicionamentoService service;

    public CondicionamentoController() {
        this(new CondicionamentoService());
    }

    public CondicionamentoController(CondicionamentoService service) {
        this.service = service;
    }

    public CondicionamentoResponseDTO calcular(double[][] A) {
        return service.calcular(A);
    }

    public void demonstrar() {
        // Matriz "quase singular" do material da disciplina:
        //   [[1, 1], [1, 1.0001]] com det = 1e-4.
        double[][] A = {
                {1.0, 1.0},
                {1.0, 1.0001}
        };

        CondicionamentoResponseDTO r = service.calcular(A);

        System.out.println("Norma infinito de A:   " + r.normaMatriz());
        System.out.println("Norma infinito de A^-1:" + r.normaInversa());
        System.out.printf("kappa_inf(A): %.4f%n", r.numeroCondicionamento());
        System.out.println("Classificacao: " + r.classificacao());
        System.out.println("Digitos perdidos: " + r.digitosPerdidos());
    }
}