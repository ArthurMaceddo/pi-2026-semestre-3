package com.fisiocare.handler;

import com.google.gson.JsonObject;
import com.fisiocare.dao.UsuarioDAO;
import com.fisiocare.model.Usuario;
import com.fisiocare.service.AutenticacaoService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * GET    /api/funcionarios         — listar funcionários
 * POST   /api/funcionarios         — criar funcionário (ADMIN)
 * PUT    /api/funcionarios/{id}    — atualizar
 * DELETE /api/funcionarios/{id}    — desativar (ADMIN)
 */
public class FuncionarioHandler extends BaseHandler {

    private final UsuarioDAO dao = new UsuarioDAO();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;
        Usuario logado = autenticar(ex);
        if (logado == null) return;

        Long id     = idDaUrl(ex, "/api/funcionarios");
        String mtd  = metodo(ex);

        if (id != null) {
            switch (mtd) {
                case "PUT"    -> atualizar(ex, id, logado);
                case "DELETE" -> deletar(ex, id, logado);
                default       -> erro(ex, 405, "Método não permitido.");
            }
        } else {
            switch (mtd) {
                case "GET"  -> listar(ex, logado);
                case "POST" -> criar(ex, logado);
                default     -> erro(ex, 405, "Método não permitido.");
            }
        }
    }

    private void listar(HttpExchange ex, Usuario logado) throws IOException {
        // Qualquer autenticado pode listar funcionários (para agendamento)
        ok(ex, dao.listarPorPerfil("FUNCIONARIO"));
    }

    private void criar(HttpExchange ex, Usuario logado) throws IOException {
        if (!"ADMINISTRADOR".equals(logado.getPerfil())) {
            erro(ex, 403, "Apenas administradores podem cadastrar funcionários."); return;
        }

        JsonObject body = lerJson(ex, JsonObject.class);
        String[] obrig = {"nome","email","cpf","telefone","dataNasc","endereco","senha","perfil"};
        for (String c : obrig) {
            if (!body.has(c) || body.get(c).getAsString().isBlank()) {
                erro(ex, 400, "Campo obrigatório: " + c); return;
            }
        }

        if (dao.emailExiste(body.get("email").getAsString())) {
            erro(ex, 409, "Email já cadastrado."); return;
        }
        if (dao.cpfExiste(body.get("cpf").getAsString())) {
            erro(ex, 409, "CPF já cadastrado."); return;
        }

        Usuario u = new Usuario();
        u.setNome(body.get("nome").getAsString());
        u.setEmail(body.get("email").getAsString());
        u.setCpf(body.get("cpf").getAsString());
        u.setTelefone(body.get("telefone").getAsString());
        u.setDataNasc(body.get("dataNasc").getAsString());
        u.setEndereco(body.get("endereco").getAsString());
        u.setSenha(body.get("senha").getAsString());
        u.setPerfil(body.get("perfil").getAsString());

        Long novoId = dao.inserir(u);
        if (novoId == null) { erro(ex, 500, "Erro ao criar funcionário."); return; }

        Usuario criado = dao.buscarPorId(novoId);
        criado.setSenha(null); // nunca devolver a senha
        created(ex, criado);
    }

    private void atualizar(HttpExchange ex, Long id, Usuario logado) throws IOException {
        if (!"ADMINISTRADOR".equals(logado.getPerfil())) {
            erro(ex, 403, "Apenas administradores podem editar funcionários."); return;
        }
        JsonObject body = lerJson(ex, JsonObject.class);
        Usuario u = dao.buscarPorId(id);
        if (u == null) { erro(ex, 404, "Funcionário não encontrado."); return; }

        if (body.has("nome"))      u.setNome(body.get("nome").getAsString());
        if (body.has("email"))     u.setEmail(body.get("email").getAsString());
        if (body.has("telefone"))  u.setTelefone(body.get("telefone").getAsString());
        if (body.has("dataNasc"))  u.setDataNasc(body.get("dataNasc").getAsString());
        if (body.has("endereco"))  u.setEndereco(body.get("endereco").getAsString());
        if (body.has("perfil"))    u.setPerfil(body.get("perfil").getAsString());

        dao.atualizar(u);

        if (body.has("senha") && !body.get("senha").getAsString().isBlank()) {
            dao.atualizarSenha(id, body.get("senha").getAsString());        }

        u.setSenha(null);
        ok(ex, u);
    }

    private void deletar(HttpExchange ex, Long id, Usuario logado) throws IOException {
        if (!"ADMINISTRADOR".equals(logado.getPerfil())) {
            erro(ex, 403, "Apenas administradores podem remover funcionários."); return;
        }
        dao.desativar(id);
        semConteudo(ex);
    }
}