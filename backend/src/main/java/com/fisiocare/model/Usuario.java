package com.fisiocare.model;

public class Usuario {
    private Long   id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private String dataNasc;
    private String endereco;
    private String senha;
    private String perfil;   // ADMINISTRADOR | FUNCIONARIO | PACIENTE
    private Boolean ativo;
    private String criadoEm;

    public Usuario() {}

    public Long   getId()       { return id; }
    public String getNome()     { return nome; }
    public String getEmail()    { return email; }
    public String getCpf()      { return cpf; }
    public String getTelefone() { return telefone; }
    public String getDataNasc() { return dataNasc; }
    public String getEndereco() { return endereco; }
    public String getSenha()    { return senha; }
    public String getPerfil()   { return perfil; }
    public Boolean getAtivo()   { return ativo; }
    public String getCriadoEm() { return criadoEm; }

    public void setId(Long id)           { this.id = id; }
    public void setNome(String n)        { this.nome = n; }
    public void setEmail(String e)       { this.email = e; }
    public void setCpf(String c)         { this.cpf = c; }
    public void setTelefone(String t)    { this.telefone = t; }
    public void setDataNasc(String d)    { this.dataNasc = d; }
    public void setEndereco(String e)    { this.endereco = e; }
    public void setSenha(String s)       { this.senha = s; }
    public void setPerfil(String p)      { this.perfil = p; }
    public void setAtivo(Boolean a)      { this.ativo = a; }
    public void setCriadoEm(String c)    { this.criadoEm = c; }
}