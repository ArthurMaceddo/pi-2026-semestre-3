package com.fisiocare.handler;

import com.google.gson.JsonObject;
import com.fisiocare.dao.AgendamentoDAO;
import com.fisiocare.model.Agendamento;
import com.fisiocare.model.Usuario;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Arrays;

public class AgendamentoHandler extends BaseHandler {

    private final AgendamentoDAO dao = new AgendamentoDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        Usuario logado = autenticar(ex);
        if (logado == null) return;

        String path = path(ex);
        String mtd  = metodo(ex);

        if (path.endsWith("/hoje") && "GET".equals(mtd)) {
            ok(ex, dao.listarHoje());
            return;
        }

        Long id = idDaUrl(ex, "/api/agendamentos");

        if (id != null) {
            switch (mtd) {
                case "PUT"    -> atualizarStatus(ex, id);
                case "DELETE" -> deletar(ex, id, logado);
                default       -> erro(ex, 405, "Método não permitido.");
            }
        } else {
            switch (mtd) {
                case "GET"  -> ok(ex, dao.listarTodos());
                case "POST" -> criar(ex, logado);
                default     -> erro(ex, 405, "Método não permitido.");
            }
        }
    }

    private void criar(HttpExchange ex, Usuario logado) throws IOException {
        if ("PACIENTE".equals(logado.getPerfil())) {
            erro(ex, 403, "Pacientes não podem criar agendamentos."); return;
        }

        JsonObject body = lerJson(ex, JsonObject.class);
        String[] obrig = {"pacienteId","fisioId","dataHora","tratamento","qtdSessoes"};
        for (String c : obrig) {
            if (!body.has(c)) { erro(ex, 400, "Campo obrigatório: " + c); return; }
        }

        Agendamento a = new Agendamento();
        a.setPacienteId(body.get("pacienteId").getAsLong());
        a.setFisioId(body.get("fisioId").getAsLong());
        a.setDataHora(body.get("dataHora").getAsString());
        a.setTratamento(body.get("tratamento").getAsString());
        a.setQtdSessoes(body.get("qtdSessoes").getAsInt());
        a.setObservacoes(body.has("observacoes") ? body.get("observacoes").getAsString() : null);

        Long novoId = dao.inserir(a);
        if (novoId == null) { erro(ex, 500, "Erro ao criar agendamento."); return; }

        JsonObject resp = new JsonObject();
        resp.addProperty("id", novoId);
        resp.addProperty("mensagem", "Agendamento criado com sucesso.");
        created(ex, resp);
    }

    private void atualizarStatus(HttpExchange ex, Long id) throws IOException {
        JsonObject body = lerJson(ex, JsonObject.class);
        if (!body.has("status")) { erro(ex, 400, "Status é obrigatório."); return; }
        String status = body.get("status").getAsString();

        if (!Arrays.asList("AGENDADA","REALIZADA","CANCELADA").contains(status)) {
            erro(ex, 400, "Status inválido."); return;
        }
        boolean ok = dao.atualizarStatus(id, status);
        if (!ok) { erro(ex, 404, "Agendamento não encontrado."); return; }

        JsonObject resp = new JsonObject();
        resp.addProperty("mensagem", "Status atualizado para " + status);
        ok(ex, resp);
    }

    private void deletar(HttpExchange ex, Long id, Usuario logado) throws IOException {
        if (!"ADMINISTRADOR".equals(logado.getPerfil())) {
            erro(ex, 403, "Apenas administradores podem excluir agendamentos."); return;
        }
        dao.deletar(id);
        semConteudo(ex);
    }
}