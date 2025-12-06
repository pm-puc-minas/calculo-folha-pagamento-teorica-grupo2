package br.com.folhapagamento.controller;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import br.com.folhapagamento.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController 
@RequestMapping("/api/funcionarios")
@Tag(name = "Funcionários", description = "API para gerenciamento de funcionários")
public class FuncionarioController {

    @Autowired
    private FuncionarioService service;

    @PostMapping 
    @Operation(summary = "Criar funcionário", description = "Cadastra um novo funcionário no sistema")
    @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<FuncionarioEntity> criar(@Valid @RequestBody Funcionario funcionario) {
        FuncionarioEntity funcionarioSalvo = service.salvar(funcionario);
        URI location = URI.create("/api/funcionarios/" + funcionarioSalvo.getId());
        return ResponseEntity.created(location).body(funcionarioSalvo);
    }

    @GetMapping 
    @Operation(summary = "Listar funcionários", description = "Retorna todos os funcionários cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de funcionários retornada com sucesso")
    public ResponseEntity<List<FuncionarioEntity>> listar() {
        List<FuncionarioEntity> funcionarios = service.buscarTodos();
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID", description = "Retorna um funcionário específico pelo ID")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioEntity> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar funcionário", description = "Atualiza os dados de um funcionário existente")
    @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<FuncionarioEntity> atualizar(
            @PathVariable Long id, 
            @Valid @RequestBody Funcionario funcionario) {
        try {
            FuncionarioEntity funcionarioAtualizado = service.atualizar(id, funcionario);
            return ResponseEntity.ok(funcionarioAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir funcionário", description = "Remove um funcionário do sistema")
    @ApiResponse(responseCode = "204", description = "Funcionário excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (service.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar por CPF", description = "Retorna um funcionário pelo CPF")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioEntity> buscarPorCpf(@PathVariable String cpf) {
        return service.buscarPorCpf(cpf)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Filtrar por tipo", description = "Retorna funcionários pelo tipo (CLT ou PJ)")
    public ResponseEntity<List<FuncionarioEntity>> filtrarPorTipo(@PathVariable String tipo) {
        List<FuncionarioEntity> funcionarios = service.filtrarPorTipo(tipo);
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/cargo/{cargo}")
    @Operation(summary = "Filtrar por cargo", description = "Retorna funcionários pelo cargo")
    public ResponseEntity<List<FuncionarioEntity>> filtrarPorCargo(@PathVariable String cargo) {
        List<FuncionarioEntity> funcionarios = service.filtrarPorCargo(cargo);
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/filtro/salario")
    @Operation(summary = "Filtrar por faixa salarial", description = "Retorna funcionários por faixa de salário")
    public ResponseEntity<List<FuncionarioEntity>> filtrarPorFaixaSalarial(
            @RequestParam(required = false) Double salarioMin,
            @RequestParam(required = false) Double salarioMax) {
        List<FuncionarioEntity> funcionarios;
        if (salarioMin != null && salarioMax != null) {
            funcionarios = service.filtrarPorFaixaSalarial(salarioMin, salarioMax);
        } else if (salarioMin != null) {
            funcionarios = service.filtrarPorSalarioMinimo(salarioMin);
        } else {
            funcionarios = service.buscarTodos();
        }
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/filtro/avancado")
    @Operation(summary = "Filtro avançado", description = "Filtra funcionários por múltiplos critérios")
    public ResponseEntity<List<FuncionarioEntity>> filtrarAvancado(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Double salarioMinimo,
            @RequestParam(required = false) Boolean comDependentes,
            @RequestParam(required = false) Boolean comPericulosidade) {
        List<FuncionarioEntity> funcionarios = service.processarEFiltrar(
            tipo, salarioMinimo, comDependentes, comPericulosidade);
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/estatisticas")
    @Operation(summary = "Estatísticas", description = "Retorna estatísticas dos funcionários")
    public ResponseEntity<FuncionarioService.EstatisticasFuncionarios> obterEstatisticas() {
        FuncionarioService.EstatisticasFuncionarios stats = service.calcularEstatisticas();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/agrupar/cargo")
    @Operation(summary = "Agrupar por cargo", description = "Agrupa funcionários por cargo")
    public ResponseEntity<Map<String, List<FuncionarioEntity>>> agruparPorCargo() {
        Map<String, List<FuncionarioEntity>> agrupados = service.agruparPorCargo();
        return ResponseEntity.ok(agrupados);
    }
    
    @GetMapping("/agrupar/tipo")
    @Operation(summary = "Agrupar por tipo", description = "Agrupa funcionários por tipo (CLT/PJ)")
    public ResponseEntity<Map<String, List<FuncionarioEntity>>> agruparPorTipo() {
        Map<String, List<FuncionarioEntity>> agrupados = service.agruparPorTipo();
        return ResponseEntity.ok(agrupados);
    }
    
    @GetMapping("/ordenar/salario")
    @Operation(summary = "Ordenar por salário", description = "Ordena funcionários por salário")
    public ResponseEntity<List<FuncionarioEntity>> ordenarPorSalario(
            @RequestParam(defaultValue = "true") boolean ascendente) {
        List<FuncionarioEntity> funcionarios = service.ordenarPorSalario(ascendente);
        return ResponseEntity.ok(funcionarios);
    }
}
