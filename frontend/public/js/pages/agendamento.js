/* ================================================
   FisioCare - Página: Agendamentos
   ================================================ */
Pages.agendamentos = async function(container) {
    if(!App.podeAcessar('agendamentos')) return;
    container.innerHTML = `
    <div class="page-header">
      <h2>📅 Agendamentos</h2>
      <button class="btn btn-primary btn-sm" onclick="Agendamentos.abrirModal()">
        + Novo Agendamento
      </button>
    </div>
    <div class="page-body">
      <div class="card">
        <div class="card-header">
          <h3>Todos os Agendamentos</h3>
          <div style="display:flex;gap:8px">
            <input type="date" id="filtro-data" onchange="Agendamentos.filtrarData(this.value)"
                   style="padding:6px 10px;border:1.5px solid var(--border);border-radius:8px;font-size:.85rem"/>
            <button class="btn btn-secondary btn-sm" onclick="Agendamentos.filtrarData('')">
              Limpar filtro
            </button>
          </div>
        </div>
        <div id="tabela-agendamentos" class="table-wrap">
          <div class="loader"><div class="spinner"></div> Carregando...</div>
        </div>
      </div>
    </div>

    <!-- Modal Novo Agendamento -->
    <div id="modal-agend" class="modal-overlay" style="display:none">
      <div class="modal">
        <div class="modal-header">
          <h3>📅 Novo Agendamento</h3>
          <button class="modal-close" onclick="Agendamentos.fecharModal()">✕</button>
        </div>

        <!-- Busca por CPF -->
        <div style="background:#f0f4f8;border-radius:10px;padding:16px;margin-bottom:18px">
          <label style="font-size:.82rem;font-weight:600;color:var(--text-muted)">
            Buscar paciente por CPF
          </label>
          <div style="display:flex;gap:8px;margin-top:6px">
            <input type="text" id="agend-cpf-busca" placeholder="000.000.000-00" maxlength="14"
                   style="flex:1" oninput="Pacientes.mascaraCpf(this)"
                   onkeydown="if(event.key==='Enter') Agendamentos.buscarPorCpf()"/>
            <button class="btn btn-secondary btn-sm" onclick="Agendamentos.buscarPorCpf()">🔍 Buscar</button>
          </div>
          <div id="agend-paciente-encontrado" style="margin-top:8px;font-size:.85rem;color:var(--success);display:none">
            ✅ Paciente encontrado: <strong id="agend-paciente-nome-info"></strong>
          </div>
        </div>

        <div class="form-grid">
          <!-- Select de pacientes -->
          <div class="form-group full">
            <label>Paciente *</label>
            <select id="agend-paciente">
              <option value="">Selecione o paciente...</option>
            </select>
          </div>
          <div class="form-group full">
            <label>Fisioterapeuta Responsável *</label>
            <select id="agend-fisio">
              <option value="">Selecione o fisioterapeuta...</option>
            </select>
          </div>
          <div class="form-group">
            <label>Tipo de Tratamento *</label>
            <select id="agend-tratamento">
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
          <div class="form-group">
            <label>Quantidade de Sessões *</label>
            <input type="number" id="agend-qtd" min="1" max="100" value="10"/>
          </div>
          <div class="form-group">
            <label>Data *</label>
            <input type="date" id="agend-data"
                   min="${new Date().toISOString().split('T')[0]}"/>
          </div>
          <div class="form-group">
            <label>Horário *</label>
            <input type="time" id="agend-hora" value="08:00"/>
          </div>
          <div class="form-group full">
            <label>Observações</label>
            <textarea id="agend-obs" placeholder="Observações sobre o agendamento..."></textarea>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-primary" onclick="Agendamentos.salvar()">💾 Agendar</button>
          <button class="btn btn-secondary" onclick="Agendamentos.fecharModal()">Cancelar</button>
        </div>
      </div>
    </div>
  `;

    await Agendamentos.carregar();
};

const Agendamentos = {
    lista: [],

    async carregar() {
        try {
            this.lista = await Api.agendamentos.listar();
            this.renderTabela(this.lista);
        } catch(e) {
            document.getElementById('tabela-agendamentos').innerHTML =
                `<div class="empty-state"><div class="icon">⚠️</div><p>${e.message}</p></div>`;
        }
    },

    renderTabela(dados) {
        const el = document.getElementById('tabela-agendamentos');
        if (!dados || dados.length === 0) {
            el.innerHTML = `<div class="empty-state"><div class="icon">📅</div><p>Nenhum agendamento encontrado.</p></div>`;
            return;
        }
        el.innerHTML = `
      <table>
        <thead>
          <tr>
            <th>Paciente</th>
            <th>Fisioterapeuta</th>
            <th>Data / Hora</th>
            <th>Tratamento</th>
            <th>Sessões</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          ${dados.map(a => `
            <tr>
              <td><strong>${a.pacienteNome || '-'}</strong><br>
                  <small style="color:var(--text-muted)">${a.pacienteCpf || ''}</small></td>
              <td>${a.fisioNome || '-'}</td>
              <td>${formatarDataHora(a.dataHora)}</td>
              <td>${a.tratamento}</td>
              <td style="text-align:center">${a.qtdSessoes}</td>
              <td>${badgeStatus(a.status)}</td>
              <td style="white-space:nowrap">
                ${a.status === 'AGENDADA' ? `
                  <button class="btn btn-success btn-sm" onclick="Agendamentos.mudarStatus(${a.id},'REALIZADA')">✔</button>
                  <button class="btn btn-danger btn-sm"  onclick="Agendamentos.mudarStatus(${a.id},'CANCELADA')">✖</button>
                ` : ''}
                ${App.usuario.perfil === 'ADMINISTRADOR'
            ? `<button class="btn btn-danger btn-sm" onclick="Agendamentos.deletar(${a.id})">🗑️</button>`
            : ''}
              </td>
            </tr>
          `).join('')}
        </tbody>
      </table>`;
    },

    filtrarData(data) {
        if (!data) { this.renderTabela(this.lista); return; }
        this.renderTabela(this.lista.filter(a => (a.dataHora || '').startsWith(data)));
    },

    async abrirModal() {
        // Carregar selects
        try {
            const [pacientes, fisios] = await Promise.all([
                Api.pacientes.listar(),
                Api.funcionarios.listar()
            ]);

            const selPac = document.getElementById('agend-paciente');
            selPac.innerHTML = '<option value="">Selecione o paciente...</option>' +
                pacientes.map(p => `<option value="${p.id}">${p.nome} - ${p.cpf}</option>`).join('');

            const selFisio = document.getElementById('agend-fisio');
            selFisio.innerHTML = '<option value="">Selecione o fisioterapeuta...</option>' +
                fisios.map(f => `<option value="${f.id}">${f.nome}</option>`).join('');

        } catch(e) { Toast.show('Erro ao carregar dados: ' + e.message, 'error'); }

        document.getElementById('modal-agend').style.display = 'flex';
    },

    async buscarPorCpf() {
        const cpf = document.getElementById('agend-cpf-busca').value.trim();
        if (!cpf) { Toast.show('Digite um CPF.', 'error'); return; }
        try {
            const p = await Api.pacientes.buscarPorCpf(cpf);
            document.getElementById('agend-paciente-nome-info').textContent = p.nome;
            document.getElementById('agend-paciente-encontrado').style.display = 'block';
            // Selecionar automaticamente no select
            document.getElementById('agend-paciente').value = p.id;
        } catch(e) {
            document.getElementById('agend-paciente-encontrado').style.display = 'none';
            Toast.show('Paciente não encontrado.', 'error');
        }
    },

    fecharModal() {
        document.getElementById('modal-agend').style.display = 'none';
        document.getElementById('agend-paciente-encontrado').style.display = 'none';
        document.getElementById('agend-cpf-busca').value = '';
    },

    async salvar() {
        const pacienteId  = document.getElementById('agend-paciente').value;
        const fisioId     = document.getElementById('agend-fisio').value;
        const tratamento  = document.getElementById('agend-tratamento').value;
        const qtd         = document.getElementById('agend-qtd').value;
        const data        = document.getElementById('agend-data').value;
        const hora        = document.getElementById('agend-hora').value;
        const obs         = document.getElementById('agend-obs').value;

        if (!pacienteId || !fisioId || !tratamento || !qtd || !data || !hora) {
            Toast.show('Preencha todos os campos obrigatórios.', 'error'); return;
        }

        const dataHora = `${data}T${hora}:00`;

        try {
            await Api.agendamentos.criar({
                pacienteId: +pacienteId,
                fisioId:    +fisioId,
                dataHora,
                tratamento,
                qtdSessoes: +qtd,
                observacoes: obs
            });
            Toast.show('Agendamento criado com sucesso!');
            this.fecharModal();
            await this.carregar();
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    },

    async mudarStatus(id, status) {
        const msgs = { REALIZADA: 'Marcar como realizada?', CANCELADA: 'Cancelar este agendamento?' };
        if (!confirmar(msgs[status])) return;
        try {
            await Api.agendamentos.status(id, status);
            Toast.show('Status atualizado!');
            await this.carregar();
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    },

    async deletar(id) {
        if (!confirmar('Excluir agendamento permanentemente?')) return;
        try {
            await Api.agendamentos.deletar(id);
            Toast.show('Agendamento excluído.');
            await this.carregar();
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    }
};
