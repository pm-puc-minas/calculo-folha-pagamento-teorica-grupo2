package main.java.br.com.folhapagamento.service;

import br.com.folhapagamento.dto.FuncionarioRequestDTO;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.repository.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationEventPublisher;
import main.java.br.com.folhapagamento.event.FuncionarioCadastradoEvent;

import java.util.List;
import java.util.stream.Collectors; 

@Service 
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository repository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Funcionario criarFuncionario(FuncionarioRequestDTO dto) {
        Funcionario novoFuncionario = new Funcionario();
        novoFuncionario.setNome(dto.nome());
        novoFuncionario.setCargo(dto.cargo());
        novoFuncionario.setDepartamento(dto.departamento());
        
        novoFuncionario.setSalarioBruto(dto.salarioBruto()); 
        

        Funcionario funcionarioSalvo = repository.save(novoFuncionario);
        
        eventPublisher.publishEvent(new FuncionarioCadastradoEvent(funcionarioSalvo));
        
        return funcionarioSalvo;
    }

    public List<Funcionario> listarTodos() {
        return repository.findAll();
    }
}