# 💊 StockLine

API REST para gerenciamento de medicamentos e controle de estoque, desenvolvida com **Java 21** e **Spring Boot 4**, utilizando **MongoDB** como banco de dados.

O projeto fornece uma solução simples e organizada para cadastro, consulta, edição e controle de quantidade de medicamentos em estoque, seguindo boas práticas de desenvolvimento, testes automatizados e integração contínua.

---

## 📑 Índice

- [Sobre o projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Modelo de dados](#-modelo-de-dados)
- [Como rodar o projeto](#-como-rodar-o-projeto)
- [Documentação da API (Swagger)](#-documentação-da-api-swagger)
- [Endpoints](#-endpoints)
- [Validações](#-validações)
- [Tratamento de erros](#-tratamento-de-erros)
- [Testes e cobertura](#-testes-e-cobertura)
- [Integração contínua](#-integração-contínua)
- [Problemas comuns](#-problemas-comuns)
- [Estratégia de branches](#-estratégia-de-branches)
- [Objetivos do projeto](#-objetivos-do-projeto)
- [Licença](#-licença)

---

## 📋 Sobre o projeto

O **StockLine** é uma aplicação backend voltada para o gerenciamento de medicamentos. A API permite:

- Cadastrar medicamentos
- Listar todos os medicamentos
- Buscar um medicamento por ID
- Editar um medicamento
- Ajustar a quantidade em estoque (entrada e baixa)
- Excluir um medicamento
- Persistir os dados em uma única coleção MongoDB (`medicamentos`)

---

## 🚀 Tecnologias

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
| **Docker / Docker Compose** | — | Containerização do MongoDB |
| **GitHub Actions** | — | Integração contínua (CI) |
| **Maven Wrapper** | — | Build sem precisar instalar o Maven |

---

## 🏗️ Arquitetura

O projeto segue uma organização em camadas, separando claramente as responsabilidades:

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
├── .mvn/
│   └── wrapper/
├── src/
│   ├── main/
│   │   ├── java/AEP/StockLine/
│   │   │   ├── controller/
│   │   │   │   └── MedicamentoController.java
│   │   │   ├── dto/
│   │   │   │   ├── AjusteQuantidadeRequestDTO.java
│   │   │   │   ├── ErroResponseDTO.java
│   │   │   │   ├── MedicamentoRequestDTO.java
│   │   │   │   └── MedicamentoResponseDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── MedicamentoNotFoundException.java
│   │   │   │   └── QuantidadeInvalidaException.java
│   │   │   ├── mapper/
│   │   │   │   └── MedicamentoMapper.java
│   │   │   ├── model/
│   │   │   │   └── Medicamento.java
│   │   │   ├── repository/
│   │   │   │   └── MedicamentoRepository.java
│   │   │   ├── service/
│   │   │   │   └── MedicamentoService.java
│   │   │   └── StockLineApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/AEP/StockLine/
│       │   ├── controller/MedicamentoControllerTest.java
│       │   ├── exception/GlobalExceptionHandlerTest.java
│       │   ├── repository/MedicamentoRepositoryTest.java
│       │   ├── service/MedicamentoServiceTest.java
│       │   └── StockLineApplicationTests.java
│       └── resources/
│           └── application-teste.properties
├── .env.example
├── docker-compose.yml
├── mvnw / mvnw.cmd
├── pom.xml
└── README.md
```

---

## 🗃️ Modelo de dados

Coleção MongoDB: **`medicamentos`**

| Campo | Tipo | Obrigatório | Observação |
|---|---|---|---|
| `id` | String | gerado | Identificador do documento |
| `nome` | String | ✅ | Não pode ser vazio |
| `descricao` | String | ❌ | Campo opcional |
| `quantidade` | Integer | ✅ | Não pode ser negativa |
| `validade` | LocalDate | ✅ | Formato `yyyy-MM-dd` |
| `lote` | String | ✅ | Não pode ser vazio |

---

## ⚙️ Como rodar o projeto

### 1. Pré-requisitos

| Ferramenta | Versão | Verificar |
|---|---|---|
| **Java JDK** | **21 ou superior** | `java -version` |
| **Docker Desktop** | recente | `docker --version` |
| **Git** | qualquer | `git --version` |
| Maven | *opcional* | o projeto já traz o wrapper |

> 💡 **Não é preciso instalar o Maven** — use `./mvnw` (Linux/macOS) ou `.\mvnw.cmd` (Windows).
>
> 💡 **Não é preciso instalar o MongoDB** — ele sobe via Docker Compose.

---

### 2. Clonar o repositório

```bash
git clone https://github.com/CaduCarfi/StockLine.git
```

```bash
cd StockLine
```

---

### 3. Configurar as variáveis de ambiente

A aplicação **não sobe sem** a variável `MONGODB_URI`. Copie o arquivo de exemplo:

**Linux/macOS:**

```bash
cp .env.example .env
```

**Windows (PowerShell):**

```bash
Copy-Item .env.example .env
```

Conteúdo do `.env` (já compatível com o `docker-compose.yml`):

```properties
MONGODB_URI=mongodb://admin:admin123@localhost:27017/medistock?authSource=admin
MONGODB_TEST_URI=mongodb://admin:admin123@localhost:27017/medistock_test?authSource=admin
```

| Variável | Uso |
|---|---|
| `MONGODB_URI` | Banco usado pela aplicação em execução |
| `MONGODB_TEST_URI` | Banco usado pelos testes (perfil `teste`) |

> ⚠️ O `.env` **não deve ser commitado**. Use o `.env.example` como modelo.

---

### 4. Subir o MongoDB

```bash
docker compose up -d mongodb
```

Isso cria o container `medistock-mongo` na porta `27017`, com usuário `admin` / senha `admin123` e um volume persistente (`mongo-data`).

Comandos úteis:

```bash
docker compose ps
```

```bash
docker compose logs -f mongodb
```

```bash
docker compose down
```

> ⚠️ `docker compose down -v` também **apaga os dados** do volume.

Testar a conexão pelo shell do Mongo:

```bash
docker exec -it medistock-mongo mongosh -u admin -p admin123 --authenticationDatabase admin
```

---

### 5. Executar a aplicação

**Windows:**

```bash
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

> Se aparecer *permission denied* no Linux/macOS, rode `chmod +x mvnw` antes.

A API sobe em **http://localhost:8080** e o console mostra:

```
Started StockLineApplication in X.XXX seconds
```

Para parar: `Ctrl + C`.

> ℹ️ O **DevTools** está habilitado — alterações recompiladas pela IDE reiniciam a aplicação automaticamente.

---

### 6. Gerar e executar o `.jar`

```bash
./mvnw clean package
```

```bash
java -jar target/StockLine-0.0.1-SNAPSHOT.jar
```

Para pular os testes no build (útil quando o Mongo não está de pé):

```bash
./mvnw clean package -DskipTests
```

---

### 7. Fluxo resumido

```bash
git clone https://github.com/CaduCarfi/StockLine.git && cd StockLine && cp .env.example .env && docker compose up -d mongodb && ./mvnw spring-boot:run
```

---

## 📖 Documentação da API (Swagger)

Com a aplicação rodando, a documentação interativa fica disponível em:

| Recurso | URL |
|---|---|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

Pelo Swagger UI é possível testar todos os endpoints direto do navegador, sem precisar de Postman ou curl.

---

## 📡 Endpoints

Base: `http://localhost:8080/medicamentos`

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| `POST` | `/medicamentos` | Cadastra um medicamento | `201 Created` |
| `GET` | `/medicamentos` | Lista todos os medicamentos | `200 OK` |
| `GET` | `/medicamentos/{id}` | Busca um medicamento por ID | `200 OK` |
| `PUT` | `/medicamentos/{id}` | Atualiza um medicamento | `200 OK` |
| `PATCH` | `/medicamentos/{id}/quantidade` | Ajusta a quantidade em estoque | `200 OK` |
| `DELETE` | `/medicamentos/{id}` | Remove um medicamento | `204 No Content` |

---

### ➕ Cadastrar medicamento

`POST /medicamentos`

**Requisição:**

```json
{
  "nome": "Dipirona",
  "descricao": "Medicamento genérico",
  "quantidade": 100,
  "validade": "2027-08-15",
  "lote": "LOT-2026-001"
}
```

**Resposta — `201 Created`:**

```json
{
  "id": "68b7a12345678901234abcde",
  "nome": "Dipirona",
  "descricao": "Medicamento genérico",
  "quantidade": 100,
  "validade": "2027-08-15",
  "lote": "LOT-2026-001"
}
```

```bash
curl -X POST http://localhost:8080/medicamentos -H "Content-Type: application/json" -d "{\"nome\":\"Dipirona\",\"descricao\":\"Medicamento generico\",\"quantidade\":100,\"validade\":\"2027-08-15\",\"lote\":\"LOT-2026-001\"}"
```

---

### 📋 Listar medicamentos

`GET /medicamentos`

**Resposta — `200 OK`:**

```json
[
  {
    "id": "68b7a12345678901234abcde",
    "nome": "Dipirona",
    "descricao": "Medicamento genérico",
    "quantidade": 100,
    "validade": "2027-08-15",
    "lote": "LOT-2026-001"
  }
]
```

```bash
curl http://localhost:8080/medicamentos
```

---

### 🔍 Buscar por ID

`GET /medicamentos/{id}`

```bash
curl http://localhost:8080/medicamentos/68b7a12345678901234abcde
```

Se o ID não existir, a API responde `404 Not Found`.

---

### ✏️ Atualizar medicamento

`PUT /medicamentos/{id}`

Substitui **todos** os campos do medicamento. O corpo é o mesmo do cadastro:

```json
{
  "nome": "Dipirona Sódica",
  "descricao": "Analgésico e antitérmico",
  "quantidade": 80,
  "validade": "2028-01-30",
  "lote": "LOT-2026-002"
}
```

```bash
curl -X PUT http://localhost:8080/medicamentos/68b7a12345678901234abcde -H "Content-Type: application/json" -d "{\"nome\":\"Dipirona Sodica\",\"quantidade\":80,\"validade\":\"2028-01-30\",\"lote\":\"LOT-2026-002\"}"
```

---

### 🔁 Ajustar quantidade (entrada / baixa de estoque)

`PATCH /medicamentos/{id}/quantidade`

Aplica um **delta** sobre a quantidade atual: valor positivo dá entrada, negativo dá baixa.

**Requisição:**

```json
{
  "delta": -5
}
```

**Resposta — `200 OK`** (medicamento com a quantidade já atualizada):

```json
{
  "id": "68b7a12345678901234abcde",
  "nome": "Dipirona",
  "descricao": "Medicamento genérico",
  "quantidade": 95,
  "validade": "2027-08-15",
  "lote": "LOT-2026-001"
}
```

```bash
curl -X PATCH http://localhost:8080/medicamentos/68b7a12345678901234abcde/quantidade -H "Content-Type: application/json" -d "{\"delta\":-5}"
```

> ⚠️ **Regras:** o `delta` precisa estar entre **-10 e 10**, e o resultado **não pode ficar negativo** — caso contrário a API responde `400 Bad Request`.

---

### 🗑️ Deletar medicamento

`DELETE /medicamentos/{id}`

Resposta: `204 No Content` (sem corpo).

```bash
curl -X DELETE http://localhost:8080/medicamentos/68b7a12345678901234abcde
```

---

## ✅ Validações

### `MedicamentoRequestDTO` (cadastro e atualização)

| Campo | Regra | Mensagem |
|---|---|---|
| `nome` | `@NotBlank` | *O nome do medicamento é obrigatório* |
| `descricao` | — | opcional |
| `quantidade` | `@NotNull` + `@Min(0)` | *A quantidade é obrigatória* / *A quantidade não pode ser negativa* |
| `validade` | `@NotNull` | *A validade é obrigatória* |
| `lote` | `@NotBlank` | *O lote é obrigatório* |

### `AjusteQuantidadeRequestDTO` (ajuste de estoque)

| Campo | Regra | Mensagem |
|---|---|---|
| `delta` | `@NotNull` | *O delta é obrigatório* |
| `delta` | `@Min(-10)` | *O delta não pode ser menor que -10* |
| `delta` | `@Max(10)` | *O delta não pode ser maior que 10* |

---

## ⚠️ Tratamento de erros

Todos os erros de domínio passam pelo `GlobalExceptionHandler` e retornam o mesmo formato (`ErroResponseDTO`):

```json
{
  "status": 404,
  "mensagem": "Medicamento não encontrado com o id: 123"
}
```

| Situação | Exceção | Status |
|---|---|---|
| ID inexistente em busca, edição, ajuste ou exclusão | `MedicamentoNotFoundException` | `404 Not Found` |
| Ajuste que deixaria o estoque negativo | `QuantidadeInvalidaException` | `400 Bad Request` |
| Corpo da requisição inválido (Bean Validation) | `MethodArgumentNotValidException` | `400 Bad Request` |

Exemplo de erro de estoque negativo:

```json
{
  "status": 400,
  "mensagem": "Ajuste de quantidade inválido para o medicamento 68b7a123: delta -10 resultaria em quantidade negativa"
}
```

---

## 🧪 Testes e cobertura

O projeto usa **JUnit 5** e **Mockito**. Os testes de repositório exigem um MongoDB acessível através de `MONGODB_TEST_URI`, então **suba o Docker antes de rodá-los**.

### Rodar os testes

**Windows:**

```bash
docker compose up -d mongodb
```

```bash
.\mvnw.cmd clean test
```

**Linux/macOS:**

```bash
./mvnw clean test
```

> O perfil de teste é ativado com `SPRING_PROFILES_ACTIVE=teste`, que carrega o `application-teste.properties` e aponta para o banco `medistock_test`.

### Suítes de teste

| Arquivo | Foco |
|---|---|
| `MedicamentoServiceTest` | Regras de negócio com mocks (Mockito) |
| `MedicamentoControllerTest` | Camada web e contratos HTTP |
| `MedicamentoRepositoryTest` | Integração com o MongoDB |
| `GlobalExceptionHandlerTest` | Padronização das respostas de erro |
| `StockLineApplicationTests` | Carregamento do contexto Spring |

### Cobertura (JaCoCo)

O relatório é gerado automaticamente na fase `test`:

```bash
./mvnw clean test
```

Abra o relatório em:

```
target/site/jacoco/index.html
```

**Regra de cobertura configurada no `pom.xml`:**

- Mínimo de **70%** de instruções cobertas (`BUNDLE` / `INSTRUCTION` / `COVEREDRATIO ≥ 0.70`)
- **Excluídos** da métrica: `model/`, `dto/` e `mapper/` (classes majoritariamente geradas pelo Lombok)
- Se a cobertura ficar abaixo do mínimo, o goal `jacoco:check` **falha o build**

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
6. Executa `mvn clean test` com `MONGODB_URI`, `MONGODB_TEST_URI` e `SPRING_PROFILES_ACTIVE=teste`
7. Derruba o MongoDB (`docker compose down -v`), mesmo em caso de falha

O build **falha** — e o PR fica bloqueado — se algum teste quebrar ou se a cobertura ficar abaixo de 70%.

---

## 🐛 Problemas comuns

| Problema | Causa provável | Solução |
|---|---|---|
| `Could not resolve placeholder 'MONGODB_URI'` | `.env` não criado | `cp .env.example .env` |
| `Connection refused: localhost:27017` | MongoDB não está rodando | `docker compose up -d mongodb` |
| `Authentication failed` | URI sem `authSource=admin` | Use a URI exatamente como no `.env.example` |
| `Port 8080 was already in use` | Porta ocupada | Altere `server.port` no `application.properties` |
| `UnsupportedClassVersionError` | JDK abaixo de 21 | Instale o **JDK 21** e confira `java -version` |
| `mvnw: Permission denied` | Wrapper sem permissão | `chmod +x mvnw` |
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

As alterações são integradas ao `master` através de **Pull Requests**, que só podem ser mesclados com o CI verde.

### Convenção de commits

```
feat:     adiciona nova funcionalidade
fix:      corrige comportamento
test:     adiciona ou ajusta testes
refactor: refatora código sem mudar comportamento
docs:     atualiza documentação
chore:    atualiza configurações e dependências
```

Exemplo:

```bash
git commit -m "feat: adiciona ajuste de quantidade em estoque"
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
