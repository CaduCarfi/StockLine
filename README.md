# 💊 StockLine

Sistema Full Stack para gerenciamento de medicamentos e controle de estoque, composto por uma API REST em **Java 21 / Spring Boot 4** e um frontend em **Angular 21**, com **MongoDB** como banco de dados.

---

## 📑 Índice

- [Sobre o projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Modelo de dados](#-modelo-de-dados)
- [Pré-requisitos](#-pré-requisitos)
- [Como configurar as variáveis de ambiente](#-como-configurar-as-variáveis-de-ambiente)
- [Como instalar as dependências](#-como-instalar-as-dependências)
- [Como executar o projeto](#-como-executar-o-projeto)
- [Como executar com Docker](#-como-executar-com-docker)
- [Como executar os testes](#-como-executar-os-testes)
- [Documentação da API (Swagger)](#-documentação-da-api-swagger)
- [Endpoints](#-endpoints)
- [Validações](#-validações)
- [Tratamento de erros](#-tratamento-de-erros)
- [Integração contínua](#-integração-contínua)
- [Problemas comuns](#-problemas-comuns)
- [Estratégia de branches](#-estratégia-de-branches)
- [Objetivos do projeto](#-objetivos-do-projeto)
- [Licença](#-licença)

---

## 📋 Sobre o projeto

O **StockLine** é uma aplicação Full Stack voltada para o gerenciamento de medicamentos. A API permite:

- Cadastrar medicamentos
- Listar todos os medicamentos
- Buscar um medicamento por ID
- Editar um medicamento
- Ajustar a quantidade em estoque (entrada e baixa)
- Excluir um medicamento
- Persistir os dados em uma coleção MongoDB

---

## 🚀 Tecnologias

### Backend (`back/`)

| Tecnologia | Versão | Utilização |
|---|---|---|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 4.1.1 | Framework principal |
| **Spring Web MVC** | — | Desenvolvimento da API REST |
| **Spring Data MongoDB** | — | Persistência dos dados |
| **Spring Validation** | — | Validação dos dados de entrada |
| **MongoDB** | 7 | Banco de dados NoSQL |
| **Lombok** | — | Redução de código boilerplate |
| **SpringDoc OpenAPI** | 2.8.14 | Documentação interativa (Swagger UI) |
| **spring-dotenv** | 4.0.0 | Leitura de variáveis do arquivo `.env` |
| **JUnit 5** | — | Testes automatizados |
| **Mockito** | — | Mocking em testes unitários |
| **JaCoCo** | 0.8.13 | Análise de cobertura de testes |

### Frontend (`front/`)

| Tecnologia | Versão | Utilização |
|---|---|---|
| **Angular** | 21 | Framework frontend |
| **TypeScript** | ~5.9 | Linguagem principal |
| **SCSS** | — | Estilização |
| **Vitest** | 4 | Testes unitários |

### Infraestrutura

| Tecnologia | Utilização |
|---|---|
| **Docker / Docker Compose** | Containerização do MongoDB |
| **GitHub Actions** | Integração contínua (CI) |
| **Maven Wrapper** | Build do backend sem instalar o Maven |

---

## 🏗️ Arquitetura

### Backend

```
Controller  →  Service  →  Repository  →  MongoDB
     ↑            ↓
    DTO      Mapper / Model
```

| Camada | Responsabilidade |
|---|---|
| **Controller** | Recebe as requisições HTTP, valida a entrada e devolve as respostas. |
| **DTO** | Contratos de entrada e saída da API, evitando expor o modelo de domínio. |
| **Mapper** | Converte DTO ↔ entidade (`MedicamentoMapper`). |
| **Service** | Concentra as regras de negócio (ex.: não permitir estoque negativo). |
| **Repository** | Comunicação com o MongoDB via `MongoRepository`. |
| **Model** | Documento persistido no banco (`@Document(collection = "medicamentos")`). |
| **Exception** | Exceções de domínio + `GlobalExceptionHandler` para padronizar erros. |

---

## 📁 Estrutura do projeto

```
StockLine/
├── .github/
│   └── workflows/
│       └── ci.yml                      # Pipeline de CI
├── back/                               # Backend (Spring Boot)
│   ├── .mvn/wrapper/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/AEP/StockLine/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/
│   │   │   │   ├── mapper/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   ├── service/
│   │   │   │   └── StockLineApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   │       ├── java/AEP/StockLine/
│   │       └── resources/
│   │           └── application-teste.properties
│   ├── .env                            # Variáveis de ambiente (não versionado)
│   ├── .env.example                    # Modelo de variáveis de ambiente
│   ├── mvnw / mvnw.cmd
│   └── pom.xml
├── front/                              # Frontend (Angular)
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/
│   │   │   ├── features/
│   │   │   ├── layout/
│   │   │   └── shared/
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.scss
│   ├── public/
│   ├── angular.json
│   ├── package.json
│   └── tsconfig*.json
├── .gitattributes
├── .gitignore
├── docker-compose.yml                  # MongoDB
└── README.md
```

---

## 🗃️ Modelo de dados

Coleção MongoDB: **`medicamentos`**

Cada medicamento contém uma lista embarcada de lotes (`lotes`), onde cada lote possui `codigo`, `validade` (LocalDate) e `saldo` (Integer).

---

## ⚙️ Pré-requisitos

| Ferramenta | Versão | Verificar |
|---|---|---|
| **Java JDK** | **21 ou superior** | `java -version` |
| **Node.js** | 18 ou superior | `node -v` |
| **npm** | 10 ou superior | `npm -v` |
| **Docker Desktop** | recente | `docker --version` |
| **Git** | qualquer | `git --version` |

> Maven não precisa ser instalado — o backend traz o wrapper (`mvnw`).

---

## 🔐 Como configurar as variáveis de ambiente

### Backend

A aplicação não inicia sem a variável `MONGODB_URI`. Copie o arquivo de exemplo:

**Linux/macOS:**

```bash
cp back/.env.example back/.env
```

**Windows (PowerShell):**

```powershell
Copy-Item back/.env.example back/.env
```

Conteúdo do `back/.env` (compatível com o `docker-compose.yml`):

```properties
MONGODB_URI=mongodb://admin:admin123@localhost:27017/medistock?authSource=admin
MONGODB_TEST_URI=mongodb://admin:admin123@localhost:27017/medistock_test?authSource=admin
```

| Variável | Uso |
|---|---|
| `MONGODB_URI` | Banco usado pela aplicação em execução |
| `MONGODB_TEST_URI` | Banco usado pelos testes (perfil `teste`) |

> O `back/.env` **não deve ser commitado**. Use o `back/.env.example` como modelo.

---

## 📦 Como instalar as dependências

### Backend

As dependências do Maven são baixadas automaticamente durante o build. Não é necessária uma etapa separada de instalação.

### Frontend

```bash
cd front
npm install
```

---

## ▶️ Como executar o projeto

### 1. Subir o MongoDB

```bash
docker compose up -d mongodb
```

Isso cria o container `medistock-mongo` na porta `27017`, com usuário `admin` / senha `admin123` e um volume persistente (`mongo-data`).

### 2. Executar o backend

**Linux/macOS:**

```bash
cd back && ./mvnw spring-boot:run
```

> Se aparecer *permission denied*, rode `chmod +x back/mvnw` antes.

**Windows:**

```powershell
cd back
.\mvnw.cmd spring-boot:run
```

A API sobe em **http://localhost:8080**.

### 3. Executar o frontend

```bash
cd front
npm start
```

O frontend sobe em **http://localhost:4200** e se comunica com o backend em `http://localhost:8080`.

### Fluxo resumido (Linux/macOS)

```bash
docker compose up -d mongodb
cd back && ./mvnw spring-boot:run &
cd ../front && npm start
```

---

## 🐳 Como executar com Docker

O `docker-compose.yml` na raiz do projeto sobe apenas o MongoDB:

```bash
docker compose up -d mongodb
```

Comandos úteis:

```bash
docker compose ps
docker compose logs -f mongodb
docker compose down
```

> `docker compose down -v` também **apaga os dados** do volume.

Testar a conexão:

```bash
docker exec -it medistock-mongo mongosh -u admin -p admin123 --authenticationDatabase admin
```

---

## 🧪 Como executar os testes

### Backend

Os testes de repositório exigem um MongoDB acessível. **Suba o Docker antes de rodá-los.**

**Linux/macOS:**

```bash
docker compose up -d mongodb
cd back && ./mvnw clean verify
```

**Windows:**

```powershell
docker compose up -d mongodb
cd back
.\mvnw.cmd clean verify
```

Testes de serviço e controller (sem MongoDB):

**Linux/macOS:**

```bash
cd back && ./mvnw "-Dtest=MedicamentoServiceTest,MedicamentoControllerTest" test
```

**Windows:**

```powershell
cd back
.\mvnw.cmd "-Dtest=MedicamentoServiceTest,MedicamentoControllerTest" test
```

#### Cobertura (JaCoCo)

O relatório é gerado na fase `test`:

```
back/target/site/jacoco/index.html
```

**Regra:** mínimo de **70%** de instruções cobertas. Excluídos: `model/`, `dto/`, `mapper/`.

#### Suítes de teste

| Arquivo | Foco |
|---|---|
| `*ServiceTest` | Regras de negócio com mocks (Mockito) |
| `*ControllerTest` | Camada web e contratos HTTP |
| `*RepositoryTest` | Integração com o MongoDB |
| `GlobalExceptionHandlerTest` | Padronização das respostas de erro |
| `StockLineApplicationTests` | Carregamento do contexto Spring |

### Frontend

```bash
cd front
npm test
```

---

## 📖 Documentação da API (Swagger)

Com o backend rodando:

| Recurso | URL |
|---|---|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

---

## 📡 Endpoints

Base: `http://localhost:8080`

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| `POST` | `/medicamentos` | Cadastra um medicamento | `201 Created` |
| `GET` | `/medicamentos` | Lista todos os medicamentos | `200 OK` |
| `GET` | `/medicamentos/{id}` | Busca um medicamento por ID | `200 OK` |
| `PUT` | `/medicamentos/{id}` | Atualiza um medicamento | `200 OK` |
| `DELETE` | `/medicamentos/{id}` | Remove um medicamento | `204 No Content` |
| `POST` | `/medicamentos/{id}/entradas` | Registra entrada de estoque por lote | `200 OK` |
| `POST` | `/medicamentos/{id}/baixas` | Registra baixa de estoque por lote | `200 OK` |
| `GET` | `/medicamentos/{id}/historico` | Consulta histórico de movimentações | `200 OK` |
| `POST` | `/categorias` | Cadastra uma categoria | `201 Created` |
| `GET` | `/categorias` | Lista todas as categorias | `200 OK` |
| `GET` | `/lotes/vencidos` | Lista lotes com validade vencida | `200 OK` |
| `GET` | `/lotes/a-vencer?dias=30` | Lista lotes próximos do vencimento | `200 OK` |

---

## ✅ Validações

### `MedicamentoRequestDTO`

| Campo | Regra |
|---|---|
| `nome` | `@NotBlank` |
| `quantidade` | `@NotNull` + `@Min(0)` |
| `validade` | `@NotNull` |
| `lote` | `@NotBlank` |
| `categoriaId` | `@NotBlank` |

---

## ⚠️ Tratamento de erros

Todos os erros de domínio passam pelo `GlobalExceptionHandler` e retornam o formato `ErroResponseDTO`:

```json
{
  "status": 404,
  "mensagem": "Medicamento não encontrado com o id: 123"
}
```

---

## 🔄 Integração contínua

Workflow: [`.github/workflows/ci.yml`](.github/workflows/ci.yml)

**Disparo:** em todo `push` (qualquer branch) e em `pull_request` para `master`.

**Etapas do pipeline:**

1. Checkout do repositório
2. Setup do **Java 21** (Temurin) com cache do Maven
3. Sobe o MongoDB via `docker compose up -d mongodb`
4. Aguarda o MongoDB ficar pronto (`mongosh ping` em loop)
5. Verifica a autenticação no banco de teste
6. Executa `mvn clean verify` em `back/` com as variáveis de ambiente configuradas
7. Derruba o MongoDB (`docker compose down -v`), mesmo em caso de falha

O build **falha** — e o PR fica bloqueado — se algum teste quebrar ou se a cobertura ficar abaixo de 70%.

---

## 🐛 Problemas comuns

| Problema | Causa provável | Solução |
|---|---|---|
| `Could not resolve placeholder 'MONGODB_URI'` | `back/.env` não criado | `cp back/.env.example back/.env` |
| `Connection refused: localhost:27017` | MongoDB não está rodando | `docker compose up -d mongodb` |
| `Authentication failed` | URI sem `authSource=admin` | Use a URI exatamente como no `back/.env.example` |
| `Port 8080 was already in use` | Porta ocupada | Altere `server.port` em `back/src/main/resources/application.properties` |
| `UnsupportedClassVersionError` | JDK abaixo de 21 | Instale o **JDK 21** e confira `java -version` |
| `mvnw: Permission denied` | Wrapper sem permissão | `chmod +x back/mvnw` |
| Testes de repositório falhando | Mongo fora do ar / perfil errado | Suba o Docker e use `SPRING_PROFILES_ACTIVE=teste` |
| Build falha em `jacoco:check` | Cobertura abaixo de 70% | Escreva mais testes |
| `docker: command not found` | Docker não instalado/aberto | Instale e abra o Docker Desktop |

---

## 🌿 Estratégia de branches

A branch principal é **`master`**. O desenvolvimento acontece em branches de funcionalidade:

```
master
 ├── feature/dtos
 ├── feature/medicamento
 ├── feature/estoque
 └── feature/testes
```

### Convenção de commits

```
feat:     adiciona nova funcionalidade
fix:      corrige comportamento
test:     adiciona ou ajusta testes
refactor: refatora código sem mudar comportamento
docs:     atualiza documentação
chore:    atualiza configurações e dependências
```

---

## 🎯 Objetivos do projeto

O desenvolvimento do StockLine busca aplicar, na prática:

- Desenvolvimento de APIs REST
- Arquitetura em camadas
- Persistência com MongoDB e Spring Data
- Utilização de DTOs e mappers
- Validação de dados com Bean Validation
- Tratamento centralizado de exceções
- Documentação de API com OpenAPI/Swagger
- Testes unitários e de integração
- Mocking com Mockito
- Controle de cobertura com JaCoCo
- Integração contínua com GitHub Actions
- Containerização com Docker
- Controle de versão com Git e GitHub

---

## 👥 Desenvolvimento

Projeto desenvolvido como parte de atividade acadêmica (AEP), utilizando práticas de desenvolvimento colaborativo e controle de versão.

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos.
