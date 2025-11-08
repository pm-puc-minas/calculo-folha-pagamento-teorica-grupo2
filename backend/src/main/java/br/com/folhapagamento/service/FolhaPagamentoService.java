package br.com.folhapagamento.interfaces;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolhaPagamentoService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public double calcularTotalFolhaPorDepartamento(String departamento) {
        List<Funcionario> funcionarios = funcionarioRepository.findByDepartamento(departamento);
        return funcionarios.stream()
                .mapToDouble(Funcionario::getSalarioBase)
                .sum();
    }

    public Map<String, Double> calcularTotalPorDepartamento() {
        List<Funcionario> todosFuncionarios = funcionarioRepository.findAll();
        return todosFuncionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getDepartamento,
                        Collectors.summingDouble(Funcionario::getSalarioBase)
                ));
    }
}
