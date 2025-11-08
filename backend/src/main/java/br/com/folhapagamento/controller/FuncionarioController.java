package main.java.br.com.folhapagamento.controller;

import br.com.folhapagamento.dto.FuncionarioRequestDTO;
import br.com.folhapagamento.dto.FuncionarioResponseDTO;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController 
@RequestMapping("/api/funcionarios") 
public class FuncionarioController {

    @Autowired
    private FuncionarioService service;

    @PostMapping 
    public ResponseEntity<FuncionarioResponseDTO> criar(@RequestBody FuncionarioRequestDTO dto) {
        Funcionario funcionarioSalvo = service.criarFuncionario(dto);
        FuncionarioResponseDTO responseDTO = new FuncionarioResponseDTO(funcionarioSalvo);
        
        URI location = URI.create("/api/funcionarios/" + responseDTO.id());
        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping 
    public ResponseEntity<List<FuncionarioResponseDTO>> listar() {
        List<Funcionario> funcionarios = service.listarTodos();
        
        List<FuncionarioResponseDTO> responseList = funcionarios.stream()
            .map(FuncionarioResponseDTO::new) 
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responseList);
    }
}