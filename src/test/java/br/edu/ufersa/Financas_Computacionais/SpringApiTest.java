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
}