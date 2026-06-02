/* ================================================
   FisioCare - App Controller
   ================================================ */
const App = {

    usuario: null,

    // ─── Inicialização ────────────────────────────
    init() {
        const token = localStorage.getItem('fc_token');
        const uStr  = localStorage.getItem('fc_usuario');
        if (token && uStr) {
            try {
                this.usuario = JSON.parse(uStr);
                Api.setToken(token);
                this.entrar();
                return;
            } catch(e) { /* ignora */ }
        }
        document.getElementById('login-senha')
            .addEventListener('keydown', e => { if (e.key === 'Enter') this.login(); });
        document.getElementById('login-email')
            .addEventListener('keydown', e => { if (e.key === 'Enter') this.login(); });
    },

    // ─── Login ────────────────────────────────────
    async login() {
        const email  = document.getElementById('login-email').value.trim();
        const senha  = document.getElementById('login-senha').value;
        const erroEl = document.getElementById('login-erro');
        erroEl.style.display = 'none';

        if (!email || !senha) {
            erroEl.textContent = 'Preencha e-mail e senha.';
            erroEl.style.display = 'block';
            return;
        }

        try {
            const data = await Api.login(email, senha);
            Api.setToken(data.token);
            this.usuario = { id: data.id, nome: data.nome, perfil: data.perfil };
            localStorage.setItem('fc_usuario', JSON.stringify(this.usuario));
            this.entrar();
        } catch(e) {
            erroEl.textContent = e.message || 'E-mail ou senha incorretos.';
            erroEl.style.display = 'block';
        }
    },

    // ─── Entrar no sistema ────────────────────────
    entrar() {
        document.getElementById('login-screen').style.display = 'none';
        document.getElementById('app').style.display = 'flex';

        const u = this.usuario;
        document.getElementById('sb-nome').textContent   = u.nome;
        document.getElementById('sb-perfil').textContent = u.perfil;
        document.getElementById('sb-avatar').textContent = u.nome.charAt(0).toUpperCase();

        // Valida e oculta os menus diretamente via JS usando a função podeAcessar
        document.querySelectorAll('.nav-item[data-page]').forEach(el => {
            const page = el.dataset.page;
            if (!this.podeAcessar(page)) {
                el.style.display = 'none'; // Esconde menus não autorizados
            } else {
                el.style.display = 'block';
                // Remove listeners duplicados recriando o elemento
                const novoEl = el.cloneNode(true);
                el.replaceWith(novoEl);
                novoEl.addEventListener('click', () => this.navegar(novoEl.dataset.page));
            }
        });

        this.navegar('dashboard');
    },

    // ─── Guarda de permissão ──────────────────────
    podeAcessar(page) {
        const perfil = this.usuario?.perfil;
        // Paciente: apenas dashboard e evolução
        const rotasPaciente = ['dashboard', 'evolucao'];
        if (perfil === 'PACIENTE' && !rotasPaciente.includes(page)) return false;
        // Funcionário: não acessa cadastro de funcionários
        if (perfil === 'FUNCIONARIO' && page === 'funcionarios') return false;
        return true;
    },

    // ─── Navegação entre páginas ──────────────────
    navegar(page) {
        if (!this.podeAcessar(page)) {
            Toast.show('Você não tem permissão para acessar esta área.', 'error');
            return;
        }

        document.querySelectorAll('.nav-item').forEach(el => {
            el.classList.toggle('active', el.dataset.page === page);
        });

        const container = document.getElementById('page-container');
        container.innerHTML = '<div class="loader"><div class="spinner"></div> Carregando...</div>';

        switch(page) {
            case 'dashboard':    Pages.dashboard(container);    break;
            case 'pacientes':    Pages.pacientes(container);    break;
            case 'funcionarios': Pages.funcionarios(container); break;
            case 'agendamentos': Pages.agendamentos(container); break;
            case 'evolucao':     Pages.evolucao(container);     break;
        }
    },

    // ─── Logout ───────────────────────────────────
    async logout() {
        try { await Api.logout(); } catch(e) { /* ignora */ }
        Api.setToken(null);
        localStorage.removeItem('fc_usuario');
        this.usuario = null;
        document.getElementById('app').style.display = 'none';
        document.getElementById('login-screen').style.display = 'flex';
        document.getElementById('login-email').value = '';
        document.getElementById('login-senha').value = '';
    }
};

// ─── Utilitários globais ──────────────────────────
const Toast = {
    show(msg, tipo = 'success', ms = 3000) {
        const t = document.getElementById('toast');
        t.textContent = msg;
        t.className = `toast ${tipo}`;
        t.style.display = 'block';
        clearTimeout(this._timer);
        this._timer = setTimeout(() => t.style.display = 'none', ms);
    }
};

function formatarData(str) {
    if (!str) return '-';
    const d = new Date(str);
    return isNaN(d) ? str : d.toLocaleDateString('pt-BR');
}

function formatarDataHora(str) {
    if (!str) return '-';
    const d = new Date(str);
    return isNaN(d) ? str : d.toLocaleString('pt-BR');
}

function badgeStatus(status) {
    const map = {
        AGENDADA:   'badge-info',
        REALIZADA:  'badge-success',
        CANCELADA:  'badge-danger',
        MELHORANDO: 'badge-success',
        ESTAVEL:    'badge-warning',
        PIORANDO:   'badge-danger'
    };
    return `<span class="badge ${map[status] || 'badge-info'}">${status || '-'}</span>`;
}

function confirmar(msg) {
    return confirm(msg);
}

document.addEventListener('DOMContentLoaded', () => App.init());
