# StockLine – Frontend

Interface web do **StockLine**, sistema de controle de estoque de medicamentos para postos de saúde e ONGs (alinhado ao **ODS 3 – Saúde e Bem-Estar**). O frontend consome a API REST do backend (Java + Spring Boot + MongoDB) e permite cadastrar, consultar e movimentar o estoque de medicamentos.

> Projeto acadêmico (AEP – Engenharia de Software, UniCesumar).

---

## Funcionalidades

- Listagem de medicamentos com destaque para **estoque baixo** e **validade vencida/próxima**
- Cadastro, edição e exclusão de medicamentos
- Ajuste rápido de quantidade (de -10 a +10)
- Página de detalhes com lotes e estoque total
- Gerenciamento de categorias
- Entrada e baixa de estoque por lote
- Histórico de movimentações
- Painel de alertas (estoque baixo, lotes vencidos e a vencer)
- Indicador de status da API (`/actuator/health`)

> O acompanhamento de cada item está nas issues do GitHub, com a label `frontend`.

---

## Tecnologias

| Item | Detalhe |
|------|---------|
| Framework | [Angular](https://angular.dev) 21 (Angular CLI 21.2.29) |
| Linguagem | TypeScript |
| Testes unitários | [Vitest](https://vitest.dev/) |
| Backend consumido | API REST Spring Boot (`http://localhost:8080`) |

---

## Pré-requisitos

- [Node.js](https://nodejs.org/) em versão LTS compatível com o Angular 21
- npm (já vem com o Node.js)
- Angular CLI (opcional, o projeto também roda com `npx`):

```bash
npm install -g @angular/cli
```

- **Backend do StockLine em execução** em `http://localhost:8080`, com o MongoDB ativo (veja o README do backend e o `docker-compose.yml`)

---

## Como executar

```bash
# 1. Entre na pasta do frontend
cd frontend

# 2. Instale as dependências
npm install

# 3. Suba o servidor de desenvolvimento
ng serve
```

Acesse **http://localhost:4200/**. A aplicação recarrega automaticamente a cada alteração nos arquivos.

### Ordem para subir o sistema completo

1. `docker compose up -d` (MongoDB)
2. Backend Spring Boot (porta `8080`)
3. Frontend Angular (porta `4200`)

---

## Integração com a API

O frontend roda na porta `4200` e a API na `8080`, ou seja, são origens diferentes. Para as chamadas funcionarem em desenvolvimento, use **uma** das opções:

**Opção 1 – CORS no backend**: liberar `http://localhost:4200` na configuração do Spring.

**Opção 2 – Proxy do Angular** (`proxy.conf.json` na raiz do frontend):

```json
{
  "/medicamentos": { "target": "http://localhost:8080", "secure": false },
  "/actuator": { "target": "http://localhost:8080", "secure": false }
}
```

```bash
ng serve --proxy-config proxy.conf.json
```

### Endpoints utilizados

| Método | Endpoint | Uso |
|--------|----------|-----|
| GET | `/medicamentos` | Listagem |
| GET | `/medicamentos/{id}` | Detalhes |
| POST | `/medicamentos` | Cadastro |
| PUT | `/medicamentos/{id}` | Edição |
| DELETE | `/medicamentos/{id}` | Exclusão |
| PATCH | `/medicamentos/{id}/quantidade` | Ajuste de quantidade (`delta` de -10 a 10) |
| GET | `/medicamentos/estoque-baixo` | Alerta de estoque baixo |
| GET | `/medicamentos/{id}/historico` | Histórico de movimentações |
| GET | `/actuator/health` | Status da API |

> Os endpoints de categorias, entrada/baixa por lote e lotes vencidos ainda estão em definição no backend. Atualize esta tabela quando forem publicados. A documentação completa da API fica no Swagger do backend (`/swagger-ui.html`).

### Tratamento de erros

A API devolve erros no formato `ErroResponseDTO`. O frontend deve exibir a **mensagem** retornada ao usuário. Casos principais:

| Status | Situação |
|--------|----------|
| 400 | Quantidade inválida (ficaria negativa) ou dados inválidos |
| 404 | Medicamento não encontrado |

Atenção: a API **recusa** excluir, editar estoque ou ajustar quantidade de medicamentos que já possuem movimentações. A interface deve mostrar essa mensagem em vez de falhar em silêncio.

---

## Estrutura do projeto
frontend/
- src/
  - app/
    - core/ (serviços de API, interceptors, modelos)
    - shared/ (componentes reutilizáveis: avisos, loading)
    - pages/ (telas: medicamentos, categorias, alertas, histórico)
    - app.routes.ts (rotas)
    - app.ts (componente raiz)
  - index.html
  - styles.css
- angular.json
- package.json
- README.md


---

## Comandos úteis

| Comando | O que faz |
|---------|-----------|
| `ng serve` | Servidor de desenvolvimento em `localhost:4200` |
| `ng build` | Build de produção na pasta `dist/` |
| `ng test` | Testes unitários com Vitest |
| `ng generate component pages/nome` | Cria um novo componente |
| `ng generate service core/nome` | Cria um novo serviço |
| `ng generate --help` | Lista todos os geradores |

---

## Testes

```bash
ng test
```

Os testes do frontend rodam com Vitest. A cobertura exigida de 70% (JaCoCo) se aplica apenas ao backend em Java.

Para testes de ponta a ponta (e2e), o Angular CLI não traz uma ferramenta por padrão. O grupo pode escolher uma (por exemplo Playwright) e executar com `ng e2e`.

---

## Referências

- [Documentação do Angular](https://angular.dev)
- [Angular CLI – Referência de comandos](https://angular.dev/tools/cli)
- [Vitest](https://vitest.dev/)
