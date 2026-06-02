/* ================================================
   FisioCare - Página: Funcionários (ADMIN)
   ================================================ */
Pages.funcionarios = async function(container) {
    if (App.usuario.perfil !== 'ADMINISTRADOR') {
        container.innerHTML = `
      <div class="page-header"><h2>👔 Funcionários</h2></div>
      <div class="page-body">
        <div class="empty-state">
          <div class="icon">🔒</div>
          <p>Acesso restrito a administradores.</p>
        </div>
      </div>`;
        return;
    }

    container.innerHTML = `
    <div class="page-header">
      <h2>👔 Funcionários</h2>
      <button class="btn btn-primary btn-sm" onclick="Funcionarios.abrirFormNovo()">
        + Novo Funcionário
      </button>
    </div>
    <div class="page-body">
      <div class="search-bar">
        <input type="text" id="busca-func" placeholder="🔍 Buscar por nome ou CPF..."
               oninput="Funcionarios.filtrar(this.value)"/>
      </div>
      <div class="card">
        <div id="tabela-funcionarios" class="table-wrap">
          <div class="loader"><div class="spinner"></div> Carregando...</div>
        </div>
      </div>
    </div>

    <!-- Modal Formulário -->
    <div id="modal-func" class="modal-overlay" style="display:none">
      <div class="modal">
        <div class="modal-header">
          <h3 id="modal-func-titulo">Novo Funcionário</h3>
          <button class="modal-close" onclick="Funcionarios.fecharModal()">✕</button>
        </div>
        <div class="form-grid">
          <div class="form-group">
            <label>Nome Completo *</label>
            <input type="text" id="func-nome" placeholder="Nome completo"/>
          </div>
          <div class="form-group">
            <label>CPF *</label>
            <input type="text" id="func-cpf" placeholder="000.000.000-00" maxlength="14"
                   oninput="Pacientes.mascaraCpf(this)"/>
          </div>
          <div class="form-group">
            <label>Data de Nascimento *</label>
            <input type="date" id="func-nasc"/>
          </div>
          <div class="form-group">
            <label>Telefone *</label>
            <input type="text" id="func-tel" placeholder="(00) 00000-0000"/>
          </div>
          <div class="form-group">
            <label>E-mail *</label>
            <input type="email" id="func-email" placeholder="email@exemplo.com"/>
          </div>
          <div class="form-group">
            <label>Perfil *</label>
            <select id="func-perfil">
              <option value="FUNCIONARIO">Funcionário</option>
              <option value="ADMINISTRADOR">Administrador</option>
            </select>
          </div>
          <div class="form-group full">
            <label>Endereço *</label>
            <input type="text" id="func-end" placeholder="Rua, número, bairro, cidade"/>
          </div>
          <div class="form-group">
            <label>Senha *</label>
            <input type="password" id="func-senha" placeholder="Senha de acesso"/>
          </div>
          <div class="form-group">
            <label>Confirmar Senha *</label>
            <input type="password" id="func-senha2" placeholder="Repita a senha"/>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-primary" onclick="Funcionarios.salvar()">💾 Salvar</button>
          <button class="btn btn-secondary" onclick="Funcionarios.fecharModal()">Cancelar</button>
        </div>
      </div>
    </div>
  `;

    await Funcionarios.carregar();
};

const Funcionarios = {
    lista: [],
    editandoId: null,

    async carregar() {
        try {
            this.lista = await Api.funcionarios.listar();
            this.renderTabela(this.lista);
        } catch(e) {
            document.getElementById('tabela-funcionarios').innerHTML =
                `<div class="empty-state"><div class="icon">⚠️</div><p>${e.message}</p></div>`;
        }
    },

    renderTabela(dados) {
        const el = document.getElementById('tabela-funcionarios');
        if (!dados || dados.length === 0) {
            el.innerHTML = `<div class="empty-state"><div class="icon">👔</div><p>Nenhum funcionário cadastrado.</p></div>`;
            return;
        }
        el.innerHTML = `
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>CPF</th>
            <th>E-mail</th>
            <th>Telefone</th>
            <th>Perfil</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${dados.map(f => `
            <tr>
              <td><strong>${f.nome || '-'}</strong></td>
              <td>${f.cpf || '-'}</td>
              <td>${f.email || '-'}</td>
              <td>${f.telefone || '-'}</td>
              <td>${badgeStatus(f.perfil)}</td>
              <td>
                <button class="btn btn-secondary btn-sm" onclick="Funcionarios.abrirFormEditar(${f.id})">✏️ Editar</button>
                ${f.id !== App.usuario.id
            ? `<button class="btn btn-danger btn-sm" style="margin-left:6px" onclick="Funcionarios.deletar(${f.id})">🗑️</button>`
            : ''}
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>`;
    },

    filtrar(texto) {
        const q = texto.toLowerCase();
        this.renderTabela(this.lista.filter(f =>
            (f.nome || '').toLowerCase().includes(q) ||
            (f.cpf  || '').toLowerCase().includes(q)
        ));
    },

    abrirFormNovo() {
        this.editandoId = null;
        document.getElementById('modal-func-titulo').textContent = 'Novo Funcionário';
        ['func-nome','func-cpf','func-nasc','func-tel','func-email','func-end','func-senha','func-senha2']
            .forEach(id => document.getElementById(id).value = '');
        document.getElementById('func-perfil').value = 'FUNCIONARIO';
        document.getElementById('modal-func').style.display = 'flex';
    },

    abrirFormEditar(id) {
        const f = this.lista.find(x => x.id === id);
        if (!f) return;
        this.editandoId = id;
        document.getElementById('modal-func-titulo').textContent = 'Editar Funcionário';
        document.getElementById('func-nome').value   = f.nome || '';
        document.getElementById('func-cpf').value    = f.cpf  || '';
        document.getElementById('func-nasc').value   = f.dataNasc || '';
        document.getElementById('func-tel').value    = f.telefone || '';
        document.getElementById('func-email').value  = f.email || '';
        document.getElementById('func-end').value    = f.endereco || '';
        document.getElementById('func-perfil').value = f.perfil || 'FUNCIONARIO';
        document.getElementById('func-senha').value  = '';
        document.getElementById('func-senha2').value = '';
        document.getElementById('modal-func').style.display = 'flex';
    },

    fecharModal() {
        document.getElementById('modal-func').style.display = 'none';
        this.editandoId = null;
    },

    async salvar() {
        const nome   = document.getElementById('func-nome').value.trim();
        const cpf    = document.getElementById('func-cpf').value.trim();
        const nasc   = document.getElementById('func-nasc').value;
        const tel    = document.getElementById('func-tel').value.trim();
        const email  = document.getElementById('func-email').value.trim();
        const end    = document.getElementById('func-end').value.trim();
        const perfil = document.getElementById('func-perfil').value;
        const senha  = document.getElementById('func-senha').value;
        const senha2 = document.getElementById('func-senha2').value;

        if (!nome || !cpf || !nasc || !tel || !email || !end) {
            Toast.show('Preencha todos os campos obrigatórios.', 'error'); return;
        }
        if (!this.editandoId && !senha) {
            Toast.show('Informe a senha.', 'error'); return;
        }
        if (senha && senha !== senha2) {
            Toast.show('As senhas não coincidem.', 'error'); return;
        }

        const dados = { nome, cpf, dataNasc: nasc, telefone: tel, email, endereco: end, perfil };
        if (senha) dados.senha = senha;

        try {
            if (this.editandoId) {
                await Api.funcionarios.atualizar(this.editandoId, dados);
                Toast.show('Funcionário atualizado!');
            } else {
                await Api.funcionarios.criar(dados);
                Toast.show('Funcionário cadastrado!');
            }
            this.fecharModal();
            await this.carregar();
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    },

    async deletar(id) {
        if (!confirmar('Deseja realmente remover este funcionário?')) return;
        try {
            await Api.funcionarios.deletar(id);
            Toast.show('Funcionário removido.');
            await this.carregar();
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    }
};
