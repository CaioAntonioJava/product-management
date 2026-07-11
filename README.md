# Product Management

> Este repositório contém a solução desenvolvida para um desafio técnico de uma vaga de Desenvolvedor Web. O projeto foi construído seguindo princípios de código limpo, arquitetura organizada e boas práticas de desenvolvimento, buscando refletir um ambiente de desenvolvimento profissional.

Este projeto roda **backend (Spring Boot)**, **frontend (React + Vite)** e **MySQL 8** em containers.
O único pré-requisito é ter o **Docker** instalado (com o Docker Compose v2, já embutido no Docker Desktop).

## Como executar

Na raiz do projeto, rode:

```bash
docker compose up --build
```

Aguarde o build e a inicialização (o MySQL é o primeiro a subir e o backend só inicia quando ele está saudável).

Quando terminar, acesse:

| Serviço   | URL                        |
|-----------|----------------------------|
| Frontend  | http://localhost:5173      |
| Backend   | http://localhost:8080/api  |
| MySQL     | localhost:3306 (root/root)  |

O esquema do banco (`product_management`) é criado automaticamente pelo Hibernate (`ddl-auto=update`).

## Parar e limpar

Para apenas parar os containers:

```bash
docker compose down
```

Para parar **e apagar o volume do banco** (dados de volta ao zero):

```bash
docker compose down -v
```

## Estrutura

```
product-management/
├── docker-compose.yml          # Orquestra os 3 serviços
├── backend/
│   ├── Dockerfile              # Build Maven -> JRE 17
│   └── .dockerignore
└── frontend/
    ├── Dockerfile              # Build Vite -> Nginx (porta 5173)
    ├── nginx.conf              # Serve o SPA
    └── .dockerignore
```

## Como rodar os testes

Os testes unitários do backend ficam em `backend/src/test/java` e cobrem serviços, controllers, DTOs, validações e tratamento de exceções. Não exigem banco de dados em execução.

### Pré-requisitos

- **Java 17** (ou superior) — apenas para rodar os testes localmente; o Docker continua dispensável.

### Rodar todos os testes unitários

A partir da raiz do projeto:

**Windows (CMD / PowerShell):**
```bash
cd backend
.\mvnw.cmd test -Dtest='!ProductManagementApplicationTests,*ServiceTest,*ControllerTest,GlobalExceptionHandlerTest,ProductResponseDTOTest,CommaBigDecimalDeserializerTest,DtoValidationTest'
```

**Linux / macOS:**
```bash
cd backend
./mvnw test -Dtest='!ProductManagementApplicationTests,*ServiceTest,*ControllerTest,GlobalExceptionHandlerTest,ProductResponseDTOTest,CommaBigDecimalDeserializerTest,DtoValidationTest'
```

### Rodar uma classe de teste específica

```bash
cd backend
.\mvnw.cmd test -Dtest=CategoryServiceTest
```

### Relatório resumido

Após a execução, o Maven imprime no console um resumo por classe, por exemplo:

```
Tests run: 78, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

O relatório completo em HTML/XML fica disponível em `backend/target/surefire-reports/`.

## Notas

- O frontend se comunica com o backend em `http://localhost:8080/api`. Essa URL é injetada no build via `VITE_API_URL` (com default `http://localhost:8080/api`) e pode ser alterada no `docker-compose.yml`.
- O CORS do backend já está liberado para `http://localhost:5173`.
- Não é necessário instalar Java, Node, Maven ou MySQL — tudo vem nas imagens.