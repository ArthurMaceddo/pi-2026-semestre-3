📋 Sobre o Projeto

O FisioCare é um sistema desktop desenvolvido como Projeto Integrador do 3º Semestre da FATEC. O objetivo é auxiliar clínicas de fisioterapia no gerenciamento de pacientes, agendamento de consultas e acompanhamento da evolução das sessões de tratamento.

🚀 Funcionalidades


🔐 Login e autenticação com controle de acesso por perfil
👥 Cadastro de pacientes com dados clínicos completos
👔 Cadastro de funcionários restrito ao Administrador
📅 Agendamento de consultas com busca automática por CPF
📈 Evolução do tratamento com histórico de sessões e gráfico de dor
🏠 Dashboard com estatísticas em tempo real



🛠️ Tecnologias

Backend: Java 17 — JDBC — HikariCP — Gson
Banco de Dados: PostgreSQL 18 
Frontend: Electron 27 — HTML — CSS — JavaScript


📁 Estrutura do Projeto

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


⚙️ Pré-requisitos

Java JDK 17+
Apache Maven 3.6+
PostgreSQL 13+
Node.js 16+



Compile e inicie o backend

bashcd backend
mvn clean package -DskipTests
java -jar target/fisiocare.jar

Você verá a seguinte mensagem:

=========================================
  FisioCare Backend rodando na porta 8080
  http://localhost:8080
=========================================

Inicie o frontend

Abra um novo terminal:

bashcd frontend
npm install
npm start

A janela do FisioCare abrirá automaticamente.

## 🔐 Perfis e Permissões

| Funcionalidade        | Administrador | Funcionário | Paciente |

| Ver Dashboard         | ✅ | ✅ | ✅ |
| Cadastrar Paciente    | ✅ | ✅ | ❌ |
| Editar Paciente       | ✅ | ✅ | ❌ |
| Remover Paciente      | ✅ | ❌ | ❌ |
| Cadastrar Funcionário | ✅ | ❌ | ❌ |
| Criar Agendamento     | ✅ | ✅ | ❌ |
| Atualizar Status      | ✅ | ✅ | ❌ |
| Registrar Sessão      | ✅ | ✅ | ❌ |
| Ver Evolução          | ✅ | ✅ | Apenas a própria |



🌐 Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| POST   | `/api/auth/login` | Realiza login |
| POST   | `/api/auth/logout` | Realiza logout |
| GET    | `/api/dashboard` | Estatísticas do sistema |
| GET    | `/api/pacientes` | Lista pacientes |
| POST   | `/api/pacientes` | Cria paciente |
| PUT    | `/api/pacientes/{id}` | Atualiza paciente |
| DELETE | `/api/pacientes/{id}` | Remove paciente |
| GET    | `/api/pacientes/cpf/{cpf}` | Busca por CPF |
| GET    | `/api/funcionarios` | Lista funcionários |
| POST   | `/api/funcionarios` | Cria funcionário |
| GET    | `/api/agendamentos` | Lista agendamentos |
| GET    | `/api/agendamentos/hoje` | Agendamentos do dia |
| POST   | `/api/agendamentos` | Cria agendamento |
| PUT    | `/api/agendamentos/{id}` | Atualiza status |
| GET    | `/api/sessoes/paciente/{id}` | Sessões por paciente |
| POST   | `/api/sessoes` | Registra sessão |


🎓 Informações Acadêmicas


Instituição: FATEC
Curso: Análise e Desenvolvimento de Sistemas
Semestre: 3º Semestre
Ano: 2026
