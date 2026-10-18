# Comandos Docker

Execute estes comandos na raiz do projeto, onde está o `docker-compose.yml`.

## Executar os testes

Os testes automatizados rodam durante o build da imagem (`mvn verify`). Para
executa-los diretamente no container de build:

```powershell
docker compose build app
```

## Executar as demonstracoes

A imagem inicia o `MetodosNumericosController`, que demonstra LU, Cholesky,
Jacobi e Gauss-Seidel. O container e um programa de console, nao um servidor
HTTP; os resultados aparecem nos logs e o processo termina ao fim da execucao.

```powershell
docker compose run --rm app
```

Para executar as demonstracoes de precificacao pela imagem:

```powershell
docker compose run --rm --entrypoint java app -cp /app/app.jar br.edu.ufersa.Financas_Computacionais.features.precificacao.PrecificacaoController
```

## Construir e iniciar

Reconstruir a imagem e executar em primeiro plano:

```powershell
docker compose up --build
```

Executar em segundo plano:

```powershell
docker compose up --build -d
```

Iniciar usando a imagem ja construida:

```powershell
docker compose up
```

Executar sem o Compose:

```powershell
docker run --rm financas-computacionais:local
```

## Verificar o estado

```powershell
docker compose ps
docker compose logs app
```

Como o container e um programa de console de execucao curta, ele normalmente
termina depois de imprimir os resultados; nao e esperado que permaneça ativo.

## Parar e remover

```powershell
docker compose down
```
