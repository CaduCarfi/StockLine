# StockLine — Frontend

Interface web para gerenciamento de medicamentos e controle de estoque.

**Stack:** Angular 21 · TypeScript 5.9 · Node.js 18+ · npm 10

---

## Pré-requisitos

| Ferramenta | Versão mínima | Verificar |
|---|---|---|
| Node.js | 18 | `node --version` |
| npm | 10 | `npm --version` |

> O Angular CLI **não precisa ser instalado globalmente** — ele está nas dependências do projeto e os scripts do `package.json` já o utilizam internamente.

---

## Configuração do backend

O frontend consome a API REST em **`http://localhost:8080`** (configurado em `src/app/core/services/api.service.ts`).

**O backend deve estar rodando antes de usar a aplicação.** Consulte `back/README.md` para saber como subir a API e o MongoDB.

> Se o backend rodar em outra porta, altere a constante `baseUrl` em `front/src/app/core/services/api.service.ts`.

---

## Variáveis de ambiente

O projeto **não utiliza arquivo `.env`**. A URL do backend está definida diretamente no código-fonte:

```typescript
// front/src/app/core/services/api.service.ts
private readonly baseUrl = 'http://localhost:8080';
```

---

## Instalar dependências

Execute a partir da pasta `front/`:

```bash
cd front
npm install
```

---

## Executar a aplicação

```bash
cd front
npm start
```

A aplicação sobe em **http://localhost:4200**. O terminal exibe:

```
Application bundle generation complete.
```

Para parar: `Ctrl + C`.

### Ordem para subir o sistema completo

```bash
# 1. A partir da raiz do repositório — sobe o MongoDB
docker compose up -d mongodb

# 2. A partir da pasta back/ — sobe a API
cd back && ./mvnw spring-boot:run

# 3. A partir da pasta front/ — sobe o frontend
cd front && npm start
```

---

## Executar os testes

```bash
cd front
npm test
```

Os testes rodam com **Vitest** em ambiente jsdom (sem abrir o navegador). O resultado aparece diretamente no terminal.

---

## Build de produção

```bash
cd front
npm run build
```

Os arquivos gerados ficam em `front/dist/`.

---

## Páginas da aplicação

| Rota | Descrição |
|---|---|
| `/medicamentos` | Listagem e gerenciamento de medicamentos |
| `/categorias` | Listagem e gerenciamento de categorias |
| `/alertas` | Lotes vencidos e próximos do vencimento |
| `/historico` | Histórico de movimentações de estoque |

---

## Estrutura de diretórios

```
front/
├── package.json                  ← dependências e scripts npm
├── tsconfig.json                 ← configuração TypeScript
├── angular.json                  ← configuração do Angular CLI
└── src/
    ├── main.ts                   ← bootstrap da aplicação
    ├── styles.scss               ← estilos globais
    └── app/
        ├── app.ts                ← componente raiz
        ├── app.html              ← template raiz (layout + router-outlet)
        ├── app-module.ts         ← módulo raiz
        ├── app-routing-module.ts ← rotas principais (lazy load)
        ├── app.spec.ts           ← testes do componente raiz
        ├── core/
        │   ├── models/           ← interfaces e tipos de dados
        │   └── services/
        │       └── api.service.ts ← cliente HTTP (baseUrl: localhost:8080)
        ├── features/
        │   ├── medicamentos/     ← módulo lazy de medicamentos
        │   ├── categorias/       ← módulo lazy de categorias
        │   ├── alertas/          ← módulo lazy de alertas
        │   └── historico/        ← módulo lazy de histórico
        ├── layout/
        │   ├── navbar/           ← barra superior
        │   └── sidebar/          ← menu lateral
        └── shared/
            ├── components/
            │   └── alert/        ← componente de notificações
            └── services/
                └── alerta.service.ts ← serviço de notificações
```

---

## Problemas comuns

| Problema | Causa provável | Solução |
|---|---|---|
| `ECONNREFUSED http://localhost:8080` | Backend não está rodando | Suba a API: `cd back && ./mvnw spring-boot:run` |
| `Port 4200 is already in use` | Porta ocupada | Use `npm start -- --port 4201` |
| `Cannot find module '...'` | Dependências não instaladas | Execute `npm install` na pasta `front/` |
| `node: command not found` | Node.js não instalado | Instale o Node.js 18+ em nodejs.org |
