# Financas Computacionais

Projeto academico em Java para implementar metodos numericos e modelos de financas computacionais. O codigo-fonte esta em `src/main/java`, organizado pelo pacote base `br.edu.ufersa.Financas_Computacionais`.

## Tecnologias e configuracao

- Java configurado no Maven com `source` e `target` 26.
- Maven (`pom.xml`) e usado como sistema de build.
- O projeto nao declara dependencias externas no `pom.xml`.
- Nao ha testes automatizados em `src/test` no estado atual.

## Organizacao dos pacotes

```text
src/main/java/br/edu/ufersa/Financas_Computacionais/
|-- Main.java
|-- avaliacao/
|-- features/
|   |-- avaliacao/
|   |-- interpolacao/
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

Ponto de entrada planejado para integrar os calculos. Atualmente contem apenas o esqueleto comentado; os exemplos executaveis estao nos controllers de cada area.

### `features/`

Contem os servicos que concentram a logica das funcionalidades e os controllers usados para demonstracoes no console.

- `avaliacao/`: `ComparacaoInvestimentosService` calcula VPL, TIR, payback simples e payback descontado, e compara dois projetos pelo VPL. `ComparacaoMetodosService` ainda e um esqueleto.
- `interpolacao/`: servicos de curva de juros e volatilidade implicita; ainda sem implementacao funcional.
- `metodosNumericos/`: LU, Cholesky, Jacobi e Gauss-Seidel implementados. `MetodosNumericosController` demonstra esses quatro metodos. Eliminacao de Gauss, interpolacao linear, Newton-Raphson, splines, condicionamento e analise de estabilidade ainda estao pendentes.
- `otimizacao/`: servicos de Markowitz, programacao quadratica, algoritmo genetico e PSO; ainda sem implementacao funcional.
- `precificacao/`: arvore binomial e Cox-Ross-Rubinstein implementados para precificacao de call e put. `PrecificacaoController` demonstra ambos. Black-Scholes, diferencas finitas e Monte Carlo ainda estao pendentes.
- `risco/`: o servico de Value at Risk ainda e um esqueleto.
- `dto/` e `exception/`: tipos de entrada/saida e excecoes especificas das funcionalidades que os utilizam.

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
- `util/Matrizes` e `util/Normas` existem como esqueletos; ainda nao oferecem operacoes implementadas.

## Executar demonstracoes

As demonstracoes podem ser executadas pelas classes com metodo `main`:

- `br.edu.ufersa.Financas_Computacionais.features.metodosNumericos.MetodosNumericosController`
- `br.edu.ufersa.Financas_Computacionais.features.precificacao.PrecificacaoController`

O `Main` geral ainda nao chama esses controllers nem oferece uma interface unica para entrada de dados.

## Estado e proximos passos

O projeto combina funcionalidades implementadas com esqueletos destinados a expansao. As prioridades estruturais identificadas sao integrar os modulos no ponto de entrada, implementar os servicos ainda vazios e criar testes automatizados para os calculos e validacoes. A lista de pendencias nesta documentacao descreve o codigo atual; requisitos adicionais do roteiro ou dos slides devem ser conferidos separadamente.
