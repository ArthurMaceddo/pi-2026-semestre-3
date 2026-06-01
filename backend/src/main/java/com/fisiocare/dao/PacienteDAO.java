package com.fisiocare.dao;

import com.fisiocare.database.DatabaseConnection;
import com.fisiocare.model.Paciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    private static final String SELECT_JOIN =
            "SELECT p.*, u.nome, u.email, u.cpf, u.telefone, u.data_nasc, u.endereco " +
                    "FROM pacientes p JOIN usuarios u ON p.usuario_id = u.id ";

    public Long inserir(Paciente p) {
        String sql = "INSERT INTO pacientes (usuario_id, problema, tratamento, observacoes) " +
                "VALUES (?,?,?,?) RETURNING id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, p.getUsuarioId());
            s.setString(2, p.getProblema());
            s.setString(3, p.getTratamento());
            s.setString(4, p.getObservacoes());
            ResultSet rs = s.executeQuery();
            if (rs.next()) return rs.getLong("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Paciente buscarPorId(Long id) {
        String sql = SELECT_JOIN + "WHERE p.id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, id);
            ResultSet rs = s.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Paciente buscarPorCpf(String cpf) {
        String sql = SELECT_JOIN + "WHERE u.cpf = ? AND p.ativo = true";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, cpf);
            ResultSet rs = s.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Paciente buscarPorUsuarioId(Long usuarioId) {
        String sql = SELECT_JOIN + "WHERE p.usuario_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, usuarioId);
            ResultSet rs = s.executeQuery();
            if (rs.next()) return mapear(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE p.ativo = true ORDER BY u.nome";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM pacientes WHERE ativo = true";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean atualizar(Paciente p) {
        String sql = "UPDATE pacientes SET problema=?, tratamento=?, observacoes=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, p.getProblema());
            s.setString(2, p.getTratamento());
            s.setString(3, p.getObservacoes());
            s.setLong(4, p.getId());
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean desativar(Long id) {
        String sql = "UPDATE pacientes SET ativo=false WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getLong("id"));
        p.setUsuarioId(rs.getLong("usuario_id"));
        p.setProblema(rs.getString("problema"));
        p.setTratamento(rs.getString("tratamento"));
        Date ini = rs.getDate("inicio_trat");
        p.setInicioTrat(ini != null ? ini.toString() : null);
        p.setObservacoes(rs.getString("observacoes"));
        p.setAtivo(rs.getBoolean("ativo"));
        Timestamp cr = rs.getTimestamp("criado_em");
        p.setCriadoEm(cr != null ? cr.toString() : null);
        // join
        p.setNome(rs.getString("nome"));
        p.setEmail(rs.getString("email"));
        p.setCpf(rs.getString("cpf"));
        p.setTelefone(rs.getString("telefone"));
        Date dn = rs.getDate("data_nasc");
        p.setDataNasc(dn != null ? dn.toString() : null);
        p.setEndereco(rs.getString("endereco"));
        return p;
    }
}