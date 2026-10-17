# Estrutura do projeto

O código está organizado como o Sistema_Web: um pacote por feature, e dentro de
cada feature as mesmas peças.

| Peça | Papel | Equivalente no Sistema_Web |
|---|---|---|
| `*Controller` | Porta de entrada da feature. Chama os Services e mostra o resultado no console. Guarda as demonstrações que antes ficavam no `main` de cada classe. | `*Controller` |
| `*Service` | A lógica: validações e cálculos. Sem `System.out`. | `*Service` |
| `dto/` | Objetos que levam dados para dentro (`*RequestDTO`) e para fora (`*ResponseDTO`) de um Service. | `dto/` |
| `exception/` | Exceções específicas da feature. | `exception/` |
| `shared/` | O que mais de uma feature usa: exceções base, validação, utilitários, modelos. | `shared/` |

```
br.edu.ufersa.Financas_Computacionais
├── Main.java
├── features
│   ├── metodosNumericos   (Controller, Services, dto/, exception/)
│   ├── precificacao       (Controller, Services, dto/, exception/)
│   ├── otimizacao         (Services)
│   ├── interpolacao       (Services)
│   ├── avaliacao          (Services)
│   └── risco              (Services)
└── shared
    ├── exception          (ParametroInvalidoException e derivadas)
    ├── validation         (ValidadorMatriz)
    ├── util               (ImpressaoConsole, SubstituicaoTriangular, Matrizes, Normas)
    └── modelo             (OpcaoFinanceira, CarteiraInvestimentos)
```

`dto/`, `exception/` e `*Controller` só existem nas features que já têm código.
Quem implementar uma das outras cria essas pastas do mesmo jeito.

## Onde foi parar cada classe

| Antes | Agora |
|---|---|
| `metodosNumericos/FatoracaoLU` | `features/metodosNumericos/FatoracaoLUService` |
| `metodosNumericos/FatoracaoCholesky` | `features/metodosNumericos/FatoracaoCholeskyService` |
| `metodosNumericos/{AnaliseEstabilidade, Condicionamento, EliminacaoGauss, GaussSeidel, InterpolacaoLinear, Jacobi, NewtonRaphson, Spline}` | `features/metodosNumericos/<Nome>Service` |
| `metodosNumericos/Matrizes`, `metodosNumericos/Normas` | `shared/util/` (mesmo nome) |
| `precificacao/ArvoreBinomial` | `features/precificacao/ArvoreBinomialService` |
| `precificacao/CoxRossRubinstein` | `features/precificacao/CoxRossRubinsteinService` |
| `precificacao/{BlackScholes, DiferencasFinitas, MonteCarlo}` | `features/precificacao/<Nome>Service` |
| `modelo/ResultadoPrecificacao` | `features/precificacao/dto/ResultadoPrecificacao` |
| `modelo/{OpcaoFinanceira, CarteiraInvestimentos}` | `shared/modelo/` (mesmo nome) |
| `otimizacao/{AlgoritmoGenetico, Markowitz, PSO, ProgramacaoQuadratica}` | `features/otimizacao/<Nome>Service` |
| `interpolacao/{CurvaJuros, VolatilidadeImplicita}` | `features/interpolacao/<Nome>Service` |
| `avaliacao/ComparacaoMetodos` | `features/avaliacao/ComparacaoMetodosService` |
| `risco/ValueAtRisk` | `features/risco/ValueAtRiskService` |

Os comentários de RESPONSÁVEL e ASSUNTO das classes ainda sem código foram mantidos como estavam.

## O que mudou no uso das classes com código

- Os métodos deixaram de ser `static`: cria-se o Service (`new FatoracaoLUService()`) e chama-se o método nele.
- `FatoracaoLU.decomposicaoLU` devolvia `double[][][]`. Agora devolve `FatoracaoLUResponseDTO`, com `L()` e `U()`.
- `ArvoreBinomial.precificarCall/Put` recebiam 8 parâmetros soltos. Agora recebem um `ArvoreBinomialRequestDTO`.
- `CoxRossRubinstein` era criada com `new` e depois consultada pelos getters. Agora `CoxRossRubinsteinService.calcular(request)` devolve um `CoxRossRubinsteinResponseDTO` com os parâmetros da árvore e o preço da call e da put.
- As exceções específicas herdam de `ParametroInvalidoException`, que herda de `IllegalArgumentException`. Quem já capturava `IllegalArgumentException` continua funcionando, e as mensagens são as mesmas de antes.
- Para rodar as demonstrações: `MetodosNumericosController.main` e `PrecificacaoController.main`.

## Diferenças em relação ao Sistema_Web

- Sem Spring: não há anotações, injeção de dependência nem `GlobalExceptionHandler`. Os Controllers recebem os Services pelo construtor, como no modelo.
- Os DTOs são `record` (imutáveis), em vez de classes com getters e setters.
- O pacote se chama `features`. No modelo está escrito `freatures`.
