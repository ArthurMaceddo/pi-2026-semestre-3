<h1 align="center">💆 FisioCare</h1>

<p align="center">
  Sistema de Gerenciamento de Clínica de Fisioterapia
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java"/>
  <img src="https://img.shields.io/badge/PostgreSQL-18-blue?style=flat-square&logo=postgresql"/>
  <img src="https://img.shields.io/badge/Electron-27-47848F?style=flat-square&logo=electron"/>
  <img src="https://img.shields.io/badge/Maven-3.6+-C71A36?style=flat-square&logo=apachemaven"/>
  <img src="https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=flat-square"/>
</p>

---

## 📋 Sobre o Projeto

O **FisioCare** é um sistema desktop desenvolvido como Projeto Integrador do 3º Semestre da **FATEC**. O objetivo é auxiliar clínicas de fisioterapia no gerenciamento de pacientes, agendamento de consultas e acompanhamento da evolução das sessões de tratamento.

---

## 👥 Equipe

| Nome | Responsabilidade |
|---|---|
| Arthur Macedo | Fundação do projeto, banco de dados e frontend |
| Guilherme | Models, DAOs e páginas do frontend |
| Tiago | Servidor HTTP, autenticação e handlers da API |

---

## 🚀 Funcionalidades

- 🔐 **Login e autenticação** com controle de acesso por perfil
- 👥 **Cadastro de pacientes** com dados clínicos completos
- 👔 **Cadastro de funcionários** restrito ao Administrador
- 📅 **Agendamento de consultas** com busca automática por CPF
- 📈 **Evolução do tratamento** com histórico de sessões e gráfico de dor
- 🏠 **Dashboard** com estatísticas em tempo real

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 17 — JDBC — HikariCP — Gson |
| Banco de Dados | PostgreSQL 18 |
| Frontend | Electron 27 — HTML — CSS — JavaScript |
| Gráficos | Chart.js |
| Build | Apache Maven |

---

## 📁 Estrutura do Projeto

```
fisiocare/
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/fisiocare/
│       ├── FisioCareServer.java
│       ├── database/
│       │   └── DatabaseConnection.java
│       ├── model/
│       │   ├── Usuario.java
│       │   ├── Paciente.java
│       │   ├── Agendamento.java
│       │   └── Sessao.java
│       ├── dao/
│       │   ├── UsuarioDAO.java
│       │   ├── PacienteDAO.java
│       │   ├── AgendamentoDAO.java
│       │   └── SessaoDAO.java
│       ├── service/
│       │   └── AutenticacaoService.java
│       └── handler/
│           ├── BaseHandler.java
│           ├── AuthHandler.java
│           ├── PacienteHandler.java
│           ├── FuncionarioHandler.java
│           ├── AgendamentoHandler.java
│           ├── SessaoHandler.java
│           └── DashboardHandler.java
├── frontend/
│   ├── main.js
│   ├── package.json
│   └── public/
│       ├── index.html
│       ├── style.css
│       └── js/
│           ├── api.js
│           ├── app.js
│           └── pages/
│               ├── dashboard.js
│               ├── pacientes.js
│               ├── funcionarios.js
│               ├── agendamentos.js
│               └── evolucao.js
└── scripts/
    └── database.sql
```

---

## ⚙️ Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- [Java JDK 17+](https://www.oracle.com/java/technologies/downloads/)
- [Apache Maven 3.6+](https://maven.apache.org/download.cgi)
- [PostgreSQL 13+](https://www.postgresql.org/download/)
- [Node.js 16+](https://nodejs.org/)

---

## 🔧 Instalação e Execução

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/fisiocare.git
cd fisiocare
```

### 2. Configure o banco de dados

```bash
psql -U postgres -f scripts/database.sql
```

> Se precisar alterar as credenciais do banco, edite o arquivo:
> `backend/src/main/java/com/fisiocare/database/DatabaseConnection.java`

### 3. Compile e inicie o backend

```bash
cd backend
mvn clean package -DskipTests
java -jar target/fisiocare.jar
```

Você verá a seguinte mensagem:
```
=========================================
  FisioCare Backend rodando na porta 8080
  http://localhost:8080
=========================================
```

### 4. Inicie o frontend

Abra um novo terminal:

```bash
cd frontend
npm install
npm start
```

A janela do FisioCare abrirá automaticamente.

---

## 🔑 Credenciais de Acesso

| Perfil | E-mail | Senha |
|---|---|---|
| Administrador | admin@fisiocare.com | admin123 |
| Funcionário | joao@fisiocare.com | fisio123 |
| Paciente | maria@email.com | maria123 |

---

## 🔐 Perfis e Permissões

| Funcionalidade | Administrador | Funcionário | Paciente |
|---|:---:|:---:|:---:|
| Ver Dashboard | ✅ | ✅ | ✅ |
| Cadastrar Paciente | ✅ | ✅ | ❌ |
| Editar Paciente | ✅ | ✅ | ❌ |
| Remover Paciente | ✅ | ❌ | ❌ |
| Cadastrar Funcionário | ✅ | ❌ | ❌ |
| Criar Agendamento | ✅ | ✅ | ❌ |
| Atualizar Status | ✅ | ✅ | ❌ |
| Registrar Sessão | ✅ | ✅ | ❌ |
| Ver Evolução | ✅ | ✅ | Apenas a própria |

---

## 🌐 Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/auth/login` | Realiza login |
| POST | `/api/auth/logout` | Realiza logout |
| GET | `/api/dashboard` | Estatísticas do sistema |
| GET | `/api/pacientes` | Lista pacientes |
| POST | `/api/pacientes` | Cria paciente |
| PUT | `/api/pacientes/{id}` | Atualiza paciente |
| DELETE | `/api/pacientes/{id}` | Remove paciente |
| GET | `/api/pacientes/cpf/{cpf}` | Busca por CPF |
| GET | `/api/funcionarios` | Lista funcionários |
| POST | `/api/funcionarios` | Cria funcionário |
| GET | `/api/agendamentos` | Lista agendamentos |
| GET | `/api/agendamentos/hoje` | Agendamentos do dia |
| POST | `/api/agendamentos` | Cria agendamento |
| PUT | `/api/agendamentos/{id}` | Atualiza status |
| GET | `/api/sessoes/paciente/{id}` | Sessões por paciente |
| POST | `/api/sessoes` | Registra sessão |

---

## 🎓 Informações Acadêmicas

- **Instituição:** FATEC
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Semestre:** 3º Semestre
- **Ano:** 2026
