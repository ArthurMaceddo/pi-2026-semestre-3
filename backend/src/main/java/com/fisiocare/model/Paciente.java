package com.fisiocare.model;

public class Paciente {
    private Long   id;
    private Long   usuarioId;
    private String problema;
    private String tratamento;
    private String inicioTrat;
    private String observacoes;
    private Boolean ativo;
    private String criadoEm;

    // Dados do usuario (join)
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String dataNasc;
    private String endereco;

    public Paciente() {}

    public Long    getId()          { return id; }
    public Long    getUsuarioId()   { return usuarioId; }
    public String  getProblema()    { return problema; }
    public String  getTratamento()  { return tratamento; }
    public String  getInicioTrat()  { return inicioTrat; }
    public String  getObservacoes() { return observacoes; }
    public Boolean getAtivo()       { return ativo; }
    public String  getCriadoEm()    { return criadoEm; }
    public String  getNome()        { return nome; }
    public String  getEmail()       { return email; }
    public String  getCpf()         { return cpf; }
    public String  getTelefone()    { return telefone; }
    public String  getDataNasc()    { return dataNasc; }
    public String  getEndereco()    { return endereco; }

    public void setId(Long v)           { this.id = v; }
    public void setUsuarioId(Long v)    { this.usuarioId = v; }
    public void setProblema(String v)   { this.problema = v; }
    public void setTratamento(String v) { this.tratamento = v; }
    public void setInicioTrat(String v) { this.inicioTrat = v; }
    public void setObservacoes(String v){ this.observacoes = v; }
    public void setAtivo(Boolean v)     { this.ativo = v; }
    public void setCriadoEm(String v)   { this.criadoEm = v; }
    public void setNome(String v)       { this.nome = v; }
    public void setEmail(String v)      { this.email = v; }
    public void setCpf(String v)        { this.cpf = v; }
    public void setTelefone(String v)   { this.telefone = v; }
    public void setDataNasc(String v)   { this.dataNasc = v; }
    public void setEndereco(String v)   { this.endereco = v; }
}