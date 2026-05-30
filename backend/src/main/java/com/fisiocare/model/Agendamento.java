package com.fisiocare.model;

public class Agendamento {
    private Long   id;
    private Long   pacienteId;
    private Long   fisioId;
    private String dataHora;
    private String tratamento;
    private Integer qtdSessoes;
    private String status;
    private String observacoes;
    private String criadoEm;
    // join
    private String pacienteNome;
    private String pacienteCpf;
    private String fisioNome;

    public Agendamento() {}

    public Long    getId()           { return id; }
    public Long    getPacienteId()   { return pacienteId; }
    public Long    getFisioId()      { return fisioId; }
    public String  getDataHora()     { return dataHora; }
    public String  getTratamento()   { return tratamento; }
    public Integer getQtdSessoes()   { return qtdSessoes; }
    public String  getStatus()       { return status; }
    public String  getObservacoes()  { return observacoes; }
    public String  getCriadoEm()     { return criadoEm; }
    public String  getPacienteNome() { return pacienteNome; }
    public String  getPacienteCpf()  { return pacienteCpf; }
    public String  getFisioNome()    { return fisioNome; }

    public void setId(Long v)            { this.id = v; }
    public void setPacienteId(Long v)    { this.pacienteId = v; }
    public void setFisioId(Long v)       { this.fisioId = v; }
    public void setDataHora(String v)    { this.dataHora = v; }
    public void setTratamento(String v)  { this.tratamento = v; }
    public void setQtdSessoes(Integer v) { this.qtdSessoes = v; }
    public void setStatus(String v)      { this.status = v; }
    public void setObservacoes(String v) { this.observacoes = v; }
    public void setCriadoEm(String v)    { this.criadoEm = v; }
    public void setPacienteNome(String v){ this.pacienteNome = v; }
    public void setPacienteCpf(String v) { this.pacienteCpf = v; }
    public void setFisioNome(String v)   { this.fisioNome = v; }
}