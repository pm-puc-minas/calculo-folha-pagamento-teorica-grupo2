package br.com.folhapagamento.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "folhas_pagamento")
public class FolhaPagamentoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private FuncionarioEntity funcionario;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double salarioBruto;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double salarioPorHora;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double adicionalPericulosidade;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double adicionalInsalubridade;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double valeAlimentacao;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double descontoValeTransporte;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double descontoINSS;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double descontoIRRF;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double fgts;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalAntesDescontos;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double totalDescontos;
    
    @Column(nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double salarioLiquido;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
    
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    
    @Column(nullable = false)
    private Integer mesReferencia;
    
    @Column(nullable = false)
    private Integer anoReferencia;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (mesReferencia == null) {
            mesReferencia = LocalDateTime.now().getMonthValue();
        }
        if (anoReferencia == null) {
            anoReferencia = LocalDateTime.now().getYear();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
    
    public FolhaPagamentoEntity() {
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public FuncionarioEntity getFuncionario() {
        return funcionario;
    }
    
    public void setFuncionario(FuncionarioEntity funcionario) {
        this.funcionario = funcionario;
    }
    
    public Double getSalarioBruto() {
        return salarioBruto;
    }
    
    public void setSalarioBruto(Double salarioBruto) {
        this.salarioBruto = salarioBruto;
    }
    
    public Double getSalarioPorHora() {
        return salarioPorHora;
    }
    
    public void setSalarioPorHora(Double salarioPorHora) {
        this.salarioPorHora = salarioPorHora;
    }
    
    public Double getAdicionalPericulosidade() {
        return adicionalPericulosidade;
    }
    
    public void setAdicionalPericulosidade(Double adicionalPericulosidade) {
        this.adicionalPericulosidade = adicionalPericulosidade;
    }
    
    public Double getAdicionalInsalubridade() {
        return adicionalInsalubridade;
    }
    
    public void setAdicionalInsalubridade(Double adicionalInsalubridade) {
        this.adicionalInsalubridade = adicionalInsalubridade;
    }
    
    public Double getValeAlimentacao() {
        return valeAlimentacao;
    }
    
    public void setValeAlimentacao(Double valeAlimentacao) {
        this.valeAlimentacao = valeAlimentacao;
    }
    
    public Double getDescontoValeTransporte() {
        return descontoValeTransporte;
    }
    
    public void setDescontoValeTransporte(Double descontoValeTransporte) {
        this.descontoValeTransporte = descontoValeTransporte;
    }
    
    public Double getDescontoINSS() {
        return descontoINSS;
    }
    
    public void setDescontoINSS(Double descontoINSS) {
        this.descontoINSS = descontoINSS;
    }
    
    public Double getDescontoIRRF() {
        return descontoIRRF;
    }
    
    public void setDescontoIRRF(Double descontoIRRF) {
        this.descontoIRRF = descontoIRRF;
    }
    
    public Double getFgts() {
        return fgts;
    }
    
    public void setFgts(Double fgts) {
        this.fgts = fgts;
    }
    
    public Double getTotalAntesDescontos() {
        return totalAntesDescontos;
    }
    
    public void setTotalAntesDescontos(Double totalAntesDescontos) {
        this.totalAntesDescontos = totalAntesDescontos;
    }
    
    public Double getTotalDescontos() {
        return totalDescontos;
    }
    
    public void setTotalDescontos(Double totalDescontos) {
        this.totalDescontos = totalDescontos;
    }
    
    public Double getSalarioLiquido() {
        return salarioLiquido;
    }
    
    public void setSalarioLiquido(Double salarioLiquido) {
        this.salarioLiquido = salarioLiquido;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
    
    public Integer getMesReferencia() {
        return mesReferencia;
    }
    
    public void setMesReferencia(Integer mesReferencia) {
        this.mesReferencia = mesReferencia;
    }
    
    public Integer getAnoReferencia() {
        return anoReferencia;
    }
    
    public void setAnoReferencia(Integer anoReferencia) {
        this.anoReferencia = anoReferencia;
    }
}

