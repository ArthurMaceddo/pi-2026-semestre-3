package com.fisiocare.handler;

import com.google.gson.JsonObject;
import com.fisiocare.dao.SessaoDAO;
import com.fisiocare.model.Sessao;
import com.fisiocare.model.Usuario;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * GET  /api/sessoes                   — todas as sessões
 * GET  /api/sessoes/paciente/{id}      — sessões de um paciente
 * POST /api/sessoes                    — registrar nova sessão
 * DELETE /api/sessoes/{id}             — excluir
 */
public class SessaoHandler extends BaseHandler {

    private final SessaoDAO dao = new SessaoDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        Usuario logado = autenticar(ex);
        if (logado == null) return;

        String path = path(ex);
        String mtd  = metodo(ex);

        // GET /api/sessoes/paciente/{id}
        if (path.contains("/paciente/") && "GET".equals(mtd)) {
            String idStr = path.substring(path.lastIndexOf("/") + 1);
            try {
                Long pacId = Long.parseLong(idStr);
                ok(ex, dao.listarPorPaciente(pacId));
            } catch (NumberFormatException e) {
                erro(ex, 400, "ID inválido.");
            }
            return;
        }

        Long id = idDaUrl(ex, "/api/sessoes");

        if (id != null) {
            if ("DELETE".equals(mtd)) deletar(ex, id, logado);
            else erro(ex, 405, "Método não permitido.");
        } else {
            switch (mtd) {
                case "GET"  -> ok(ex, dao.listarTodas());
                case "POST" -> criar(ex, logado);
                default     -> erro(ex, 405, "Método não permitido.");
            }
        }
    }

    private void criar(HttpExchange ex, Usuario logado) throws IOException {
        if ("PACIENTE".equals(logado.getPerfil())) {
            erro(ex, 403, "Pacientes não podem registrar sessões."); return;
        }
        JsonObject body = lerJson(ex, JsonObject.class);
        String[] obrig = {"agendamentoId","pacienteId","dataSessao","descricao"};
        for (String c : obrig) {
            if (!body.has(c) || body.get(c).getAsString().isBlank()) {
                erro(ex, 400, "Campo obrigatório: " + c); return;
            }
        }

        Sessao s = new Sessao();
        s.setAgendamentoId(body.get("agendamentoId").getAsLong());
        s.setPacienteId(body.get("pacienteId").getAsLong());
        s.setDataSessao(body.get("dataSessao").getAsString());
        s.setDescricao(body.get("descricao").getAsString());
        s.setDorAntes(body.has("dorAntes") && !body.get("dorAntes").isJsonNull()
                ? body.get("dorAntes").getAsInt() : null);
        s.setDorDepois(body.has("dorDepois") && !body.get("dorDepois").isJsonNull()
                ? body.get("dorDepois").getAsInt() : null);
        s.setMobAntes(body.has("mobAntes")   ? body.get("mobAntes").getAsString()   : null);
        s.setMobDepois(body.has("mobDepois") ? body.get("mobDepois").getAsString()  : null);
        s.setExercicios(body.has("exercicios") ? body.get("exercicios").getAsString() : null);
        s.setAvaliacao(body.has("avaliacao")   ? body.get("avaliacao").getAsString()  : null);
        s.setObservacoes(body.has("observacoes") ? body.get("observacoes").getAsString() : null);
        s.setEvolucao(body.has("evolucao") ? body.get("evolucao").getAsString() : null);

        Long novoId = dao.inserir(s);
        if (novoId == null) { erro(ex, 500, "Erro ao registrar sessão."); return; }

        JsonObject resp = new JsonObject();
        resp.addProperty("id", novoId);
        resp.addProperty("mensagem", "Sessão registrada com sucesso.");
        created(ex, resp);
    }

    private void deletar(HttpExchange ex, Long id, Usuario logado) throws IOException {
        if (!"ADMINISTRADOR".equals(logado.getPerfil())) {
            erro(ex, 403, "Apenas administradores podem excluir sessões."); return;
        }
        dao.deletar(id);
        semConteudo(ex);
    }
}