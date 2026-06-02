/* ================================================
   FisioCare - Página: Dashboard
   ================================================ */
const Pages = window.Pages || {};

Pages.dashboard = async function(container) {
    try {
        const stats = await Api.dashboard();
        const isAdminOuFunc = App.usuario.perfil === 'ADMINISTRADOR' || App.usuario.perfil === 'FUNCIONARIO';

        if (isAdminOuFunc) {
            container.innerHTML = `
        <div class="page-header"><h2>🏠 Dashboard</h2></div>
        <div class="page-body">
          <p class="welcome-msg">Bem-vindo(a), <span>${stats.nomeUsuario}</span>! 👋</p>
          <div class="stat-grid">
            <div class="stat-card">
              <div class="stat-icon">👥</div>
              <div class="stat-info">
                <label>Total de Pacientes</label><strong>${stats.totalPacientes}</strong>
              </div>
            </div>
            <div class="stat-card green">
              <div class="stat-icon">📅</div>
              <div class="stat-info">
                <label>Consultas Hoje</label><strong>${stats.consultasHoje}</strong>
              </div>
            </div>
            <div class="stat-card orange">
              <div class="stat-icon">📊</div>
              <div class="stat-info">
                <label>Sessões no Mês</label><strong>${stats.sessoesMes}</strong>
              </div>
            </div>
          </div>
          <div class="card">
            <div class="card-header">
              <h3>📅 Agendamentos de Hoje</h3>
              <button class="btn btn-secondary btn-sm" onclick="App.navegar('agendamentos')">Ver todos</button>
            </div>
            <div id="tabela-hoje" class="table-wrap"><div class="loader"><div class="spinner"></div> Carregando...</div></div>
          </div>
        </div>
      `;

            const hoje = await Api.agendamentos.hoje();
            const tbl = document.getElementById('tabela-hoje');
            if (!hoje || hoje.length === 0) {
                tbl.innerHTML = `<div class="empty-state"><div class="icon">📭</div><p>Nenhum agendamento para hoje.</p></div>`;
            } else {
                tbl.innerHTML = `
          <table>
            <thead><tr><th>Paciente</th><th>Fisioterapeuta</th><th>Horário</th><th>Tratamento</th><th>Status</th></tr></thead>
            <tbody>${hoje.map(a => `
              <tr>
                <td>${a.pacienteNome || '-'}</td><td>${a.fisioNome || '-'}</td>
                <td>${formatarDataHora(a.dataHora)}</td><td>${a.tratamento}</td><td>${badgeStatus(a.status)}</td>
              </tr>`).join('')}
            </tbody>
          </table>`;
            }
        } else {
            // Dashboard exclusivo para PACIENTE
            container.innerHTML = `
        <div class="page-header"><h2>🏠 Meu Dashboard</h2></div>
        <div class="page-body">
          <p class="welcome-msg">Bem-vindo(a), <span>${App.usuario.nome}</span>! 👋</p>
          <div id="paciente-dashboard-content">
            <div class="loader"><div class="spinner"></div> Carregando suas sessões...</div>
          </div>
        </div>
      `;

            try {
                const pac = await Api.pacientes.buscarPorUsuarioId(App.usuario.id);
                if (!pac) throw new Error("Seu cadastro de paciente não foi encontrado.");

                const todosAgendamentos = await Api.agendamentos.listar();
                const meusAgendamentos = todosAgendamentos.filter(a => a.pacienteId === pac.id);

                const aRealizar = meusAgendamentos.filter(a => a.status === 'AGENDADA');
                const jaRealizadas = meusAgendamentos.filter(a => a.status === 'REALIZADA');

                const renderTabela = (lista, msg) => {
                    if (!lista.length) return `<div class="empty-state"><div class="icon">📭</div><p>${msg}</p></div>`;
                    return `
            <table>
              <thead><tr><th>Fisioterapeuta</th><th>Data / Hora</th><th>Tratamento</th><th>Status</th></tr></thead>
              <tbody>${lista.map(a => `
                <tr>
                  <td>${a.fisioNome || '-'}</td><td>${formatarDataHora(a.dataHora)}</td>
                  <td>${a.tratamento}</td><td>${badgeStatus(a.status)}</td>
                </tr>`).join('')}
              </tbody>
            </table>`;
                };

                document.getElementById('paciente-dashboard-content').innerHTML = `
          <div class="card" style="margin-bottom: 20px;">
            <div class="card-header"><h3>📅 Próximas Sessões (A Realizar)</h3></div>
            <div class="table-wrap">${renderTabela(aRealizar, 'Nenhuma sessão futura agendada.')}</div>
          </div>
          <div class="card">
            <div class="card-header"><h3>✅ Sessões Realizadas</h3></div>
            <div class="table-wrap">${renderTabela(jaRealizadas, 'Nenhuma sessão realizada ainda.')}</div>
          </div>
        `;
            } catch (err) {
                document.getElementById('paciente-dashboard-content').innerHTML =
                    `<div class="empty-state"><div class="icon">⚠️</div><p>${err.message}</p></div>`;
            }
        }
    } catch(e) {
        container.innerHTML = `
      <div class="page-header"><h2>Dashboard</h2></div>
      <div class="page-body">
        <div class="empty-state"><div class="icon">⚠️</div><p>Não foi possível conectar ao servidor.</p></div>
      </div>`;
    }
};