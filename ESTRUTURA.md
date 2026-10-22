# Financas Computacionais

Projeto academico em Java para implementar metodos numericos e modelos de financas computacionais. O codigo-fonte esta em `src/main/java`, organizado pelo pacote base `br.edu.ufersa.Financas_Computacionais`.

## Tecnologias e configuracao

- Java configurado para compilar com `release` 25; o build e a imagem usam JDK 26.
- Maven (`pom.xml`) e usado como sistema de build.
- Spring Boot 3.5 fornece a aplicacao web e os endpoints REST.
- Spring Boot Starter Validation habilita Jakarta Bean Validation nos corpos HTTP.
- JUnit 5 e Spring Boot Test cobrem servicos e endpoints.
- Maven Surefire executa os testes; Spring Boot Maven Plugin gera o JAR executavel.

## Organizacao dos pacotes

```text
src/main/java/br/edu/ufersa/Financas_Computacionais/
|-- Main.java
|-- avaliacao/
|-- features/
|   |-- amortizacao/
|   |-- avaliacao/
|   |-- interpolacao/
|   |-- juros/
|   |   |-- dto/
|   |   `-- exception/
|   |-- metodosNumericos/
|   |   |-- dto/
|   |   `-- exception/
|   |-- otimizacao/
|   |-- precificacao/
|   |   |-- dto/
|   |   `-- exception/
|   `-- risco/
|-- interpolacao/
|-- metodosNumericos/
|-- modelo/
|-- otimizacao/
|-- precificacao/
|-- risco/
`-- shared/
	|-- exception/
	|-- util/
	`-- validation/
```

### `Main.java`

Ponto de entrada Spring Boot, anotado com `@SpringBootApplication`. Inicializa a API REST e descobre os controllers e services sob o pacote base.

### `features/`

Contem os servicos de negocio, DTOs, excecoes e controllers. Os controllers `*RestController` atendem HTTP; os controllers antigos de demonstracao continuam disponiveis para execucao manual no console.

- `amortizacao/`: `SistemaAmortizacaoService` calcula tabelas SAC e PRICE e inclui diagnostico de condicionamento. O calculo PRICE ainda falha devido a matriz singular atualmente construida; essa falha esta registrada por teste.
- `avaliacao/`: `ComparacaoInvestimentosService` calcula VPL, TIR, payback simples e payback descontado, e compara dois projetos pelo VPL. `ComparacaoMetodosService` ainda e um esqueleto.
- `interpolacao/`: servicos de curva de juros e volatilidade implicita; ainda sem implementacao funcional.
- `juros/`: `JurosService` calcula juros simples e compostos, valor futuro, valor presente e taxa equivalente. `EquivalenciaCapitaisService` monta e resolve, por eliminacao gaussiana com pivoteamento parcial, o sistema de quanto aplicar em cada conta para atingir metas em datas diferentes, e informa o numero de condicionamento para avaliar a sensibilidade do resultado. `JurosRestController` expoe os calculos por HTTP e `JurosController` traz a demonstracao no console.
- `metodosNumericos/`: LU, Cholesky, Jacobi, Gauss-Seidel e condicionamento implementados. `MetodosNumericosRestController` expoe sistemas lineares e condicionamento por HTTP. Eliminacao gaussiana (com e sem pivoteamento parcial, determinante, inversa e classificacao SPD/SPI/SI), normas matriciais, condicionamento nas normas 1, infinito e Frobenius e analise de estabilidade (`AnaliseEstabilidadeService`) tambem estao implementados. Interpolacao linear, Newton-Raphson e splines ainda estao pendentes.
- `otimizacao/`: servicos de Markowitz, programacao quadratica, algoritmo genetico e PSO; ainda sem implementacao funcional.
- `precificacao/`: arvore binomial e Cox-Ross-Rubinstein implementados para precificacao de call e put e expostos pelo `PrecificacaoRestController`. Black-Scholes, diferencas finitas e Monte Carlo ainda estao pendentes.
- `risco/`: o servico de Value at Risk ainda e um esqueleto.
- `dto/` e `exception/`: tipos de entrada/saida e excecoes especificas das funcionalidades que os utilizam. DTOs recebidos pela API aplicam Jakarta Bean Validation; regras matematicas entre parametros permanecem validadas pelos servicos.

### Endpoints REST

- `POST /api/amortizacoes`: calcula tabela SAC ou PRICE.
- `POST /api/avaliacoes/investimentos/analise`: calcula indicadores de um investimento.
- `POST /api/avaliacoes/investimentos/comparacao`: compara dois investimentos usando a mesma taxa de desconto.
- `POST /api/metodos-numericos/condicionamento`: calcula o numero de condicao na norma infinito.
- `POST /api/metodos-numericos/condicionamento/{norma}`: calcula o numero de condicao em `um`, `infinito` ou `frobenius`.
- `POST /api/metodos-numericos/normas`: retorna as normas 1, infinito e Frobenius da matriz.
- `POST /api/metodos-numericos/eliminacao-gauss?pivoteamento=true|false`: eliminacao gaussiana com passos, trocas de linha, multiplicadores e determinante.
- `POST /api/metodos-numericos/classificacao`: classifica o sistema em SPD, SPI ou SI.
- `POST /api/metodos-numericos/estabilidade`: compara o erro real na solucao com o limitante kappa(A) * erro relativo em b.
- `POST /api/metodos-numericos/sistemas/lu` e `POST /api/metodos-numericos/sistemas/cholesky`: resolvem sistemas por fatoracao.
- `POST /api/metodos-numericos/sistemas/jacobi` e `POST /api/metodos-numericos/sistemas/gauss-seidel`: resolvem sistemas iterativos.
- `POST /api/metodos-numericos/sistemas/gauss` e `POST /api/metodos-numericos/sistemas/gauss-pivoteamento`: resolvem sistemas por eliminacao gaussiana.
- `POST /api/juros/valor-futuro` e `POST /api/juros/valor-presente`: calculam valor futuro e valor presente em juros simples ou compostos.
- `POST /api/juros/equivalencia-capitais`: resolve o sistema de aplicacoes por conta e informa o numero de condicionamento.
- `POST /api/precificacao/arvore-binomial/call` e `POST /api/precificacao/arvore-binomial/put`: precificam opcoes pela arvore binomial.
- `POST /api/precificacao/cox-ross-rubinstein`: retorna os precos CRR de call e put.

Erros de validacao de request e parametros invalidos retornam HTTP 400 em JSON por meio de `shared/web/ApiExceptionHandler`.

### Pacotes de dominio no nivel raiz

Os pacotes `avaliacao/`, `interpolacao/`, `metodosNumericos/`, `modelo/`, `otimizacao/`, `precificacao/` e `risco/` mantem fachadas, tipos de dominio e esqueletos do trabalho. Nem todas as classes nesses pacotes possuem logica implementada.

- `avaliacao/ComparacaoInvestimentos` e uma fachada para o servico em `features/avaliacao/`.
- `metodosNumericos/Jacobi` e `metodosNumericos/GaussSeidel` encaminham chamadas para seus servicos. Os demais algoritmos numericos nesse pacote que nao possuem servico ativo permanecem como trabalho pendente.
- `modelo/` contem os esqueletos de `CarteiraInvestimentos` e `OpcaoFinanceira`.
- Os modelos de precificacao ativos estao nos servicos em `features/precificacao/`; as antigas copias completas de LU, Cholesky, arvore binomial e Cox-Ross-Rubinstein foram removidas para evitar implementacoes paralelas.

### `shared/`

Codigo que pode ser reutilizado por mais de uma funcionalidade:

- `validation/ValidadorMatriz`: validacao de matrizes e sistemas lineares.
- `util/SubstituicaoTriangular`: substituicoes progressiva e regressiva usadas pelas fatoracoes.
- `util/ImpressaoConsole`: formatacao e impressao de matrizes e vetores nas demonstracoes.
- `exception/`: excecoes compartilhadas.
- `util/Matrizes`: copia, produto matriz-vetor e matriz-matriz, transposta, matriz aumentada, troca de linhas e maior modulo.
- `util/Normas`: normas vetoriais 1, 2 e infinito e normas matriciais 1, infinito e Frobenius.

## Executar demonstracoes

Iniciar a API localmente:

```powershell
mvn spring-boot:run
```

Executar a suite automatizada:

```powershell
mvn test
```

Construir e iniciar a aplicacao containerizada:

```powershell
docker compose up --build
```

A API fica disponivel em `http://localhost:8080`. As demonstracoes de console tambem podem ser executadas pelas classes:

- `br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.MetodosNumericosController`
- `br.edu.ufersa.Financas_Computacionais.features.precificacao.PrecificacaoController`
- `br.edu.ufersa.Financas_Computacionais.features.juros.JurosController`

## Estado e proximos passos

O projeto combina funcionalidades REST implementadas com esqueletos destinados a expansao. Os testes cobrem os metodos lineares, eliminacao gaussiana, normas e condicionamento, juros e equivalencia de capitais, comparacao de investimentos, SAC, a falha conhecida de PRICE, inicializacao Spring e validacao HTTP. Os proximos passos incluem implementar os servicos ainda vazios e corrigir a matriz do sistema PRICE. Requisitos adicionais do roteiro ou dos slides devem ser conferidos separadamente.