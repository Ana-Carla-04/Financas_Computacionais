package br.edu.ufersa.Financas_Computacionais;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners(
  listeners = {
    ServletTestExecutionListener.class,
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class
  },
  mergeMode = MergeMode.REPLACE_DEFAULTS
)
class SpringApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextoSpringIniciaECalculaSAC() throws Exception {
        mockMvc.perform(post("/api/amortizacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sistema": "SAC",
                                  "valorFinanciado": 100000,
                                  "taxaJuros": 0.01,
                                  "meses": 3
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sistema").value("SAC"))
                .andExpect(jsonPath("$.linhas.length()").value(3));
    }

    @Test
    void beanValidationRetorna400ParaValorFinanciadoInvalido() throws Exception {
        mockMvc.perform(post("/api/amortizacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sistema": "SAC",
                                  "valorFinanciado": 0,
                                  "taxaJuros": 0.01,
                                  "meses": 3
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.valorFinanciado").exists());
    }
    @Test
    void eliminacaoGaussComPivoteamentoRetornaPassos() throws Exception {
        mockMvc.perform(post("/api/metodos-numericos/eliminacao-gauss?pivoteamento=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "matriz": [[0, 1, 1], [2, 1, -1], [1, 3, -1]],
                                  "termosIndependentes": [5, 1, 4]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pivoteamento").value(true))
                .andExpect(jsonPath("$.trocas.length()").value(2))
                .andExpect(jsonPath("$.solucao.length()").value(3));
    }

    @Test
    void gaussSemPivoteamentoRetorna400QuandoPivoENulo() throws Exception {
        mockMvc.perform(post("/api/metodos-numericos/sistemas/gauss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "matriz": [[0, 1, 1], [2, 1, -1], [1, 3, -1]],
                                  "termosIndependentes": [5, 1, 4]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void classificaSistemaImpossivel() throws Exception {
        mockMvc.perform(post("/api/metodos-numericos/classificacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "matriz": [[1, 1], [2, 2]],
                                  "termosIndependentes": [2, 5]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("SI"));
    }

    @Test
    void calculaNormasDaMatriz() throws Exception {
        mockMvc.perform(post("/api/metodos-numericos/normas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "matriz": [[4, -2], [1, 3]] }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.norma1").value(5.0))
                .andExpect(jsonPath("$.normaInfinito").value(6.0))
                .andExpect(jsonPath("$.normaFrobenius").exists());
    }

    @Test
    void calculaCondicionamentoNaNormaDeFrobenius() throws Exception {
        mockMvc.perform(post("/api/metodos-numericos/condicionamento/frobenius")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "matriz": [[2, 0], [0, 4]] }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.norma").value("FROBENIUS"))
                .andExpect(jsonPath("$.numeroCondicionamento").exists());
    }

    @Test
    void calculaValorFuturoSimples() throws Exception {
        mockMvc.perform(post("/api/juros/valor-futuro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "regime": "SIMPLES",
                                  "valor": 1000,
                                  "taxa": 0.25,
                                  "periodos": 4
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value(2000.0))
                .andExpect(jsonPath("$.juros").value(1000.0));
    }

    @Test
    void jurosRetorna400ParaRegimeInvalido() throws Exception {
        mockMvc.perform(post("/api/juros/valor-presente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "regime": "OUTRO",
                                  "valor": 1000,
                                  "taxa": 0.02,
                                  "periodos": 12
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.regime").exists());
    }

    @Test
    void equivalenciaDeCapitaisRetornaCondicionamento() throws Exception {
        mockMvc.perform(post("/api/juros/equivalencia-capitais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "taxas": [0.01, 0.0101],
                                  "prazos": [12, 24],
                                  "metas": [5000, 9000]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacoes.length()").value(2))
                .andExpect(jsonPath("$.numeroCondicionamento").exists())
                .andExpect(jsonPath("$.alerta").exists());
    }

                @Test
                void endpointsDeJurosValorPresenteEEquivalenciaRespondem() throws Exception {
              mockMvc.perform(post("/api/juros/valor-presente")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                    {"regime":"COMPOSTO","valor":121,"taxa":0.1,"periodos":2}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").exists());

              mockMvc.perform(post("/api/juros/equivalencia-capitais")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                    {"taxas":[0.05],"prazos":[2],"metas":[110.25]}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacoes.length()").value(1));
                }

                @Test
                void interpolacaoAtendeLagrangeNewtonERejeitaPontosInvalidos() throws Exception {
              String pontos = """
                {"prazos":[1,2],"taxas":[0.1,0.2],"prazoDesejado":1.5,"metodo":"%s"}
                """;

              for (String metodo : new String[]{"LAGRANGE", "NEWTON"}) {
                  mockMvc.perform(post("/api/v1/interpolacao")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(pontos.formatted(metodo)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.metodo").value(metodo))
                    .andExpect(jsonPath("$.taxaEstimada").exists());
              }

              mockMvc.perform(post("/api/v1/interpolacao")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                    {"prazos":[1,1],"taxas":[0.1,0.2],"prazoDesejado":1.5,"metodo":"NEWTON"}
                    """))
                .andExpect(status().isBadRequest());
                }

                @Test
                void analiseEComparacaoDeInvestimentosRespondem() throws Exception {
              mockMvc.perform(post("/api/avaliacoes/investimentos/analise")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                    {"nome":"Projeto A","fluxosCaixa":[-1000,600,600],"taxaDesconto":0.1}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vpl").exists());

              mockMvc.perform(post("/api/avaliacoes/investimentos/comparacao")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                    {
                      "primeiro":{"nome":"A","fluxosCaixa":[-1000,600,600],"taxaDesconto":0.1},
                      "segundo":{"nome":"B","fluxosCaixa":[-1000,700,500],"taxaDesconto":0.1}
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primeiro.vpl").exists())
                .andExpect(jsonPath("$.recomendacao").exists());
                }

                @Test
                void metodosNumericosAtendemSolversNormasECondicionamento() throws Exception {
              String sistemaDiagonalDominante = """
                {"matriz":[[10,-1,2],[-1,11,-1],[2,-1,10]],"termosIndependentes":[6,22,-10]}
                """;
              for (String metodo : new String[]{"lu", "jacobi", "gauss-seidel"}) {
                  mockMvc.perform(post("/api/metodos-numericos/sistemas/" + metodo)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(sistemaDiagonalDominante))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0]").exists())
                    .andExpect(jsonPath("$[1]").exists())
                    .andExpect(jsonPath("$[2]").exists());
              }

              mockMvc.perform(post("/api/metodos-numericos/sistemas/cholesky")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                    {"matriz":[[4,1],[1,3]],"termosIndependentes":[1,2]}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());

              String matrizIdentidade = "{\"matriz\":[[1,0],[0,1]]}";
              mockMvc.perform(post("/api/metodos-numericos/condicionamento")
                  .contentType(MediaType.APPLICATION_JSON).content(matrizIdentidade))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroCondicionamento").value(1.0));
              for (String norma : new String[]{"um", "infinito", "frobenius"}) {
                  mockMvc.perform(post("/api/metodos-numericos/condicionamento/" + norma)
                      .contentType(MediaType.APPLICATION_JSON).content(matrizIdentidade))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.numeroCondicionamento").exists());
              }
                }

                @Test
                void priceRetornaTabelaUsandoFallbackQuandoLUFalha() throws Exception {
              mockMvc.perform(post("/api/amortizacoes")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("""
                    {"sistema":"PRICE","valorFinanciado":100000,"taxaJuros":0.01,"meses":3}
                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.linhas.length()").value(3))
                .andExpect(jsonPath("$.condicionamento.classificacao")
                  .value("Matriz Singular (Fallback)"));
                }
}