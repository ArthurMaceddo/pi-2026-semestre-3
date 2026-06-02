package com.fisiocare.handler;

import com.google.gson.JsonObject;
import com.fisiocare.model.Usuario;
import com.fisiocare.service.AutenticacaoService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * POST /api/auth/login  — { email, senha }
 * POST /api/auth/logout — Authorization: Bearer {token}
 */
public class AuthHandler extends BaseHandler {

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (isOptions(ex)) return;

        String path = path(ex);

        if (path.endsWith("/login") && "POST".equals(metodo(ex))) {
            login(ex);
        } else if (path.endsWith("/logout") && "POST".equals(metodo(ex))) {
            logout(ex);
        } else {
            erro(ex, 404, "Endpoint não encontrado");
        }
    }

    private void login(HttpExchange ex) throws IOException {
        JsonObject body = lerJson(ex, JsonObject.class);

        if (body == null || !body.has("email") || !body.has("senha")) {
            erro(ex, 400, "Email e senha são obrigatórios.");
            return;
        }

        String email = body.get("email").getAsString();
        String senha = body.get("senha").getAsString();

        String token = AutenticacaoService.login(email, senha);

        if (token == null) {
            erro(ex, 401, "Email ou senha incorretos.");
            return;
        }

        Usuario u = AutenticacaoService.validarToken(token);

        JsonObject resposta = new JsonObject();
        resposta.addProperty("token",  token);
        resposta.addProperty("nome",   u.getNome());
        resposta.addProperty("perfil", u.getPerfil());
        resposta.addProperty("id",     u.getId());

        ok(ex, resposta);
    }

    private void logout(HttpExchange ex) throws IOException {
        String token = extrairToken(ex);
        AutenticacaoService.logout(token);

        JsonObject resposta = new JsonObject();
        resposta.addProperty("mensagem", "Logout realizado com sucesso.");
        ok(ex, resposta);
    }
}