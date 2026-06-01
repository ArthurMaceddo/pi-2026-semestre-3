package com.fisiocare.dao;

import com.fisiocare.database.DatabaseConnection;
import com.fisiocare.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Long inserir(Usuario u) {
        String sql = "INSERT INTO usuarios (nome, email, cpf, telefone, data_nasc, endereco, senha, perfil) " +
                "VALUES (?,?,?,?,?,?,?,?) RETURNING id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, u.getNome());
            s.setString(2, u.getEmail());
            s.setString(3, u.getCpf());
            s.setString(4, u.getTelefone());
            s.setDate(5, Date.valueOf(u.getDataNasc()));
            s.setString(6, u.getEndereco());
            s.setString(7, u.getSenha());
            s.setString(8, u.getPerfil());
            ResultSet rs = s.executeQuery();
            if (rs.next()) return rs.getLong("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        return buscarPor("email", email);
    }

    public Usuario buscarPorId(Long id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, id);
            ResultSet rs = s.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Usuario buscarPorCpf(String cpf) {
        return buscarPor("cpf", cpf);
    }

    private Usuario buscarPor(String campo, String valor) {
        String sql = "SELECT * FROM usuarios WHERE " + campo + " = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, valor);
            ResultSet rs = s.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Usuario> listarPorPerfil(String perfil) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE perfil = ? AND ativo = true ORDER BY nome";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, perfil);
            ResultSet rs = s.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE ativo = true ORDER BY nome";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean atualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nome=?, email=?, telefone=?, data_nasc=?, endereco=?, perfil=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, u.getNome());
            s.setString(2, u.getEmail());
            s.setString(3, u.getTelefone());
            s.setDate(4, Date.valueOf(u.getDataNasc()));
            s.setString(5, u.getEndereco());
            s.setString(6, u.getPerfil());
            s.setLong(7, u.getId());
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean atualizarSenha(Long id, String novaSenha) {
        String sql = "UPDATE usuarios SET senha=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, novaSenha);
            s.setLong(2, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean desativar(Long id) {
        String sql = "UPDATE usuarios SET ativo=false WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, email);
            return s.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean cpfExiste(String cpf) {
        String sql = "SELECT 1 FROM usuarios WHERE cpf = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, cpf);
            return s.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setNome(rs.getString("nome"));
        u.setEmail(rs.getString("email"));
        u.setCpf(rs.getString("cpf"));
        u.setTelefone(rs.getString("telefone"));
        Date d = rs.getDate("data_nasc");
        u.setDataNasc(d != null ? d.toString() : null);
        u.setEndereco(rs.getString("endereco"));
        u.setSenha(rs.getString("senha"));
        u.setPerfil(rs.getString("perfil"));
        u.setAtivo(rs.getBoolean("ativo"));
        Timestamp cr = rs.getTimestamp("criado_em");
        u.setCriadoEm(cr != null ? cr.toString() : null);
        return u;
    }
}