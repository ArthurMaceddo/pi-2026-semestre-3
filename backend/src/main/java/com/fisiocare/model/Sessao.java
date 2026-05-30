package com.fisiocare.model;

public class Sessao {
    private Long    id;
    private Long    agendamentoId;
    private Long    pacienteId;
    private String  dataSessao;
    private Integer dorAntes;
    private Integer dorDepois;
    private String  mobAntes;
    private String  mobDepois;
    private String  descricao;
    private String  exercicios;
    private String  avaliacao;
    private String  observacoes;
    private String  evolucao; // MELHORANDO | ESTAVEL | PIORANDO
    private String  criadoEm;
    // join
    private String  pacienteNome;

    public Sessao() {}

    public Long    getId()             { return id; }
    public Long    getAgendamentoId()  { return agendamentoId; }
    public Long    getPacienteId()     { return pacienteId; }
    public String  getDataSessao()     { return dataSessao; }
    public Integer getDorAntes()       { return dorAntes; }
    public Integer getDorDepois()      { return dorDepois; }
    public String  getMobAntes()       { return mobAntes; }
    public String  getMobDepois()      { return mobDepois; }
    public String  getDescricao()      { return descricao; }
    public String  getExercicios()     { return exercicios; }
    public String  getAvaliacao()      { return avaliacao; }
    public String  getObservacoes()    { return observacoes; }
    public String  getEvolucao()       { return evolucao; }
    public String  getCriadoEm()       { return criadoEm; }
    public String  getPacienteNome()   { return pacienteNome; }

    public void setId(Long v)             { this.id = v; }
    public void setAgendamentoId(Long v)  { this.agendamentoId = v; }
    public void setPacienteId(Long v)     { this.pacienteId = v; }
    public void setDataSessao(String v)   { this.dataSessao = v; }
    public void setDorAntes(Integer v)    { this.dorAntes = v; }
    public void setDorDepois(Integer v)   { this.dorDepois = v; }
    public void setMobAntes(String v)     { this.mobAntes = v; }
    public void setMobDepois(String v)    { this.mobDepois = v; }
    public void setDescricao(String v)    { this.descricao = v; }
    public void setExercicios(String v)   { this.exercicios = v; }
    public void setAvaliacao(String v)    { this.avaliacao = v; }
    public void setObservacoes(String v)  { this.observacoes = v; }
    public void setEvolucao(String v)     { this.evolucao = v; }
    public void setCriadoEm(String v)     { this.criadoEm = v; }
    public void setPacienteNome(String v) { this.pacienteNome = v; }
}