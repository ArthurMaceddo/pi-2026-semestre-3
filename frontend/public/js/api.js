/* ================================================
   FisioCare - Serviço de API
   Comunica com o backend Java na porta 8080
   ================================================ */
const API_BASE = 'http://localhost:8080/api';

const Api = {

    // ─── Token de sessão ─────────────────────────
    token: localStorage.getItem('fc_token') || null,

    setToken(t) {
        this.token = t;
        if (t) localStorage.setItem('fc_token', t);
        else   localStorage.removeItem('fc_token');
    },

    // ─── Requisição genérica ──────────────────────
    async req(method, path, body = null) {
        const opts = {
            method,
            headers: {
                'Content-Type': 'application/json',
                ...(this.token ? { Authorization: `Bearer ${this.token}` } : {})
            }
        };
        if (body) opts.body = JSON.stringify(body);

        const res = await fetch(API_BASE + path, opts);
        if (res.status === 204) return null;

        const data = await res.json().catch(() => ({}));
        if (!res.ok) throw new Error(data.erro || `Erro ${res.status}`);
        return data;
    },

    get:    (path)        => Api.req('GET',    path),
    post:   (path, body)  => Api.req('POST',   path, body),
    put:    (path, body)  => Api.req('PUT',    path, body),
    delete: (path)        => Api.req('DELETE', path),

    // ─── Auth ─────────────────────────────────────
    login:  (email, senha) => Api.post('/auth/login',  { email, senha }),
    logout: ()             => Api.post('/auth/logout', {}),

    // ─── Dashboard ────────────────────────────────
    dashboard: () => Api.get('/dashboard'),

    // ─── Pacientes ────────────────────────────────
    pacientes: {
        listar:        ()    => Api.get('/pacientes'),
        buscarPorId:   (id)  => Api.get(`/pacientes/${id}`),
        buscarPorCpf:      (cpf)   => Api.get(`/pacientes/cpf/${cpf}`),
        buscarPorUsuarioId:(uid)   => Api.get(`/pacientes/usuario/${uid}`),
        criar:         (d)   => Api.post('/pacientes', d),
        atualizar:     (id, d) => Api.put(`/pacientes/${id}`, d),
        deletar:       (id)  => Api.delete(`/pacientes/${id}`)
    },

    // ─── Funcionários ─────────────────────────────
    funcionarios: {
        listar:    ()       => Api.get('/funcionarios'),
        criar:     (d)      => Api.post('/funcionarios', d),
        atualizar: (id, d)  => Api.put(`/funcionarios/${id}`, d),
        deletar:   (id)     => Api.delete(`/funcionarios/${id}`)
    },

    // ─── Agendamentos ─────────────────────────────
    agendamentos: {
        listar:   ()       => Api.get('/agendamentos'),
        hoje:     ()       => Api.get('/agendamentos/hoje'),
        criar:    (d)      => Api.post('/agendamentos', d),
        status:   (id, s)  => Api.put(`/agendamentos/${id}`, { status: s }),
        deletar:  (id)     => Api.delete(`/agendamentos/${id}`)
    },

    // ─── Sessões ──────────────────────────────────
    sessoes: {
        listar:         ()    => Api.get('/sessoes'),
        porPaciente:    (id)  => Api.get(`/sessoes/paciente/${id}`),
        criar:          (d)   => Api.post('/sessoes', d),
        deletar:        (id)  => Api.delete(`/sessoes/${id}`)
    }
};