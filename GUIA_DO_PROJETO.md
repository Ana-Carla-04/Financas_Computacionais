# Guia do projeto Finanças Computacionais

Este documento descreve a organização, os cálculos, as APIs, as validações e o estado atual do projeto. Ele foi escrito com base nos arquivos existentes em `src/main/java` e `src/test/java`.

## 1. Objetivo e tecnologias

O projeto é uma aplicação acadêmica de finanças computacionais e métodos numéricos. Ele reúne cálculos financeiros, algoritmos para sistemas lineares e uma API REST para disponibilizar algumas funcionalidades.

- **Java:** o Maven Compiler Plugin está configurado para `release 25`.
- **Maven:** gerencia dependências, compilação, testes e empacotamento.
- **Spring Boot 3.5.6:** inicializa a aplicação web e registra controllers e serviços.
- **Spring Validation:** valida os campos dos pedidos HTTP com Jakarta Bean Validation.
- **JUnit 5 / Spring Boot Test:** usados nos testes automatizados.
- **Docker:** constrói a aplicação em uma etapa Maven e executa o JAR em uma imagem JRE.

O ponto de entrada é `src/main/java/br/edu/ufersa/Financas_Computacionais/Main.java`. A anotação `@SpringBootApplication` ativa a configuração automática e a descoberta dos componentes Spring. O método `main` inicia o servidor.

## 2. Organização do código

O código principal está em `src/main/java/br/edu/ufersa/Financas_Computacionais`.

- `features/`: funcionalidades organizadas por área — amortização, avaliação, interpolação, métodos numéricos, otimização, precificação e risco.
- `shared/`: utilitários, validações, exceções e tratamento de erros compartilhados.
- `dto/`: objetos que representam dados recebidos ou devolvidos pela API.
- `exception/`: exceções específicas de uma funcionalidade.
- `src/test/java`: testes automatizados.

O arquivo `ESTRUTURA.md` também descreve pacotes de domínio no nível raiz, mas esses pacotes não aparecem na listagem atual de arquivos Java. Este guia considera o código que existe atualmente.

## 3. Como uma requisição percorre o sistema

Em geral, uma chamada HTTP segue este fluxo:

1. Um controller REST recebe o pedido e o converte em um DTO.
2. `@Valid` ativa as validações declaradas no DTO.
3. O controller encaminha os dados para um service.
4. O service valida regras matemáticas e executa os cálculos.
5. O resultado é devolvido como DTO e serializado em JSON pelo Spring.
6. Se ocorrer um erro conhecido, `ApiExceptionHandler` produz uma resposta HTTP 400 em formato `ApiError`.

Controllers são a camada de entrada; services concentram os cálculos; DTOs definem o formato dos dados trocados.

## 4. Amortização: SAC e PRICE

### Arquivos envolvidos

- `features/amortizacao/AmortizacaoRestController.java`: recebe `POST /api/amortizacoes` e delega o pedido.
- `features/amortizacao/SistemaAmortizacaoService.java`: valida entradas, monta os dados do sistema, analisa condicionamento e constrói a tabela.
- `features/amortizacao/SistemaAmortizacaoController.java`: fachada para chamadas sem HTTP e demonstração no console.
- `features/amortizacao/SistemaAmortizacao.java`: métodos estáticos de conveniência para criar pedidos SAC ou PRICE.
- `features/amortizacao/dto/AmortizacaoRequestDTO.java`: pedido com sistema, valor financiado, taxa e meses.
- `features/amortizacao/dto/TabelaAmortizacaoDTO.java`: resumo e lista das linhas da tabela.
- `features/amortizacao/dto/AnaliseCondicionamentoDTO.java`: número de condição, classificação, dígitos estimados e mensagem.
- `features/amortizacao/exception/ParametrosAmortizacaoInvalidosException.java`: erro de parâmetros da amortização.

### Fórmulas e montagem da tabela

No **SAC**, a amortização é constante:

\[
A_m = \frac{VP}{n}
\]

Em cada mês, os juros são calculados sobre o saldo inicial, a parcela é a soma dos juros e da amortização, e o saldo final é o saldo inicial menos a amortização. Como o saldo diminui, os juros e as parcelas também diminuem.

No **PRICE**, o código calcula uma parcela fixa pela fórmula:

\[
P = \frac{VP \cdot i}{1-(1+i)^{-n}}
\]

Os juros do mês são calculados sobre o saldo inicial; a amortização é `parcela - juros`; e o saldo final reduz pela amortização. A parcela fica constante, enquanto sua composição muda ao longo do prazo.

### Uso de LU no serviço de amortização

`SistemaAmortizacaoService` chama `FatoracaoLUService` para decompor a matriz `A` em `L` e `U`. Depois resolve `Ly = b` por substituição progressiva e `Ux = y` por substituição regressiva. No SAC, a matriz criada é identidade, e a solução do sistema não é usada para montar a tabela — a tabela SAC é calculada diretamente pelas fórmulas.

### Limitação conhecida do PRICE

O caminho PRICE constrói uma matriz quadrada em que todas as linhas são iguais: para cada linha `t`, cada coluna `k` recebe `(1+i)^(-(k+1))`. Essa matriz tem linhas dependentes e é singular. O serviço tenta calcular o condicionamento antes de resolver LU; portanto, o fluxo atual lança `MatrizSingularException` e não devolve a tabela PRICE.

O código contém a fórmula da parcela e a rotina que montaria a tabela, mas a matriz singular impede o serviço de chegar ao resultado. O teste `priceRegistraFalhaConhecidaDaMatrizSingular` documenta esse comportamento. É necessário corrigir a formulação da matriz/sistema antes de afirmar que PRICE está funcionando via LU.

## 5. Fatoração LU e outros métodos numéricos

### Fatoração LU

`features/metodosNumericos/FatoracaoLUService.java` implementa Doolittle. A fatoração escreve `A = LU`, com `L` triangular inferior e diagonal unitária e `U` triangular superior. Para resolver `Ax=b`, resolve primeiro `Ly=b` e depois `Ux=y`. O código verifica pivôs muito pequenos; esta implementação não faz pivotamento de linhas.

### Cholesky

`FatoracaoCholeskyService.java` implementa uma fatoração apropriada a matrizes simétricas definidas positivas e resolve o sistema com substituições triangulares. Se a matriz não satisfaz as condições matemáticas do método, pode lançar exceções específicas.

### Métodos iterativos

- `JacobiService.java`: calcula aproximações sucessivas para a solução de um sistema linear.
- `GaussSeidelService.java`: também usa iterações, atualizando os valores conforme percorre as equações.

Os dois permitem configurar chute inicial, tolerância e limite de iterações pelas rotinas correspondentes. A convergência depende das propriedades da matriz e dos parâmetros.

### Classes reservadas a métodos ainda pendentes

- `EliminacaoGaussService.java`: espaço para eliminação de Gauss.
- `InterpolacaoLinearService.java`: espaço para interpolação linear.
- `NewtonRaphsonService.java`: espaço para busca iterativa de raízes.
- `SplineService.java`: espaço para interpolação por splines.
- `AnaliseEstabilidadeService.java`: espaço para análise de estabilidade.

Esses arquivos existem, mas não são listados como implementações funcionais na documentação atual do projeto.

## 6. Número de condicionamento e matriz singular

`CondicionamentoService.java` calcula o número de condição na norma infinito:

\[
\kappa_\infty(A)=\|A\|_\infty\,\|A^{-1}\|_\infty
\]

A norma infinito da matriz é a maior soma dos módulos dos elementos de uma linha. A norma infinito de um vetor é o maior módulo entre seus elementos. Para obter a inversa, o serviço reaproveita a fatoração LU e resolve `A x = e_j` para cada coluna `e_j` da identidade.

Um `κ` próximo de 1 indica que a solução tende a ser pouco sensível a pequenas perturbações. Um `κ` elevado indica maior sensibilidade e possível perda de dígitos significativos. Isso é um diagnóstico numérico, não uma medida de risco financeiro.

`MatrizSingularException.java` estende `ParametroInvalidoException`. Seu construtor usa uma mensagem indicando que a matriz é singular e que não é possível calcular seu número de condicionamento. `CondicionamentoService` a lança quando LU encontra pivô nulo durante a inversão ou quando o resultado do condicionamento não é finito.

As classificações usadas pelo cálculo são: abaixo de 10, “Bem condicionada”; abaixo de 1.000, “Aceitável”; abaixo de 1.000.000, “Mal condicionada (atenção)”; a partir daí, “Severamente mal condicionada”. A análise da amortização transforma os valores em mensagens mais descritivas.

## 7. Avaliação de investimentos

- `features/avaliacao/ComparacaoInvestimentosService.java`: analisa fluxos de caixa e compara projetos usando a taxa de desconto. A estrutura do projeto indica cálculo de VPL, TIR, payback simples e descontado; a comparação recomenda o projeto com maior VPL.
- `AvaliacaoRestController.java`: oferece endpoints de análise individual e comparação.
- `ComparacaoInvestimentos.java`: tipo/fachada relacionado à comparação de investimentos.
- `ComparacaoMetodosService.java` e `ComparacaoMetodos.java`: estrutura para comparação de métodos, ainda descrita como incompleta.
- `dto/InvestimentoRequestDTO.java`: dados de um projeto e seus fluxos de caixa.
- `dto/ComparacaoInvestimentosRequestDTO.java`: dados dos dois projetos a comparar.

## 8. Precificação de opções

- `ArvoreBinomialService.java`: calcula opções call e put usando parâmetros de uma árvore binomial.
- `CoxRossRubinsteinService.java`: calcula parâmetros e preços de call e put pelo modelo CRR.
- `PrecificacaoRestController.java`: publica as rotas HTTP de precificação.
- `PrecificacaoController.java`: entrada de demonstração pelo console.
- `BlackScholesService.java`, `DiferencasFinitasService.java` e `MonteCarloService.java`: classes reservadas a abordagens ainda indicadas como pendentes.
- `dto/ArvoreBinomialRequestDTO.java`: parâmetros da árvore e validações, incluindo finitude e limites de probabilidade.
- `dto/CoxRossRubinsteinRequestDTO.java`: dados financeiros do CRR e validações.
- `dto/CoxRossRubinsteinResponseDTO.java`: parâmetros da árvore e preços calculados.
- `dto/ResultadoPrecificacao.java`: classe de resultado atualmente vazia.
- `exception/ArbitragemException.java`: violação da condição de não arbitragem.
- `exception/ParametrosPrecificacaoInvalidosException.java`: parâmetros inválidos para o modelo.
- `exception/ProbabilidadeInvalidaException.java`: probabilidade fora do intervalo permitido.

## 9. Interpolação

- `TipoMetodoInterpolacao.java`: enum para representar tipos de método de interpolação.
- `InterpolacaoController.java`: porta de entrada da funcionalidade.
- `InterpolacaoApplicationService.java`: camada de aplicação que coordena o caso de uso.
- `InterpolacaoDomainService.java`: local previsto para regras matemáticas da interpolação.
- `dto/InterpolacaoRequest.java` e `dto/InterpolacaoResponse.java`: formatos de entrada e saída.

Esta funcionalidade é apresentada como incompleta na documentação do projeto e não consta entre os endpoints ativos.

## 10. Otimização e risco

- `features/otimizacao/MarkowitzService.java`: estrutura para otimização de carteiras pelo modelo de Markowitz.
- `ProgramacaoQuadraticaService.java`: estrutura para programação quadrática.
- `AlgoritmoGeneticoService.java`: estrutura para algoritmo genético.
- `PSOService.java`: estrutura para otimização por enxame de partículas.
- `features/risco/ValueAtRiskService.java`: comentário descreve a finalidade de estimar uma perda de carteira para horizonte e confiança determinados; o cálculo ainda não está implementado.

Os serviços de otimização e risco são tratados como esqueletos, não como recursos REST disponíveis.

## 11. Código compartilhado

- `shared/validation/ValidadorMatriz.java`: valida matrizes quadradas, dimensões de vetores e, nas variantes finitas, rejeita `NaN` e infinito.
- `shared/util/SubstituicaoTriangular.java`: substituição progressiva para sistemas triangulares inferiores; verifica diagonal quase nula.
- `shared/util/ImpressaoConsole.java`: formata matrizes e vetores para saída no terminal.
- `shared/util/Matrizes.java` e `shared/util/Normas.java`: arquivos vazios reservados a futuras operações.
- `shared/exception/ParametroInvalidoException.java`: exceção-base de argumento inválido.
- `shared/exception/MatrizInvalidaException.java`: matriz/vetor com forma ou valores inválidos.
- `shared/exception/DiagonalNulaException.java`: diagonal nula ou quase nula numa substituição triangular.

## 12. DTOs e validação de requisições

DTOs são records ou classes que carregam dados, sem concentrar a lógica principal do cálculo. As anotações Jakarta, como `@Positive`, `@DecimalMin`, `@NotBlank`, `@Pattern` e `@AssertTrue`, são executadas quando o controller recebe um pedido com `@Valid`. O service também valida regras que dependem do modelo, como sistema conhecido, taxa PRICE não nula e valores finitos.

## 13. Tratamento de erros HTTP

`shared/web/ApiExceptionHandler.java` usa `@RestControllerAdvice` para interceptar erros. Erros de validação de campo são agrupados em `validationErrors`; JSON ausente ou malformado recebe uma mensagem própria; exceções de parâmetros inválidos e `IllegalArgumentException` também viram HTTP 400.

O formato de saída é `ApiError`: `timestamp`, `status`, `error`, `message` e `validationErrors`.

## 14. Endpoints REST implementados

| Método | Endpoint | Função |
|---|---|---|
| POST | `/api/amortizacoes` | Recebe SAC ou PRICE e retorna a tabela, se o cálculo completar. PRICE atualmente falha por matriz singular. |
| POST | `/api/avaliacoes/investimentos/analise` | Calcula indicadores de um investimento. |
| POST | `/api/avaliacoes/investimentos/comparacao` | Compara dois investimentos pela taxa de desconto informada pelo primeiro. |
| POST | `/api/metodos-numericos/condicionamento` | Calcula condicionamento na norma infinito. |
| POST | `/api/metodos-numericos/sistemas/lu` | Resolve um sistema por LU. |
| POST | `/api/metodos-numericos/sistemas/cholesky` | Resolve por Cholesky. |
| POST | `/api/metodos-numericos/sistemas/jacobi` | Resolve por Jacobi. |
| POST | `/api/metodos-numericos/sistemas/gauss-seidel` | Resolve por Gauss-Seidel. |
| POST | `/api/precificacao/arvore-binomial/call` | Precifica call pela árvore binomial. |
| POST | `/api/precificacao/arvore-binomial/put` | Precifica put pela árvore binomial. |
| POST | `/api/precificacao/cox-ross-rubinstein` | Devolve resultado CRR de call e put. |

Os endpoints iterativos podem aceitar `chuteInicial`, `tolerancia` e `maxIteracoes` no DTO de sistema linear, conforme as regras de validação desse DTO.

### Exemplo de pedido SAC

```json
{
  "sistema": "SAC",
  "valorFinanciado": 100000,
  "taxaJuros": 0.01,
  "meses": 12
}
```

## 15. Testes existentes

- `SpringApiTest.java`: verifica que o contexto Spring inicia, que uma requisição SAC retorna sucesso e que um valor financiado inválido retorna HTTP 400 com erro de validação.
- `features/amortizacao/SistemaAmortizacaoServiceTest.java`: verifica uma tabela SAC e seu condicionamento igual a 1; também documenta que PRICE lança `MatrizSingularException`.
- `features/metodosNumericos/MetodosNumericosServiceTest.java`: testa soluções conhecidas para LU, Cholesky, Jacobi e Gauss-Seidel, além de conferir condicionamento 1 para a identidade.
- `features/avaliacao/ComparacaoInvestimentosServiceTest.java`: verifica recomendação pelo maior VPL e presença dos resultados de TIR.

## 16. Build e execução

Executar a aplicação localmente:

```powershell
mvn spring-boot:run
```

Executar os testes:

```powershell
mvn test
```

Subir pelo Docker Compose:

```powershell
docker compose up --build
```

O `Dockerfile` compila e empacota com Maven/JDK 26 e executa o JAR em uma imagem JRE Alpine com usuário sem privilégios. O `docker-compose.yml` publica a aplicação na porta local 8080, vinculada a `127.0.0.1`.

## 17. Estado atual e pontos de atenção

O projeto já possui API REST funcional para métodos lineares, análise de investimentos, amortização SAC e precificação binomial/CRR. Interpolação, otimização, VaR e alguns métodos numéricos ainda são estruturas pendentes. O principal problema conhecido no fluxo ativo é o sistema PRICE: sua matriz repetida é singular, então o serviço lança uma exceção antes de retornar a tabela. O condicionamento mede sensibilidade numérica da matriz e ajuda a identificar instabilidade; não corrige a matriz nem valida a qualidade financeira do modelo.

## 18. Aprofundamento: decomposição LU

### Por que decompor uma matriz

Um sistema linear `Ax=b` pode ser difícil de resolver diretamente. Se a matriz for escrita como `A=LU`, em que `L` é triangular inferior e `U` é triangular superior, a solução pode ser obtida com duas substituições simples:

1. Resolver `Ly=b` para obter um vetor intermediário `y`.
2. Resolver `Ux=y` para obter a solução `x` original.

Isso é útil quando há vários vetores `b` para a mesma matriz `A`: a fatoração é feita uma vez e as substituições são repetidas para cada novo vetor.

### Algoritmo Doolittle no arquivo

Em `FatoracaoLUService.decomposicaoLU`, o tamanho da matriz determina a dimensão das matrizes `L` e `U`. O código inicializa ambas com zeros e coloca 1 em toda a diagonal de `L`. Esse é o padrão de Doolittle: os pivôs ficam em `U`, enquanto a diagonal de `L` é unitária.

Para cada posição diagonal `i`, o algoritmo executa duas etapas:

1. **Linha `i` de U:** para cada coluna `j` a partir de `i`, calcula `U[i][j] = A[i][j] - soma`. A soma remove as contribuições já explicadas pelas linhas anteriores de `L` e `U`.
2. **Coluna `i` de L:** para cada linha `j` abaixo de `i`, calcula `L[j][i] = (A[j][i] - soma) / U[i][i]`.

Em notação matemática, as relações são:

\[
u_{ij}=a_{ij}-\sum_{k=0}^{i-1}l_{ik}u_{kj},\qquad
l_{ji}=\frac{a_{ji}-\sum_{k=0}^{i-1}l_{jk}u_{ki}}{u_{ii}},\qquad l_{ii}=1.
\]

Antes de dividir pela diagonal de `U`, o serviço testa se o pivô tem módulo menor que `1e-12`. Se tiver, lança `PivoNuloException`. Ao concluir, retorna `L` e `U` em `FatoracaoLUResponseDTO`.

### Substituição progressiva e regressiva

`substituicaoProgressiva` resolve `Ly=b` linha por linha de cima para baixo. Os valores anteriores de `y` já são conhecidos:

\[
y_i=\frac{b_i-\sum_{k=0}^{i-1}L_{ik}y_k}{L_{ii}}.
\]

O cálculo compartilhado está em `SubstituicaoTriangular.progressiva`; a diagonal nula/quase nula causa `DiagonalNulaException`.

`substituicaoRegressiva` resolve `Ux=y` de baixo para cima, pois os valores posteriores de `x` já são conhecidos:

\[
x_i=\frac{y_i-\sum_{k=i+1}^{n-1}U_{ik}x_k}{U_{ii}}.
\]

`resolverSistema` reúne validação, fatoração e as duas substituições. A validação de forma e dimensão é feita por `ValidadorMatriz`.

### Limitação: ausência de pivotamento

Esta implementação não troca linhas para escolher um pivô maior. Portanto, pode falhar com pivô nulo para algumas matrizes invertíveis, dependendo da ordem das linhas, e pivôs pequenos podem aumentar erros de arredondamento. LU com pivotamento parcial geralmente registra uma matriz de permutação `P` e escreve `PA=LU`. O serviço atual usa `A=LU`, sem essa etapa. A tolerância fixa `1e-12` também não considera a escala dos dados.

## 19. Aprofundamento: número de condicionamento

### O que ele mede

O número de condicionamento caracteriza a sensibilidade do problema associado a uma matriz. Para uma matriz invertível, o projeto usa a norma infinito:

\[
\|A\|_\infty=\max_i\sum_j|a_{ij}|,\qquad
\kappa_\infty(A)=\|A\|_\infty\,\|A^{-1}\|_\infty.
\]

`CondicionamentoService.normaInfinito(double[][])` soma os módulos em cada linha e escolhe a maior soma. `normaInfinito(double[])` escolhe o maior módulo do vetor. Para calcular a inversa, o serviço resolve `Ax=e_j` para cada coluna canônica `e_j` da identidade. As soluções formam as colunas de `A^-1`; a fatoração LU é reutilizada durante essas resoluções.

`κ` perto de 1 sugere baixa sensibilidade. `κ` grande significa que pequenas perturbações relativas na matriz ou no vetor independente podem provocar mudanças relativamente maiores na solução. Como regra intuitiva, o erro relativo pode ser amplificado por um fator da ordem de `κ`, sob as hipóteses usuais. Isso não prediz o erro exato de uma execução e não substitui testes de implementação.

O código calcula `floor(log10(max(κ,1)))` como estimativa de dígitos decimais que podem ser perdidos. É uma estimativa aproximada. As faixas de classificação são: `<10` bem condicionada; `<10^3` aceitável; `<10^6` mal condicionada com atenção; valores maiores severamente mal condicionados.

### Matriz singular e matriz mal condicionada

Uma matriz **singular** não tem inversa e o sistema não possui solução única; seu número de condição não é finito. Uma matriz **mal condicionada** ainda é invertível, mas sua solução é sensível a perturbações. Portanto, singularidade e mau condicionamento são conceitos relacionados, mas diferentes.

`CondicionamentoService.inversa` captura `PivoNuloException` durante a LU e lança `MatrizSingularException`. Também lança essa exceção se o produto das normas não resultar em um número finito. Essa exceção estende `ParametroInvalidoException`, então o handler REST a converte em HTTP 400 com a mensagem de erro.

### Ligação com amortização

Em `SistemaAmortizacaoService.calcular`, a sequência é: validar o pedido; normalizar o nome do sistema; montar a matriz; calcular seu condicionamento; montar o vetor `b`; resolver por LU; montar as linhas da tabela; consolidar totais e diagnóstico. Como a análise vem antes da solução, uma matriz singular interrompe o fluxo antes de produzir uma tabela.

No **SAC**, a matriz criada é a identidade `I`. A norma infinito de `I` e de `I^-1` é 1, então `κ∞(I)=1`. Para `VP=100.000`, taxa mensal de 1% e 3 meses, o código produz amortização constante de `33.333,33`, juros de `1.000`, `666,67` e `333,33`, e parcelas aproximadas de `34.333,33`, `34.000,00` e `33.666,67`. O vetor `b` contém essas parcelas. Embora o serviço execute a LU de `I x=b`, a tabela SAC é depois montada novamente pelas fórmulas, sem utilizar a solução retornada. O condicionamento 1 descreve a matriz identidade usada, não é uma validação geral da correção financeira.

No **PRICE**, a parcela é calculada pela fórmula financeira `P=VP*i/(1-(1+i)^(-n))`. A matriz atual, no entanto, copia os mesmos fatores de desconto para cada linha. Todas as linhas ficam idênticas; para `n>1`, a matriz é singular. Como o condicionamento tenta obter sua inversa antes de chegar à LU de resolução e à montagem da tabela, ocorre `MatrizSingularException`. A fórmula e o método que monta as linhas existem, mas a execução atual não chega até eles. A matriz precisa ser reformulada para representar equações independentes, ou o cálculo PRICE deve usar diretamente a fórmula escalar da parcela. O condicionamento pode diagnosticar a matriz que foi construída, mas não corrige uma formulação incorreta.
