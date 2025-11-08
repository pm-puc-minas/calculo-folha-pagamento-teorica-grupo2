package br.com.folhapagamento.controller;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import br.com.folhapagamento.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController 
@RequestMapping("/api/funcionarios") 
public class FuncionarioController {

    @Autowired
    private FuncionarioService service;

    @PostMapping 
    public ResponseEntity<FuncionarioEntity> criar(@RequestBody Funcionario funcionario) {
        FuncionarioEntity funcionarioSalvo = service.salvar(funcionario);
        
        URI location = URI.create("/api/funcionarios/" + funcionarioSalvo.getId());
        return ResponseEntity.created(location).body(funcionarioSalvo);
    }

    @GetMapping 
    public ResponseEntity<List<FuncionarioEntity>> listar() {
        List<FuncionarioEntity> funcionarios = service.buscarTodos();
        return ResponseEntity.ok(funcionarios);
    }
}