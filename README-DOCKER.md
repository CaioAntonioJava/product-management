# Product Management — Execução via Docker

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

## Notas

- O frontend se comunica com o backend em `http://localhost:8080/api`. Essa URL é injetada no build via `VITE_API_URL` (com default `http://localhost:8080/api`) e pode ser alterada no `docker-compose.yml`.
- O CORS do backend já está liberado para `http://localhost:5173`.
- Não é necessário instalar Java, Node, Maven ou MySQL — tudo vem nas imagens.