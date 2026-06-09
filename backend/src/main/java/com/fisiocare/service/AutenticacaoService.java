package com.fisiocare.service;

import com.fisiocare.dao.UsuarioDAO;
import com.fisiocare.model.Usuario;

import java.security.MessageDigest;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gerencia autenticação e tokens de sessão em memória
 */
public class AutenticacaoService {

    // token → usuario logado
    private static final Map<String, Usuario> sessoes = new ConcurrentHashMap<>();
    private static final UsuarioDAO dao = new UsuarioDAO();

    /**
     * Realiza login e retorna token de sessão ou null se falhar
     */
    public static String login(String email, String senha) {
        Usuario u = dao.buscarPorEmail(email);
        if (u == null || !u.getAtivo()) return null;

        if (!senha.equals(u.getSenha())) return null;

        // Remove tokens antigos do mesmo usuário
        sessoes.entrySet().removeIf(e -> e.getValue().getId().equals(u.getId()));

        String token = UUID.randomUUID().toString();
        sessoes.put(token, u);
        return token;
    }

    /**
     * Retorna o usuário associado ao token ou null
     */
    public static Usuario validarToken(String token) {
        if (token == null || token.isBlank()) return null;
        return sessoes.get(token);
    }

    /**
     * Invalida o token (logout)
     */
    public static void logout(String token) {
        sessoes.remove(token);
    }


}