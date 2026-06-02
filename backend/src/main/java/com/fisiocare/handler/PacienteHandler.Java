package com.fisiocare.handler;

import com.google.gson.JsonObject;
import com.fisiocare.dao.PacienteDAO;
import com.fisiocare.dao.UsuarioDAO;
import com.fisiocare.model.Paciente;
import com.fisiocare.model.Usuario;
import com.fisiocare.service.AutenticacaoService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

/**
 * GET    /api/pacientes                — listar todos (staff)
 * POST   /api/pacientes                — criar paciente
 * GET    /api/pacientes/{id}           — buscar por ID
 * PUT    /api/pacientes/{id}           — atualizar
 * DELETE /api/pacientes/{id}           — desativar (ADMIN)
 * GET    /api/pacientes/cpf/{cpf}      — buscar por CPF
 * GET    /api/pacientes/usuario/{uid}  — buscar pelo usuarioId (usado pelo próprio paciente)
 */
public class PacienteHandler extends BaseHandler {

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final UsuarioDAO  usuarioDAO  = new UsuarioDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;

        Usuario logado = autenticar(ex);
        if (logado == null) return;

        String path   = path(ex);
        String metodo = metodo(ex);

        // GET /api/pacientes/cpf/{cpf}
        if (path.contains("/cpf/") && "GET".equals(metodo)) {
            String cpf = path.substring(path.lastIndexOf("/cpf/") + 5);
            Paciente p = pacienteDAO.buscarPorCpf(cpf);
            if (p == null) { erro(ex, 404, "Paciente não encontrado."); return; }
            ok(ex, p);
            return;
        }

        // GET /api/pacientes/usuario/{uid}
        if (path.contains("/usuario/") && "GET".equals(metodo)) {
            String uidStr = path.substring(path.lastIndexOf("/usuario/") + 9);
            try {
                Long uid = Long.parseLong(uidStr);

                // Paciente só pode ver os próprios dados
                if ("PACIENTE".equals(logado.getPerfil()) && !uid.equals(logado.getId())) {
                    erro(ex, 403, "Acesso negado."); return;
                }

                Paciente p = pacienteDAO.buscarPorUsuarioId(uid);
                if (p == null) { erro(ex, 404, "Paciente não encontrado."); return; }
                ok(ex, p);
            } catch (NumberFormatException e) {
                erro(ex, 400, "ID inválido.");
            }
            return;
        }

        // Operações com ID numérico
        Long id = idDaUrl(ex, "/api/pacientes");

        if (id != null) {
            switch (metodo) {
                case "GET"    -> buscarPorId(ex, id, logado);
                case "PUT"    -> atualizar(ex, id, logado);
                case "DELETE" -> deletar(ex, id, logado);
                default       -> erro(ex, 405, "Método não permitido.");
            }
        } else {
            switch (metodo) {
                case "GET"  -> listar(ex, logado);
                case "POST" -> criar(ex, logado);
                default     -> erro(ex, 405, "Método não permitido.");
            }
        }
    }

    private void listar(HttpExchange ex, Usuario logado) throws IOException {
        // Paciente não pode listar todos — bloqueio no backend também
        if ("PACIENTE".equals(logado.getPerfil())) {
            erro(ex, 403, "Acesso negado."); return;
        }
        ok(ex, pacienteDAO.listarTodos());
    }

    private void buscarPorId(HttpExchange ex, Long id, Usuario logado) throws IOException {
        Paciente p = pacienteDAO.buscarPorId(id);
        if (p == null) { erro(ex, 404, "Paciente não encontrado."); return; }

        // Paciente só pode ver o próprio registro
        if ("PACIENTE".equals(logado.getPerfil()) && !p.getUsuarioId().equals(logado.getId())) {
            erro(ex, 403, "Acesso negado."); return;
        }
        ok(ex, p);
    }

    private void criar(HttpExchange ex, Usuario logado) throws IOException {
        if ("PACIENTE".equals(logado.getPerfil())) {
            erro(ex, 403, "Sem permissão para criar pacientes."); return;
        }

        JsonObject body = lerJson(ex, JsonObject.class);
        String[] obrigatorios = {"nome","email","cpf","telefone","dataNasc","endereco","problema","tratamento"};
        for (String campo : obrigatorios) {
            if (!body.has(campo) || body.get(campo).getAsString().isBlank()) {
                erro(ex, 400, "Campo obrigatório: " + campo); return;
            }
        }

        String email = body.get("email").getAsString();
        String cpf   = body.get("cpf").getAsString();

        if (usuarioDAO.emailExiste(email)) { erro(ex, 409, "Email já cadastrado."); return; }
        if (usuarioDAO.cpfExiste(cpf))     { erro(ex, 409, "CPF já cadastrado.");   return; }

        Usuario u = new Usuario();
        u.setNome(body.get("nome").getAsString());
        u.setEmail(email);
        u.setCpf(cpf);
        u.setTelefone(body.get("telefone").getAsString());
        u.setDataNasc(body.get("dataNasc").getAsString());
        u.setEndereco(body.get("endereco").getAsString());
        u.setSenha(body.has("senha") && !body.get("senha").getAsString().isBlank()
                ? body.get("senha").getAsString()
                : cpf.replaceAll("[^0-9]", ""));
        u.setPerfil("PACIENTE");

        Long usuarioId = usuarioDAO.inserir(u);
        if (usuarioId == null) { erro(ex, 500, "Erro ao criar usuário."); return; }

        Paciente p = new Paciente();
        p.setUsuarioId(usuarioId);
        p.setProblema(body.get("problema").getAsString());
        p.setTratamento(body.get("tratamento").getAsString());
        p.setObservacoes(body.has("observacoes") ? body.get("observacoes").getAsString() : null);

        Long pacienteId = pacienteDAO.inserir(p);
        if (pacienteId == null) { erro(ex, 500, "Erro ao criar paciente."); return; }

        created(ex, pacienteDAO.buscarPorId(pacienteId));
    }

    private void atualizar(HttpExchange ex, Long id, Usuario logado) throws IOException {
        if ("PACIENTE".equals(logado.getPerfil())) {
            erro(ex, 403, "Sem permissão para editar pacientes."); return;
        }
        JsonObject body = lerJson(ex, JsonObject.class);
        Paciente p = pacienteDAO.buscarPorId(id);
        if (p == null) { erro(ex, 404, "Paciente não encontrado."); return; }

        if (body.has("problema"))    p.setProblema(body.get("problema").getAsString());
        if (body.has("tratamento"))  p.setTratamento(body.get("tratamento").getAsString());
        if (body.has("observacoes")) p.setObservacoes(body.get("observacoes").getAsString());

        Usuario u = usuarioDAO.buscarPorId(p.getUsuarioId());
        if (u != null) {
            if (body.has("nome"))     u.setNome(body.get("nome").getAsString());
            if (body.has("telefone")) u.setTelefone(body.get("telefone").getAsString());
            if (body.has("endereco")) u.setEndereco(body.get("endereco").getAsString());
            usuarioDAO.atualizar(u);
        }

        pacienteDAO.atualizar(p);
        ok(ex, pacienteDAO.buscarPorId(id));
    }

    private void deletar(HttpExchange ex, Long id, Usuario logado) throws IOException {
        if (!"ADMINISTRADOR".equals(logado.getPerfil())) {
            erro(ex, 403, "Apenas administradores podem remover pacientes."); return;
        }
        if (pacienteDAO.buscarPorId(id) == null) { erro(ex, 404, "Paciente não encontrado."); return; }
        pacienteDAO.desativar(id);
        semConteudo(ex);
    }
}