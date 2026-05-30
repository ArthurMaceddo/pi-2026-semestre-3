package com.fisiocare;

import com.fisiocare.database.DatabaseConnection;
import com.fisiocare.handler.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP principal do FisioCare
 * Inicia na porta 8080 e registra todos os endpoints da API
 */
public class FisioCareServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        // Testa conexão com banco antes de iniciar
        if (!DatabaseConnection.testConnection()) {
            System.err.println("ERRO: Não foi possível conectar ao banco de dados!");
            System.err.println("Verifique as configurações em DatabaseConnection.java");
            System.exit(1);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Registrar rotas
        server.createContext("/api/auth",          new AuthHandler());
        server.createContext("/api/pacientes",      new PacienteHandler());
        server.createContext("/api/funcionarios",   new FuncionarioHandler());
        server.createContext("/api/agendamentos",   new AgendamentoHandler());
        server.createContext("/api/sessoes",        new SessaoHandler());
        server.createContext("/api/dashboard",      new DashboardHandler());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();

        System.out.println("=========================================");
        System.out.println("  FisioCare Backend rodando na porta " + PORT);
        System.out.println("  http://localhost:" + PORT);
        System.out.println("=========================================");
    }
}
