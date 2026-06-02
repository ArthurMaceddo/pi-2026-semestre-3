package com.fisiocare.handler;

import com.google.gson.JsonObject;
import com.fisiocare.dao.AgendamentoDAO;
import com.fisiocare.dao.PacienteDAO;
import com.fisiocare.dao.SessaoDAO;
import com.fisiocare.model.Usuario;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * GET /api/dashboard — retorna estatísticas para o painel principal
 */
public class DashboardHandler extends BaseHandler {

    private final PacienteDAO    pacienteDAO    = new PacienteDAO();
    private final AgendamentoDAO agendamentoDAO = new AgendamentoDAO();
    private final SessaoDAO      sessaoDAO      = new SessaoDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        Usuario logado = autenticar(ex);
        if (logado == null) return;

        if (!"GET".equals(metodo(ex))) {
            erro(ex, 405, "Método não permitido.");
            return;
        }

        JsonObject stats = new JsonObject();
        stats.addProperty("totalPacientes",   pacienteDAO.contar());
        stats.addProperty("consultasHoje",    agendamentoDAO.contarHoje());
        stats.addProperty("sessoesMes",       sessaoDAO.contarMes());
        stats.addProperty("nomeUsuario",      logado.getNome());
        stats.addProperty("perfilUsuario",    logado.getPerfil());

        ok(ex, stats);
    }
}