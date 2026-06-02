package com.fisiocare.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.fisiocare.model.Usuario;
import com.fisiocare.service.AutenticacaoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Handler base — fornece utilitários de parsing, resposta e autenticação
 */
public abstract class BaseHandler implements HttpHandler {

    protected static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ─── Métodos auxiliares de resposta ──────────────────────────────────────

    protected void responder(HttpExchange ex, int status, Object body) throws IOException {
        addCorsHeaders(ex);
        byte[] bytes = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    protected void ok(HttpExchange ex, Object body) throws IOException {
        responder(ex, 200, body);
    }

    protected void created(HttpExchange ex, Object body) throws IOException {
        responder(ex, 201, body);
    }

    protected void erro(HttpExchange ex, int status, String msg) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("erro", msg);
        responder(ex, status, obj);
    }

    protected void semConteudo(HttpExchange ex) throws IOException {
        addCorsHeaders(ex);
        ex.sendResponseHeaders(204, -1);
    }

    // ─── Leitura do body ─────────────────────────────────────────────────────

    protected String lerBody(HttpExchange ex) throws IOException {
        try (InputStreamReader r = new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(r)) {
            return br.lines().collect(Collectors.joining());
        }
    }

    protected <T> T lerJson(HttpExchange ex, Class<T> tipo) throws IOException {
        return gson.fromJson(lerBody(ex), tipo);
    }

    // ─── Autenticação ────────────────────────────────────────────────────────

    protected String extrairToken(HttpExchange ex) {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) return auth.substring(7);
        return null;
    }

    protected Usuario autenticar(HttpExchange ex) throws IOException {
        Usuario u = AutenticacaoService.validarToken(extrairToken(ex));
        if (u == null) {
            erro(ex, 401, "Token inválido ou expirado. Faça login novamente.");
        }
        return u;
    }

    // ─── Roteamento por método ───────────────────────────────────────────────

    protected String metodo(HttpExchange ex) {
        return ex.getRequestMethod().toUpperCase();
    }

    protected String path(HttpExchange ex) {
        return ex.getRequestURI().getPath();
    }

    protected Long idDaUrl(HttpExchange ex, String prefix) {
        String p = ex.getRequestURI().getPath();
        String resto = p.substring(prefix.length());
        if (resto.startsWith("/")) resto = resto.substring(1);
        String[] partes = resto.split("/");
        try { return Long.parseLong(partes[0]); } catch (Exception e) { return null; }
    }

    // ─── CORS ────────────────────────────────────────────────────────────────

    protected void addCorsHeaders(HttpExchange ex) {
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    protected boolean isOptions(HttpExchange ex) throws IOException {
        if ("OPTIONS".equals(metodo(ex))) {
            addCorsHeaders(ex);
            ex.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }
}