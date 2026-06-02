/* ================================================
   FisioCare - Página: Pacientes
   ================================================ */
Pages.pacientes = async function(container) {
    if(!App.podeAcessar('paciente')) return;
    container.innerHTML = `
    <div class="page-header">
      <h2>👥 Pacientes</h2>
      <button class="btn btn-primary btn-sm" onclick="Pacientes.abrirFormNovo()">
        + Novo Paciente
      </button>
    </div>
    <div class="page-body">
      <div class="search-bar">
        <input type="text" id="busca-paciente" placeholder="🔍 Buscar por nome ou CPF..."
               oninput="Pacientes.filtrar(this.value)"/>
      </div>
      <div class="card">
        <div id="tabela-pacientes" class="table-wrap">
          <div class="loader"><div class="spinner"></div> Carregando...</div>
        </div>
      </div>
    </div>

    <!-- Modal Formulário -->
    <div id="modal-paciente" class="modal-overlay" style="display:none">
      <div class="modal">
        <div class="modal-header">
          <h3 id="modal-pac-titulo">Novo Paciente</h3>
          <button class="modal-close" onclick="Pacientes.fecharModal()">✕</button>
        </div>
        <div class="form-grid">
          <div class="form-group">
            <label>Nome Completo *</label>
            <input type="text" id="pac-nome" placeholder="Nome completo"/>
          </div>
          <div class="form-group">
            <label>CPF *</label>
            <input type="text" id="pac-cpf" placeholder="000.000.000-00" maxlength="14"
                   oninput="Pacientes.mascaraCpf(this)"/>
          </div>
          <div class="form-group">
            <label>Data de Nascimento *</label>
            <input type="date" id="pac-nasc"/>
          </div>
          <div class="form-group">
            <label>Telefone *</label>
            <input type="text" id="pac-tel" placeholder="(00) 00000-0000"/>
          </div>
          <div class="form-group">
            <label>E-mail *</label>
            <input type="email" id="pac-email" placeholder="email@exemplo.com"/>
          </div>
          <div class="form-group">
            <label>Tipo de Tratamento *</label>
            <select id="pac-tratamento">
              <option value="">Selecione...</option>
              <option>Fisioterapia Ortopédica</option>
              <option>Fisioterapia Neurológica</option>
              <option>Fisioterapia Esportiva</option>
              <option>Fisioterapia Respiratória</option>
              <option>Fisioterapia Pediátrica</option>
              <option>Fisioterapia Geriátrica</option>
              <option>RPG</option>
              <option>Pilates Terapêutico</option>
              <option>Outro</option>
            </select>
          </div>
          <div class="form-group full">
            <label>Endereço *</label>
            <input type="text" id="pac-end" placeholder="Rua, número, bairro, cidade"/>
          </div>
          <div class="form-group full">
            <label>Problema Principal *</label>
            <textarea id="pac-problema" placeholder="Descreva o problema que levou o paciente à fisioterapia..."></textarea>
          </div>
          <div class="form-group full">
            <label>Observações</label>
            <textarea id="pac-obs" placeholder="Observações adicionais..."></textarea>
          </div>
          <div class="form-group" id="pac-senha-group">
            <label>Senha (acesso ao sistema)</label>
            <input type="password" id="pac-senha" placeholder="Deixe em branco para usar o CPF"/>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-primary" onclick="Pacientes.salvar()">💾 Salvar</button>
          <button class="btn btn-secondary" onclick="Pacientes.fecharModal()">Cancelar</button>
        </div>
      </div>
    </div>
  `;

    await Pacientes.carregar();
};

const Pacientes = {
    lista: [],
    editandoId: null,

    async carregar() {
        try {
            this.lista = await Api.pacientes.listar();
            this.renderTabela(this.lista);
        } catch(e) {
            document.getElementById('tabela-pacientes').innerHTML =
                `<div class="empty-state"><div class="icon">⚠️</div><p>${e.message}</p></div>`;
        }
    },

    renderTabela(dados) {
        const el = document.getElementById('tabela-pacientes');
        if (!dados || dados.length === 0) {
            el.innerHTML = `<div class="empty-state"><div class="icon">👥</div><p>Nenhum paciente cadastrado.</p></div>`;
            return;
        }
        el.innerHTML = `
      <table>
        <thead>
          <tr>
            <th>Nome</th>
            <th>CPF</th>
            <th>Telefone</th>
            <th>Tratamento</th>
            <th>Problema Principal</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${dados.map(p => `
            <tr>
              <td><strong>${p.nome || '-'}</strong></td>
              <td>${p.cpf || '-'}</td>
              <td>${p.telefone || '-'}</td>
              <td>${p.tratamento || '-'}</td>
              <td style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap"
                  title="${p.problema || ''}">${p.problema || '-'}</td>
              <td>
                <button class="btn btn-secondary btn-sm" onclick="Pacientes.abrirFormEditar(${p.id})">✏️ Editar</button>
                ${App.usuario.perfil === 'ADMINISTRADOR'
            ? `<button class="btn btn-danger btn-sm" style="margin-left:6px" onclick="Pacientes.deletar(${p.id})">🗑️</button>`
            : ''}
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>`;
    },

    filtrar(texto) {
        const q = texto.toLowerCase();
        const filtrado = this.lista.filter(p =>
            (p.nome  || '').toLowerCase().includes(q) ||
            (p.cpf   || '').toLowerCase().includes(q)
        );
        this.renderTabela(filtrado);
    },

    abrirFormNovo() {
        this.editandoId = null;
        document.getElementById('modal-pac-titulo').textContent = 'Novo Paciente';
        ['pac-nome','pac-cpf','pac-nasc','pac-tel','pac-email','pac-end',
            'pac-problema','pac-obs','pac-senha'].forEach(id => {
            document.getElementById(id).value = '';
        });
        document.getElementById('pac-tratamento').value = '';
        document.getElementById('modal-paciente').style.display = 'flex';
    },

    abrirFormEditar(id) {
        const p = this.lista.find(x => x.id === id);
        if (!p) return;
        this.editandoId = id;
        document.getElementById('modal-pac-titulo').textContent = 'Editar Paciente';
        document.getElementById('pac-nome').value      = p.nome || '';
        document.getElementById('pac-cpf').value       = p.cpf  || '';
        document.getElementById('pac-nasc').value      = p.dataNasc || '';
        document.getElementById('pac-tel').value       = p.telefone || '';
        document.getElementById('pac-email').value     = p.email || '';
        document.getElementById('pac-end').value       = p.endereco || '';
        document.getElementById('pac-tratamento').value= p.tratamento || '';
        document.getElementById('pac-problema').value  = p.problema || '';
        document.getElementById('pac-obs').value       = p.observacoes || '';
        document.getElementById('pac-senha').value     = '';
        document.getElementById('modal-paciente').style.display = 'flex';
    },

    fecharModal() {
        document.getElementById('modal-paciente').style.display = 'none';
        this.editandoId = null;
    },

    async salvar() {
        const dados = {
            nome:       document.getElementById('pac-nome').value.trim(),
            cpf:        document.getElementById('pac-cpf').value.trim(),
            dataNasc:   document.getElementById('pac-nasc').value,
            telefone:   document.getElementById('pac-tel').value.trim(),
            email:      document.getElementById('pac-email').value.trim(),
            endereco:   document.getElementById('pac-end').value.trim(),
            tratamento: document.getElementById('pac-tratamento').value,
            problema:   document.getElementById('pac-problema').value.trim(),
            observacoes:document.getElementById('pac-obs').value.trim(),
            senha:      document.getElementById('pac-senha').value
        };

        if (!dados.nome || !dados.cpf || !dados.dataNasc || !dados.telefone ||
            !dados.email || !dados.endereco || !dados.tratamento || !dados.problema) {
            Toast.show('Preencha todos os campos obrigatórios.', 'error'); return;
        }

        try {
            if (this.editandoId) {
                await Api.pacientes.atualizar(this.editandoId, dados);
                Toast.show('Paciente atualizado com sucesso!');
            } else {
                await Api.pacientes.criar(dados);
                Toast.show('Paciente cadastrado com sucesso!');
            }
            this.fecharModal();
            await this.carregar();
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    },

    async deletar(id) {
        if (!confirmar('Deseja realmente remover este paciente?')) return;
        try {
            await Api.pacientes.deletar(id);
            Toast.show('Paciente removido.');
            await this.carregar();
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    },

    mascaraCpf(input) {
        let v = input.value.replace(/\D/g, '').slice(0, 11);
        if (v.length > 9)      v = v.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/,  '$1.$2.$3-$4');
        else if (v.length > 6) v = v.replace(/(\d{3})(\d{3})(\d+)/,            '$1.$2.$3');
        else if (v.length > 3) v = v.replace(/(\d{3})(\d+)/,                   '$1.$2');
        input.value = v;
    }
};
