package com.fisiocare.dao;

import com.fisiocare.database.DatabaseConnection;
import com.fisiocare.model.Agendamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendamentoDAO {

    private static final String SELECT_JOIN =
            "SELECT a.*, up.nome as paciente_nome, up.cpf as paciente_cpf, uf.nome as fisio_nome " +
                    "FROM agendamentos a " +
                    "JOIN pacientes p ON a.paciente_id = p.id " +
                    "JOIN usuarios up ON p.usuario_id = up.id " +
                    "JOIN usuarios uf ON a.fisio_id = uf.id ";

    public Long inserir(Agendamento a) {
        String sql = "INSERT INTO agendamentos (paciente_id, fisio_id, data_hora, tratamento, qtd_sessoes, observacoes) " +
                "VALUES (?,?,?,?,?,?) RETURNING id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, a.getPacienteId());
            s.setLong(2, a.getFisioId());
            s.setTimestamp(3, Timestamp.valueOf(a.getDataHora().replace("T", " ").replace("Z", "")));
            s.setString(4, a.getTratamento());
            s.setInt(5, a.getQtdSessoes());
            s.setString(6, a.getObservacoes());
            ResultSet rs = s.executeQuery();
            if (rs.next()) return rs.getLong("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Agendamento> listarTodos() {
        List<Agendamento> lista = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY a.data_hora DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Agendamento> listarHoje() {
        List<Agendamento> lista = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE DATE(a.data_hora) = CURRENT_DATE ORDER BY a.data_hora";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Agendamento> listarPorPaciente(Long pacienteId) {
        List<Agendamento> lista = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE a.paciente_id = ? ORDER BY a.data_hora DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, pacienteId);
            ResultSet rs = s.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public int contarHoje() {
        String sql = "SELECT COUNT(*) FROM agendamentos WHERE DATE(data_hora) = CURRENT_DATE";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean atualizarStatus(Long id, String status) {
        String sql = "UPDATE agendamentos SET status=? WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, status);
            s.setLong(2, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deletar(Long id) {
        String sql = "DELETE FROM agendamentos WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Agendamento mapear(ResultSet rs) throws SQLException {
        Agendamento a = new Agendamento();
        a.setId(rs.getLong("id"));
        a.setPacienteId(rs.getLong("paciente_id"));
        a.setFisioId(rs.getLong("fisio_id"));
        Timestamp dh = rs.getTimestamp("data_hora");
        a.setDataHora(dh != null ? dh.toString() : null);
        a.setTratamento(rs.getString("tratamento"));
        a.setQtdSessoes(rs.getInt("qtd_sessoes"));
        a.setStatus(rs.getString("status"));
        a.setObservacoes(rs.getString("observacoes"));
        Timestamp cr = rs.getTimestamp("criado_em");
        a.setCriadoEm(cr != null ? cr.toString() : null);
        a.setPacienteNome(rs.getString("paciente_nome"));
        a.setPacienteCpf(rs.getString("paciente_cpf"));
        a.setFisioNome(rs.getString("fisio_nome"));
        return a;
    }
}