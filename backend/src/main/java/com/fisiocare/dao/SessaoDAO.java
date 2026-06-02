package com.fisiocare.dao;

import com.fisiocare.database.DatabaseConnection;
import com.fisiocare.model.Sessao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessaoDAO {

    private static final String SELECT_JOIN =
            "SELECT s.*, u.nome as paciente_nome " +
                    "FROM sessoes s " +
                    "JOIN pacientes p ON s.paciente_id = p.id " +
                    "JOIN usuarios u ON p.usuario_id = u.id ";

    public Long inserir(Sessao sessao) {
        String sql = "INSERT INTO sessoes (agendamento_id, paciente_id, data_sessao, dor_antes, dor_depois, " +
                "mob_antes, mob_depois, descricao, exercicios, avaliacao, observacoes, evolucao) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, sessao.getAgendamentoId());
            s.setLong(2, sessao.getPacienteId());
            s.setDate(3, Date.valueOf(sessao.getDataSessao()));
            s.setObject(4, sessao.getDorAntes(), Types.INTEGER);
            s.setObject(5, sessao.getDorDepois(), Types.INTEGER);
            s.setString(6, sessao.getMobAntes());
            s.setString(7, sessao.getMobDepois());
            s.setString(8, sessao.getDescricao());
            s.setString(9, sessao.getExercicios());
            s.setString(10, sessao.getAvaliacao());
            s.setString(11, sessao.getObservacoes());
            s.setString(12, sessao.getEvolucao());
            ResultSet rs = s.executeQuery();
            if (rs.next()) return rs.getLong("id");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Sessao> listarPorPaciente(Long pacienteId) {
        List<Sessao> lista = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE s.paciente_id = ? ORDER BY s.data_sessao DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, pacienteId);
            ResultSet rs = s.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Sessao> listarPorAgendamento(Long agendamentoId) {
        List<Sessao> lista = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE s.agendamento_id = ? ORDER BY s.data_sessao DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, agendamentoId);
            ResultSet rs = s.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Sessao> listarTodas() {
        List<Sessao> lista = new ArrayList<>();
        String sql = SELECT_JOIN + "ORDER BY s.data_sessao DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public int contarMes() {
        String sql = "SELECT COUNT(*) FROM sessoes " +
                "WHERE EXTRACT(YEAR FROM data_sessao)=EXTRACT(YEAR FROM NOW()) " +
                "AND EXTRACT(MONTH FROM data_sessao)=EXTRACT(MONTH FROM NOW())";
        try (Connection c = DatabaseConnection.getConnection();
             Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public boolean deletar(Long id) {
        String sql = "DELETE FROM sessoes WHERE id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement s = c.prepareStatement(sql)) {
            s.setLong(1, id);
            return s.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private Sessao mapear(ResultSet rs) throws SQLException {
        Sessao s = new Sessao();
        s.setId(rs.getLong("id"));
        s.setAgendamentoId(rs.getLong("agendamento_id"));
        s.setPacienteId(rs.getLong("paciente_id"));
        Date d = rs.getDate("data_sessao");
        s.setDataSessao(d != null ? d.toString() : null);
        int da = rs.getInt("dor_antes");
        s.setDorAntes(rs.wasNull() ? null : da);
        int dd = rs.getInt("dor_depois");
        s.setDorDepois(rs.wasNull() ? null : dd);
        s.setMobAntes(rs.getString("mob_antes"));
        s.setMobDepois(rs.getString("mob_depois"));
        s.setDescricao(rs.getString("descricao"));
        s.setExercicios(rs.getString("exercicios"));
        s.setAvaliacao(rs.getString("avaliacao"));
        s.setObservacoes(rs.getString("observacoes"));
        s.setEvolucao(rs.getString("evolucao"));
        Timestamp cr = rs.getTimestamp("criado_em");
        s.setCriadoEm(cr != null ? cr.toString() : null);
        s.setPacienteNome(rs.getString("paciente_nome"));
        return s;
    }
}