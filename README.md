StockLine

API REST para gerenciamento de medicamentos e controle de estoque, desenvolvida com Java e Spring Boot, utilizando MongoDB como banco de dados.

O projeto tem como objetivo fornecer uma solução simples e organizada para cadastro, consulta e gerenciamento da quantidade de medicamentos em estoque, seguindo boas práticas de desenvolvimento, testes automatizados e integração contínua.

📋 Sobre o projeto

O StockLine é uma aplicação backend voltada para o gerenciamento de medicamentos.

A API permitirá realizar operações como:

Cadastro de medicamentos
Listagem de medicamentos
Busca de medicamentos
Edição de medicamentos
Controle de quantidade em estoque
Baixa de estoque
Persistência dos dados em uma única coleção MongoDB
Testes automatizados
Análise de cobertura de testes

O projeto está sendo desenvolvido de forma incremental, com foco em código organizado, testável e de fácil manutenção.

🚀 Tecnologias
Tecnologia	Utilização
Java	Linguagem principal
Spring Boot	Framework principal
Spring Web	Desenvolvimento da API REST
Spring Data MongoDB	Persistência dos dados
MongoDB	Banco de dados NoSQL
JUnit 5	Testes automatizados
Mockito	Mocking e testes unitários
JaCoCo	Análise de cobertura de testes
Docker	Containerização
GitHub Actions	Integração contínua (CI)
Git	Controle de versão
🏗️ Arquitetura

O projeto segue uma organização em camadas, buscando separar as responsabilidades da aplicação:

src/
└── main/
    └── java/
        └── AEP/
            └── StockLine/
                ├── controller/
                ├── dto/
                ├── model/
                ├── repository/
                └── service/

Camadas

Controller

Responsável por receber as requisições HTTP e retornar as respostas da API.

DTO

Responsável pela comunicação de dados entre a API e o cliente, evitando expor diretamente os objetos de domínio.

Service

Concentra as regras de negócio da aplicação.

Repository

Responsável pela comunicação com o MongoDB através do Spring Data MongoDB.

Model

Representa os documentos persistidos no banco de dados.

💊 Medicamento

O cadastro de um medicamento possui informações como:

Nome — obrigatório
Descrição — opcional
Quantidade — obrigatória
Validade
Lote

As informações retornadas pela API podem ser diferentes das informações recebidas no cadastro, utilizando DTOs específicos para entrada e saída de dados.

Exemplo de requisição
{
  "nome": "Dipirona",
  "descricao": "Medicamento genérico",
  "quantidade": 100
}

Exemplo de resposta
{
  "id": "68b7a123456789",
  "nome": "Dipirona",
  "descricao": "Medicamento genérico",
  "quantidade": 100,
  "validade": "2027-08-15",
  "lote": "LOT-2026-001"
}

📌 Funcionalidades
Medicamentos
 Cadastro de medicamentos
 Listagem de medicamentos
 Busca de medicamento
 Edição de medicamento
Estoque
 Controle de quantidade
 Baixa de estoque
Banco de dados
 Uma única coleção MongoDB
 Documentos simples e homogêneos
Qualidade
 Testes automatizados
 Cobertura mínima de 70%
 CI bloqueando código com testes/cobertura insuficientes
Documentação
 README inicial
🧪 Testes

O projeto utiliza JUnit 5 para testes automatizados e Mockito para criação de mocks durante os testes unitários.

A cobertura do código é analisada utilizando JaCoCo, tendo como objetivo uma cobertura mínima de:

70%


Os testes fazem parte do processo de integração contínua e alterações que não atendam aos critérios definidos pelo projeto podem ser bloqueadas pelo pipeline.

🔄 Integração Contínua

O projeto utiliza GitHub Actions para executar automaticamente verificações a cada alteração enviada ao repositório.

O pipeline tem como objetivo:

Compilar o projeto
Executar os testes automatizados
Gerar o relatório de cobertura
Verificar o percentual mínimo de cobertura
Bloquear alterações que não atendam aos critérios definidos
🐳 Docker

O projeto possui suporte à utilização de Docker para facilitar a configuração do ambiente de desenvolvimento e execução da aplicação.

A utilização de containers permite padronizar o ambiente e reduzir diferenças entre as máquinas utilizadas pelos integrantes da equipe.

⚙️ Como executar o projeto
Pré-requisitos

Antes de executar o projeto, certifique-se de possuir instalado:

Java
Maven ou Maven Wrapper
Docker
Git
Clonando o repositório
git clone https://github.com/CaduCarfi/StockLine.git


Entre no diretório:

cd StockLine

Executando os testes

No Windows:

.\mvnw.cmd test


No Linux/macOS:

./mvnw test

Executando a aplicação

Windows:

.\mvnw.cmd spring-boot:run


Linux/macOS:

./mvnw spring-boot:run

🌿 Estratégia de branches

O projeto utiliza branches para organizar o desenvolvimento das funcionalidades.

Exemplo:

main
 ├── feature/dtos
 ├── feature/medicamento
 ├── feature/estoque
 └── feature/testes


As alterações devem ser desenvolvidas em branches específicas e posteriormente integradas à branch principal através de Pull Requests.

Convenção de commits

Recomenda-se utilizar mensagens de commit seguindo o padrão:

feat: adiciona nova funcionalidade
fix: corrige comportamento
test: adiciona testes
refactor: refatora código
docs: atualiza documentação
chore: atualiza configurações


Exemplo:

git commit -m "feat: adiciona DTOs de medicamento"

📁 Estrutura do projeto
StockLine/
├── .github/
│   └── workflows/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── AEP/
│   │   │       └── StockLine/
│   │   │           ├── controller/
│   │   │           ├── dto/
│   │   │           ├── model/
│   │   │           ├── repository/
│   │   │           └── service/
│   │   └── resources/
│   └── test/
├── Dockerfile
├── pom.xml
└── README.md

🎯 Objetivos do projeto

O desenvolvimento do StockLine busca aplicar, na prática:

Desenvolvimento de APIs REST
Arquitetura em camadas
Persistência com MongoDB
Utilização de DTOs
Validação de dados
Testes unitários
Mocking com Mockito
Controle de cobertura com JaCoCo
Integração contínua
Containerização com Docker
Controle de versão com Git e GitHub
👥 Desenvolvimento

Projeto desenvolvido como parte da atividade acadêmica, utilizando práticas de desenvolvimento colaborativo e controle de versão.

📄 Licença

Este projeto foi desenvolvido para fins acadêmicos.
