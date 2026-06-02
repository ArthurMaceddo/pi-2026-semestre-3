/* ================================================
   FisioCare - Página: Evolução
   ================================================ */
Pages.evolucao = async function(container) {
    // ── Paciente: vai direto para a própria evolução ──
    if (App.usuario.perfil === 'PACIENTE') {
        container.innerHTML = `
      <div class="page-header"><h2>📈 Minha Evolução</h2></div>
      <div class="page-body">
        <div id="evolucao-detalhe">
          <div class="loader"><div class="spinner"></div> Carregando sua evolução...</div>
        </div>
      </div>
      ${modalSessaoHTML()}
    `;
        try {
            // Busca o registro de paciente pelo usuarioId do logado
            const pac = await Api.pacientes.buscarPorUsuarioId(App.usuario.id);
            if (!pac) {
                document.getElementById('evolucao-detalhe').innerHTML =
                    `<div class="empty-state"><div class="icon">⚠️</div>
           <p>Seu cadastro de paciente não foi encontrado.<br>Entre em contato com a clínica.</p></div>`;
                return;
            }
            const sessoes = await Api.sessoes.porPaciente(pac.id);
            Evolucao.pacienteSelecionado = pac;
            Evolucao.renderDetalhe(pac, sessoes, /* somenteVisualizacao */ true);
        } catch(e) {
            document.getElementById('evolucao-detalhe').innerHTML =
                `<div class="empty-state"><div class="icon">⚠️</div><p>${e.message}</p></div>`;
        }
        return;
    }

    // ── Staff/Admin: lista todos os pacientes ──────────
    container.innerHTML = `
    <div class="page-header">
      <h2>📈 Evolução dos Pacientes</h2>
    </div>
    <div class="page-body" style="padding:0;height:calc(100vh - 66px);overflow:hidden">
      <div class="evolucao-grid">

        <div class="card paciente-lista" style="border-radius:0;box-shadow:none;border-right:1px solid var(--border)">
          <div class="card-header" style="position:sticky;top:0;z-index:1;background:var(--surface)">
            <h3>Pacientes</h3>
          </div>
          <input type="text" placeholder="🔍 Buscar..." style="margin:10px;width:calc(100% - 20px)"
                 oninput="Evolucao.filtrar(this.value)"/>
          <div id="lista-pac-evol">
            <div class="loader"><div class="spinner"></div> Carregando...</div>
          </div>
        </div>

        <div class="evolucao-detail" id="evolucao-detalhe" style="padding:20px">
          <div class="empty-state" style="margin-top:60px">
            <div class="icon">👈</div>
            <p>Selecione um paciente para ver a evolução.</p>
          </div>
        </div>

      </div>
    </div>
    ${modalSessaoHTML()}
  `;

    await Evolucao.carregarPacientes();
};

// ─── HTML do modal de sessão (reutilizado nos dois modos) ──
function modalSessaoHTML() {
    return `
  <div id="modal-sessao" class="modal-overlay" style="display:none">
    <div class="modal">
      <div class="modal-header">
        <h3>📋 Registrar Sessão</h3>
        <button class="modal-close" onclick="Evolucao.fecharModalSessao()">✕</button>
      </div>
      <div class="form-grid">
        <div class="form-group">
          <label>Data da Sessão *</label>
          <input type="date" id="sess-data" value="${new Date().toISOString().split('T')[0]}"/>
        </div>
        <div class="form-group">
          <label>Agendamento *</label>
          <select id="sess-agend"><option value="">Selecione...</option></select>
        </div>
        <div class="form-group">
          <label>Escala de Dor ANTES (0-10)</label>
          <input type="range" id="sess-dor-antes" min="0" max="10" value="5"
                 oninput="document.getElementById('sess-dor-antes-v').textContent=this.value"/>
          <span id="sess-dor-antes-v" style="font-size:.8rem;color:var(--primary)">5</span>
        </div>
        <div class="form-group">
          <label>Escala de Dor DEPOIS (0-10)</label>
          <input type="range" id="sess-dor-depois" min="0" max="10" value="3"
                 oninput="document.getElementById('sess-dor-depois-v').textContent=this.value"/>
          <span id="sess-dor-depois-v" style="font-size:.8rem;color:var(--success)">3</span>
        </div>
        <div class="form-group">
          <label>Mobilidade ANTES</label>
          <select id="sess-mob-antes">
            <option value="">Selecione...</option>
            <option>Normal</option><option>Regular</option>
            <option>Limitada</option><option>Muito Limitada</option><option>Ausente</option>
          </select>
        </div>
        <div class="form-group">
          <label>Mobilidade DEPOIS</label>
          <select id="sess-mob-depois">
            <option value="">Selecione...</option>
            <option>Normal</option><option>Regular</option>
            <option>Limitada</option><option>Muito Limitada</option><option>Ausente</option>
          </select>
        </div>
        <div class="form-group full">
          <label>Descrição do Tratamento *</label>
          <textarea id="sess-desc" placeholder="Descreva os procedimentos realizados..."></textarea>
        </div>
        <div class="form-group full">
          <label>Exercícios Realizados</label>
          <textarea id="sess-exerc" placeholder="Liste os exercícios..."></textarea>
        </div>
        <div class="form-group full">
          <label>Avaliação do Fisioterapeuta</label>
          <textarea id="sess-aval" placeholder="Avaliação clínica..."></textarea>
        </div>
        <div class="form-group">
          <label>Evolução Geral</label>
          <select id="sess-evolucao">
            <option value="">Selecione...</option>
            <option value="MELHORANDO">✅ Melhorando</option>
            <option value="ESTAVEL">➡️ Estável</option>
            <option value="PIORANDO">⬇️ Piorando</option>
          </select>
        </div>
        <div class="form-group full">
          <label>Observações</label>
          <textarea id="sess-obs" placeholder="Outras observações..."></textarea>
        </div>
      </div>
      <div class="form-actions">
        <button class="btn btn-primary" onclick="Evolucao.salvarSessao()">💾 Registrar Sessão</button>
        <button class="btn btn-secondary" onclick="Evolucao.fecharModalSessao()">Cancelar</button>
      </div>
    </div>
  </div>`;
}

const Evolucao = {
    pacientes: [],
    pacienteSelecionado: null,
    chartDor: null,

    async carregarPacientes() {
        try {
            this.pacientes = await Api.pacientes.listar();
            this.renderListaPacientes(this.pacientes);
        } catch(e) {
            document.getElementById('lista-pac-evol').innerHTML =
                `<div class="empty-state"><div class="icon">⚠️</div><p>${e.message}</p></div>`;
        }
    },

    renderListaPacientes(lista) {
        const el = document.getElementById('lista-pac-evol');
        if (!lista || lista.length === 0) {
            el.innerHTML = `<div class="empty-state"><div class="icon">👥</div><p>Nenhum paciente.</p></div>`;
            return;
        }
        el.innerHTML = lista.map(p => `
      <div class="paciente-item ${this.pacienteSelecionado?.id === p.id ? 'active' : ''}"
           onclick="Evolucao.selecionarPaciente(${p.id})">
        <h4>${p.nome}</h4>
        <p>${p.tratamento || '-'}</p>
      </div>`).join('');
    },

    filtrar(texto) {
        const q = texto.toLowerCase();
        this.renderListaPacientes(this.pacientes.filter(p =>
            (p.nome || '').toLowerCase().includes(q)
        ));
    },

    async selecionarPaciente(id) {
        this.pacienteSelecionado = this.pacientes.find(p => p.id === id);
        if (!this.pacienteSelecionado) return;
        this.renderListaPacientes(this.pacientes);

        const detalhe = document.getElementById('evolucao-detalhe');
        detalhe.innerHTML = '<div class="loader"><div class="spinner"></div> Carregando...</div>';

        try {
            const sessoes = await Api.sessoes.porPaciente(id);
            this.renderDetalhe(this.pacienteSelecionado, sessoes, false);
        } catch(e) {
            detalhe.innerHTML = `<div class="empty-state"><div class="icon">⚠️</div><p>${e.message}</p></div>`;
        }
    },

    // somenteVisualizacao = true oculta o botão "Nova Sessão" para pacientes
    renderDetalhe(p, sessoes, somenteVisualizacao = false) {
        const podeRegistrar = !somenteVisualizacao &&
            (App.usuario.perfil === 'ADMINISTRADOR' || App.usuario.perfil === 'FUNCIONARIO');

        const detalhe = document.getElementById('evolucao-detalhe');
        detalhe.innerHTML = `
      <div class="card" style="padding:20px">
        <div style="display:flex;justify-content:space-between;align-items:flex-start">
          <div>
            <h3 style="font-size:1.2rem;margin-bottom:6px">${p.nome}</h3>
            <p style="color:var(--text-muted);font-size:.88rem">
              CPF: ${p.cpf} &nbsp;|&nbsp; Tratamento: ${p.tratamento}
            </p>
            <p style="color:var(--text-muted);font-size:.88rem;margin-top:4px">
              <strong>Problema:</strong> ${p.problema || '-'}
            </p>
          </div>
          ${podeRegistrar
            ? `<button class="btn btn-primary btn-sm" onclick="Evolucao.abrirModalSessao(${p.id})">+ Nova Sessão</button>`
            : ''}
        </div>

        <div style="display:flex;gap:16px;margin-top:16px">
          <div style="background:var(--accent);border-radius:8px;padding:10px 16px;text-align:center">
            <strong style="font-size:1.4rem;color:var(--primary)">${sessoes.length}</strong>
            <p style="font-size:.75rem;color:var(--text-muted)">Sessões realizadas</p>
          </div>
          ${sessoes.length > 0 && sessoes[0].evolucao ? `
          <div style="background:#d1fae5;border-radius:8px;padding:10px 16px;text-align:center">
            <strong style="font-size:1.4rem;color:var(--success)">${sessoes[0].evolucao}</strong>
            <p style="font-size:.75rem;color:var(--text-muted)">Última evolução</p>
          </div>` : ''}
        </div>
      </div>

      ${sessoes.length >= 2 ? `
      <div class="chart-container">
        <h4>📊 Evolução da Escala de Dor</h4>
        <canvas id="chart-dor"></canvas>
      </div>` : ''}

      <div class="card">
        <div class="card-header">
          <h3>📋 Histórico de Sessões (${sessoes.length})</h3>
        </div>
        <div style="padding:16px">
          ${sessoes.length === 0
            ? '<div class="empty-state"><div class="icon">📋</div><p>Nenhuma sessão registrada.</p></div>'
            : sessoes.map(s => `
              <div class="sessao-card">
                <div class="sessao-card-header">
                  <strong>📅 ${formatarData(s.dataSessao)}</strong>
                  ${badgeStatus(s.evolucao)}
                </div>
                <div class="sessao-info-grid">
                  <div class="info-item">
                    <label>Dor Antes</label>
                    <span style="color:var(--danger)">${s.dorAntes ?? '-'}/10</span>
                  </div>
                  <div class="info-item">
                    <label>Dor Depois</label>
                    <span style="color:var(--success)">${s.dorDepois ?? '-'}/10</span>
                  </div>
                  <div class="info-item">
                    <label>Mobilidade</label>
                    <span>${s.mobAntes || '-'} → ${s.mobDepois || '-'}</span>
                  </div>
                  ${s.descricao ? `
                  <div class="info-item" style="grid-column:1/-1">
                    <label>Tratamento</label>
                    <span>${s.descricao}</span>
                  </div>` : ''}
                  ${s.exercicios ? `
                  <div class="info-item" style="grid-column:1/-1">
                    <label>Exercícios</label>
                    <span>${s.exercicios}</span>
                  </div>` : ''}
                  ${s.avaliacao ? `
                  <div class="info-item" style="grid-column:1/-1">
                    <label>Avaliação</label>
                    <span>${s.avaliacao}</span>
                  </div>` : ''}
                  ${s.observacoes ? `
                  <div class="info-item" style="grid-column:1/-1">
                    <label>Observações</label>
                    <span>${s.observacoes}</span>
                  </div>` : ''}
                </div>
              </div>`).join('')}
        </div>
      </div>
    `;

        if (sessoes.length >= 2) this.renderGrafico(sessoes);
    },

    renderGrafico(sessoes) {
        if (this.chartDor) { this.chartDor.destroy(); this.chartDor = null; }
        const rev    = [...sessoes].reverse();
        const labels = rev.map(s => formatarData(s.dataSessao));
        const antes  = rev.map(s => s.dorAntes  ?? null);
        const depois = rev.map(s => s.dorDepois ?? null);
        const ctx = document.getElementById('chart-dor');
        if (!ctx) return;
        this.chartDor = new Chart(ctx, {
            type: 'line',
            data: {
                labels,
                datasets: [
                    { label: 'Dor Antes',  data: antes,  borderColor: '#ef4444',
                        backgroundColor: 'rgba(239,68,68,.1)', tension: .35, pointRadius: 5, fill: true },
                    { label: 'Dor Depois', data: depois, borderColor: '#10b981',
                        backgroundColor: 'rgba(16,185,129,.1)', tension: .35, pointRadius: 5, fill: true }
                ]
            },
            options: {
                responsive: true,
                scales: { y: { min: 0, max: 10, ticks: { stepSize: 1 } } },
                plugins: { legend: { position: 'top' } }
            }
        });
    },

    async abrirModalSessao(pacienteId) {
        try {
            const agendamentos = await Api.agendamentos.listar();
            const dosPaciente  = agendamentos.filter(a => a.pacienteId === pacienteId);
            const sel = document.getElementById('sess-agend');
            sel.innerHTML = '<option value="">Selecione o agendamento...</option>' +
                dosPaciente.map(a =>
                    `<option value="${a.id}">${formatarDataHora(a.dataHora)} — ${a.tratamento} (${a.status})</option>`
                ).join('');
        } catch(e) { /* segue */ }

        document.getElementById('sess-data').value = new Date().toISOString().split('T')[0];
        document.getElementById('sess-dor-antes').value  = '5';
        document.getElementById('sess-dor-antes-v').textContent = '5';
        document.getElementById('sess-dor-depois').value = '3';
        document.getElementById('sess-dor-depois-v').textContent = '3';
        ['sess-mob-antes','sess-mob-depois','sess-evolucao'].forEach(id =>
            document.getElementById(id).value = '');
        ['sess-desc','sess-exerc','sess-aval','sess-obs'].forEach(id =>
            document.getElementById(id).value = '');

        this._pacienteIdSessao = pacienteId;
        document.getElementById('modal-sessao').style.display = 'flex';
    },

    fecharModalSessao() {
        document.getElementById('modal-sessao').style.display = 'none';
    },

    async salvarSessao() {
        const agendId   = document.getElementById('sess-agend').value;
        const data      = document.getElementById('sess-data').value;
        const desc      = document.getElementById('sess-desc').value.trim();
        const dorAntes  = +document.getElementById('sess-dor-antes').value;
        const dorDepois = +document.getElementById('sess-dor-depois').value;

        if (!data || !desc)    { Toast.show('Preencha data e descrição.', 'error'); return; }
        if (!agendId)          { Toast.show('Selecione o agendamento.', 'error');   return; }

        const dados = {
            agendamentoId: +agendId,
            pacienteId:    this._pacienteIdSessao,
            dataSessao:    data,
            descricao:     desc,
            dorAntes,
            dorDepois,
            mobAntes:    document.getElementById('sess-mob-antes').value    || null,
            mobDepois:   document.getElementById('sess-mob-depois').value   || null,
            exercicios:  document.getElementById('sess-exerc').value.trim() || null,
            avaliacao:   document.getElementById('sess-aval').value.trim()  || null,
            observacoes: document.getElementById('sess-obs').value.trim()   || null,
            evolucao:    document.getElementById('sess-evolucao').value     || null
        };

        try {
            await Api.sessoes.criar(dados);
            Toast.show('Sessão registrada com sucesso!');
            this.fecharModalSessao();
            await this.selecionarPaciente(this._pacienteIdSessao);
        } catch(e) {
            Toast.show(e.message, 'error');
        }
    }
};
