package br.com.folhapagamento.dto;

import br.com.folhapagamento.model.Funcionario;

public record FuncionarioResponseDTO(
    Long id,
    String nome,
    String cargo,
    String departamento,
    double salarioBruto 
) {
    public FuncionarioResponseDTO(Funcionario funcionario) {
        this(
            funcionario.getId(), 
            funcionario.getNome(), 
            funcionario.getCargo(), 
            funcionario.getDepartamento(), 
            funcionario.getSalarioBruto() 
        );
    }
}