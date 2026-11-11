# StockLine — Backend

API REST para gerenciamento de medicamentos e controle de estoque.

**Stack:** Java 21 · Spring Boot 4.1.1 · MongoDB 7 · Maven

---

## Pré-requisitos

| Ferramenta | Versão mínima | Verificar |
|---|---|---|
| Java JDK | 21 | `java -version` |
| Docker Desktop | qualquer recente | `docker --version` |
| Git | qualquer | `git --version` |

> Maven **não precisa ser instalado** — o projeto inclui o wrapper `mvnw`.

---

## Variáveis de ambiente

A aplicação lê as variáveis do arquivo `back/.env` via `spring-dotenv`. **Sem esse arquivo a aplicação não inicia.**

**1. Copie o arquivo de exemplo:**

```bash
# Linux / macOS
cp .env.example .env

# Windows (PowerShell)
Copy-Item .env.example .env
```

**2. Conteúdo do `.env`:**

```properties
MONGODB_URI=mongodb://admin:admin123@localhost:27017/medistock?authSource=admin&authMechanism=SCRAM-SHA-256

MONGODB_TEST_URI=mongodb://admin:admin123@localhost:27017/medistock_test?authSource=admin&authMechanism=SCRAM-SHA-256
```

| Variável | Uso |
|---|---|
| `MONGODB_URI` | Banco utilizado pela aplicação em execução |
| `MONGODB_TEST_URI` | Banco utilizado pelos testes de integração (perfil `teste`) |

> O `.env` **nunca deve ser commitado**. Ele já está no `.gitignore`.

---

## Subir o banco de dados

O MongoDB sobe via Docker Compose. O `docker-compose.yml` fica na **raiz do repositório**:

```bash
# A partir da raiz do repositório
docker compose up -d mongodb
```

Isso cria o container `medistock-mongo` na porta `27017` com usuário `admin` / senha `admin123`.

Comandos úteis:

```bash
docker compose ps                     # verifica se o container está rodando
docker compose logs -f mongodb        # acompanha os logs
docker compose down                   # para o container (mantém os dados)
docker compose down -v                # para o container e apaga os dados
```

> Se você tiver um MongoDB local instalado (ex.: Homebrew) também rodando na porta `27017`, pare-o antes de executar os testes, pois ele intercepta as conexões do Docker:
>
> ```bash
> brew services stop mongodb-community
> ```

---

## Executar a aplicação

Execute os comandos a partir da pasta `back/`:

**Linux / macOS:**

```bash
cd back
./mvnw spring-boot:run
```

> Se aparecer `Permission denied`, rode `chmod +x mvnw` antes.

**Windows:**

```powershell
cd back
.\mvnw.cmd spring-boot:run
```

A API sobe em **http://localhost:8080**. O console exibe:

```
Started StockLineApplication in X.XXX seconds
```

Para parar: `Ctrl + C`.

### Gerar e executar o `.jar`

```bash
./mvnw clean package
java -jar target/StockLine-0.0.1-SNAPSHOT.jar
```

Para pular os testes durante o build:

```bash
./mvnw clean package -DskipTests
```

---

## Executar os testes

Os testes de integração (repositório) exigem o MongoDB ativo. **Suba o Docker antes de executar.**

**Linux / macOS:**

```bash
cd back

MONGODB_URI="mongodb://admin:admin123@localhost:27017/medistock?authSource=admin&authMechanism=SCRAM-SHA-256" \
MONGODB_TEST_URI="mongodb://admin:admin123@localhost:27017/medistock_test?authSource=admin&authMechanism=SCRAM-SHA-256" \
SPRING_PROFILES_ACTIVE=teste \
./mvnw clean verify
```

**Windows (PowerShell):**

```powershell
cd back

$env:MONGODB_URI = "mongodb://admin:admin123@localhost:27017/medistock?authSource=admin&authMechanism=SCRAM-SHA-256"
$env:MONGODB_TEST_URI = "mongodb://admin:admin123@localhost:27017/medistock_test?authSource=admin&authMechanism=SCRAM-SHA-256"
$env:SPRING_PROFILES_ACTIVE = "teste"
.\mvnw.cmd clean verify
```

### Rodar apenas testes sem banco (service e controller)

```bash
./mvnw test -Dtest="*ServiceTest,*ControllerTest,*ExceptionHandlerTest,StockLineApplicationTests"
```

### Relatório de cobertura (JaCoCo)

Gerado automaticamente após `mvn verify`:

```
back/target/site/jacoco/index.html
```

Regra configurada: **mínimo 70%** de instruções cobertas. Excluídos: `model/`, `dto/`, `mapper/`.

---

## Suítes de teste

| Classe | Tipo | Precisa do MongoDB |
|---|---|---|
| `*ServiceTest` | Unitário (Mockito) | Não |
| `*ControllerTest` | Web slice (MockMvc) | Não |
| `GlobalExceptionHandlerTest` | Web slice | Não |
| `StockLineApplicationTests` | Contexto Spring | Não |
| `*RepositoryTest` | Integração (`@DataMongoTest`) | **Sim** |

---

## Documentação da API

Com a aplicação rodando:

| Recurso | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

---

## Endpoints principais

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/medicamentos` | Cadastra um medicamento |
| `GET` | `/medicamentos` | Lista todos os medicamentos |
| `GET` | `/medicamentos/{id}` | Busca por ID |
| `PUT` | `/medicamentos/{id}` | Atualiza um medicamento |
| `DELETE` | `/medicamentos/{id}` | Remove um medicamento |
| `POST` | `/medicamentos/{id}/entradas` | Entrada de estoque por lote |
| `POST` | `/medicamentos/{id}/baixas` | Baixa de estoque por lote |
| `GET` | `/medicamentos/{id}/historico` | Histórico de movimentações |
| `POST` | `/categorias` | Cadastra uma categoria |
| `GET` | `/categorias` | Lista categorias |
| `GET` | `/lotes/vencidos` | Lotes com validade vencida |
| `GET` | `/lotes/a-vencer?dias=30` | Lotes próximos do vencimento |

---

## Estrutura de diretórios

```
back/
├── .env                        ← não versionado, copie de .env.example
├── .env.example                ← modelo de variáveis de ambiente
├── .mvn/wrapper/               ← Maven Wrapper
├── mvnw / mvnw.cmd             ← scripts do wrapper
├── pom.xml                     ← dependências e plugins Maven
└── src/
    ├── main/
    │   ├── java/AEP/StockLine/
    │   │   ├── controller/     ← endpoints REST
    │   │   ├── service/        ← regras de negócio
    │   │   ├── repository/     ← acesso ao MongoDB
    │   │   ├── model/          ← documentos MongoDB
    │   │   ├── dto/            ← contratos de entrada e saída
    │   │   ├── mapper/         ← conversão DTO ↔ modelo
    │   │   └── exception/      ← exceções e handler global
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/AEP/StockLine/ ← testes espelhando src/main
        └── resources/
            └── application-teste.properties
```

---

## Problemas comuns

| Problema | Causa provável | Solução |
|---|---|---|
| `Could not resolve placeholder 'MONGODB_URI'` | `.env` não criado | `cp .env.example .env` |
| `Connection refused: localhost:27017` | MongoDB não está rodando | `docker compose up -d mongodb` |
| `Authentication failed` | URI sem `authMechanism=SCRAM-SHA-256` | Use exatamente a URI do `.env.example` |
| Testes falham com `Unauthorized` | MongoDB local na porta 27017 | Pare o MongoDB local antes de testar |
| `mvnw: Permission denied` | Wrapper sem permissão de execução | `chmod +x mvnw` |
| Build falha em `jacoco:check` | Cobertura abaixo de 70% | Escreva mais testes |
| `Port 8080 already in use` | Porta ocupada | Altere `server.port` em `application.properties` |
| `UnsupportedClassVersionError` | JDK abaixo de 21 | Instale o JDK 21 e verifique `java -version` |
