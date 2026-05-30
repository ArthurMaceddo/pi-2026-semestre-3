-- ============================================
-- FisioCare - Script completo de banco de dados
-- Execute: psql -U postgres -f database.sql
-- ============================================

-- Criar banco
DROP DATABASE IF EXISTS fisiocare_db;
CREATE DATABASE fisiocare_db ENCODING 'UTF8';
\c fisiocare_db;

-- ============================================
-- TABELA: usuarios
-- ============================================
CREATE TABLE usuarios (
                          id          SERIAL PRIMARY KEY,
                          nome        VARCHAR(150) NOT NULL,
                          email       VARCHAR(100) NOT NULL UNIQUE,
                          cpf         VARCHAR(14)  NOT NULL UNIQUE,
                          telefone    VARCHAR(20)  NOT NULL,
                          data_nasc   DATE         NOT NULL,
                          endereco    TEXT         NOT NULL,
                          senha       VARCHAR(255) NOT NULL,
                          perfil      VARCHAR(20)  NOT NULL CHECK (perfil IN ('ADMINISTRADOR','FUNCIONARIO','PACIENTE')),
                          ativo       BOOLEAN      DEFAULT TRUE,
                          criado_em   TIMESTAMP    DEFAULT NOW()
);

-- ============================================
-- TABELA: pacientes
-- ============================================
CREATE TABLE pacientes (
                           id              SERIAL PRIMARY KEY,
                           usuario_id      INTEGER NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
                           problema        VARCHAR(255) NOT NULL,
                           tratamento      VARCHAR(150) NOT NULL,
                           inicio_trat     DATE         DEFAULT CURRENT_DATE,
                           observacoes     TEXT,
                           ativo           BOOLEAN      DEFAULT TRUE,
                           criado_em       TIMESTAMP    DEFAULT NOW()
);

-- ============================================
-- TABELA: agendamentos
-- ============================================
CREATE TABLE agendamentos (
                              id              SERIAL PRIMARY KEY,
                              paciente_id     INTEGER NOT NULL REFERENCES pacientes(id) ON DELETE CASCADE,
                              fisio_id        INTEGER NOT NULL REFERENCES usuarios(id),
                              data_hora       TIMESTAMP    NOT NULL,
                              tratamento      VARCHAR(150) NOT NULL,
                              qtd_sessoes     INTEGER      NOT NULL DEFAULT 1,
                              status          VARCHAR(20)  DEFAULT 'AGENDADA' CHECK (status IN ('AGENDADA','REALIZADA','CANCELADA')),
                              observacoes     TEXT,
                              criado_em       TIMESTAMP    DEFAULT NOW()
);

-- ============================================
-- TABELA: sessoes
-- ============================================
CREATE TABLE sessoes (
                         id              SERIAL PRIMARY KEY,
                         agendamento_id  INTEGER NOT NULL REFERENCES agendamentos(id) ON DELETE CASCADE,
                         paciente_id     INTEGER NOT NULL REFERENCES pacientes(id),
                         data_sessao     DATE         NOT NULL DEFAULT CURRENT_DATE,
                         dor_antes       INTEGER      CHECK (dor_antes BETWEEN 0 AND 10),
                         dor_depois      INTEGER      CHECK (dor_depois BETWEEN 0 AND 10),
                         mob_antes       VARCHAR(50),
                         mob_depois      VARCHAR(50),
                         descricao       TEXT,
                         exercicios      TEXT,
                         avaliacao       TEXT,
                         observacoes     TEXT,
                         evolucao        VARCHAR(20)  CHECK (evolucao IN ('MELHORANDO','ESTAVEL','PIORANDO')),
                         criado_em       TIMESTAMP    DEFAULT NOW()
);

-- ============================================
-- ÍNDICES
-- ============================================
CREATE INDEX idx_usuarios_cpf    ON usuarios(cpf);
CREATE INDEX idx_usuarios_email  ON usuarios(email);
CREATE INDEX idx_pacientes_usuario ON pacientes(usuario_id);
CREATE INDEX idx_agendamentos_data  ON agendamentos(data_hora);
CREATE INDEX idx_sessoes_paciente   ON sessoes(paciente_id);

-- ============================================
-- DADOS DE TESTE
-- ============================================

-- Admin (senha: admin123)
INSERT INTO usuarios (nome, email, cpf, telefone, data_nasc, endereco, senha, perfil)
VALUES ('Administrador', 'admin@fisiocare.com', '000.000.000-00',
        '(11) 98765-4321', '1985-01-01', 'Rua Principal, 100',
        'jZae727K08KaOmKSgBqG40dKliP+2HpH5S8mKIUDPGk=', 'ADMINISTRADOR');

-- Funcionário (senha: fisio123)
INSERT INTO usuarios (nome, email, cpf, telefone, data_nasc, endereco, senha, perfil)
VALUES ('Dr. João Silva', 'joao@fisiocare.com', '111.111.111-11',
        '(11) 99999-8888', '1988-05-15', 'Av. Saúde, 250',
        'sy3sHCz1ZBjYZ4aBPUBPzFmIbgpQECpkPOZnBPVVXc4=', 'FUNCIONARIO');

-- Paciente (senha: maria123)
INSERT INTO usuarios (nome, email, cpf, telefone, data_nasc, endereco, senha, perfil)
VALUES ('Maria Santos', 'maria@email.com', '222.222.222-22',
        '(11) 97777-6666', '1995-08-20', 'Rua dos Ipês, 50',
        'X+B7YEIHPVNaRgcn3gznFcbM8rOhV1Fxk0WFQsKhFD0=', 'PACIENTE');

INSERT INTO pacientes (usuario_id, problema, tratamento, observacoes)
VALUES (3, 'Dor lombar crônica', 'Fisioterapia ortopédica', 'Paciente com histórico de hérnia de disco');

-- Agendamento de teste
INSERT INTO agendamentos (paciente_id, fisio_id, data_hora, tratamento, qtd_sessoes)
VALUES (1, 2, NOW() + interval '1 hour', 'Fisioterapia ortopédica', 10);

-- Sessão de teste
INSERT INTO sessoes (agendamento_id, paciente_id, data_sessao, dor_antes, dor_depois,
                     mob_antes, mob_depois, descricao, exercicios, evolucao)
VALUES (1, 1, CURRENT_DATE, 7, 4, 'Limitada', 'Regular',
        'Manipulação vertebral e alongamentos', 'Ponte, Bird-dog, Gato-camelo', 'MELHORANDO');

COMMIT;
