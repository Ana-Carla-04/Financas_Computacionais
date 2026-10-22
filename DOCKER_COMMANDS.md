# Executar a API

A aplicacao e uma API REST Spring Boot. Ela escuta em `http://localhost:8080`.

## Testes e build

```powershell
mvn clean test
mvn package
```

Os testes tambem sao executados durante a construcao da imagem (`mvn verify`).

## Testar no Insomnia

Importe a colecao `insomnia/financas-computacionais.insomnia.json` pelo menu
**Import/Export** do Insomnia. Ela inclui requests agrupados por area, exemplos
de sucesso e alguns casos negativos que devem retornar HTTP 400. O ambiente
base usa `http://localhost:8080`.

## Executar localmente

```powershell
mvn spring-boot:run
```

## Executar com Docker Compose

```powershell
docker compose up --build
```

Executar em segundo plano:

```powershell
docker compose up --build -d
```

Ver logs e parar a aplicacao:

```powershell
docker compose logs -f app
docker compose down
```

## Endpoints implementados

- `POST /api/amortizacoes` calcula uma tabela SAC ou PRICE. Se a matriz PRICE for singular, o servico usa a formula escalar como fallback e identifica isso no diagnostico.
- `POST /api/avaliacoes/investimentos/analise` calcula indicadores para um investimento.
- `POST /api/avaliacoes/investimentos/comparacao` compara dois investimentos pela mesma taxa de desconto.
- `POST /api/metodos-numericos/condicionamento` calcula o numero de condicao na norma infinito.
- `POST /api/metodos-numericos/sistemas/lu` resolve um sistema por fatoracao LU.
- `POST /api/metodos-numericos/sistemas/cholesky` resolve um sistema por Cholesky.
- `POST /api/metodos-numericos/sistemas/jacobi` resolve por Jacobi.
- `POST /api/metodos-numericos/sistemas/gauss-seidel` resolve por Gauss-Seidel.

Exemplo de request para amortizacao:

```json
{
  "sistema": "SAC",
  "valorFinanciado": 100000,
  "taxaJuros": 0.01,
  "meses": 12
}
```

Exemplo de request para resolver sistema linear:

```json
{
  "matriz": [[10, -1, 2], [-1, 11, -1], [2, -1, 10]],
  "termosIndependentes": [6, 22, -10]
}
```

Jacobi e Gauss-Seidel aceitam opcionalmente `chuteInicial`, `tolerancia` e `maxIteracoes`; informe tolerancia e limite de iteracoes juntos.

Erros de Bean Validation e parametros invalidos retornam HTTP 400 com JSON contendo a mensagem e, quando aplicavel, os erros por campo.

Os modulos que ainda sao esqueletos nao possuem endpoints.
