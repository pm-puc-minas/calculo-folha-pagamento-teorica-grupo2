package br.com.folhapagamento.controller;

import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import br.com.folhapagamento.service.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/funcionarios")
@Tag(name = "Funcionários", description = "API para gerenciamento de funcionários com Streams e persistência")
public class FuncionarioController {
    
    @Autowired
    private FuncionarioService funcionarioService;
    
    @PostMapping
    @Operation(summary = "Criar funcionário", description = "Cria um novo funcionário no banco de dados")
    @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<FuncionarioEntity> criar(@Valid @RequestBody Funcionario funcionario) {
        FuncionarioEntity entity = funcionarioService.salvar(funcionario);
        return ResponseEntity.status(HttpStatus.CREATED).body(entity);
    }
    
    @GetMapping
    @Operation(summary = "Listar todos os funcionários", description = "Retorna todos os funcionários usando Streams")
    @ApiResponse(responseCode = "200", description = "Lista de funcionários retornada com sucesso")
    public ResponseEntity<List<FuncionarioEntity>> listarTodos() {
        List<FuncionarioEntity> funcionarios = funcionarioService.buscarTodos();
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar funcionário por ID", description = "Retorna um funcionário específico")
    @ApiResponse(responseCode = "200", description = "Funcionário encontrado")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioEntity> buscarPorId(@PathVariable Long id) {
        Optional<FuncionarioEntity> funcionario = funcionarioService.buscarPorId(id);
        return funcionario.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar funcionário por CPF", description = "Retorna um funcionário pelo CPF")
    public ResponseEntity<FuncionarioEntity> buscarPorCpf(@PathVariable String cpf) {
        Optional<FuncionarioEntity> funcionario = funcionarioService.buscarPorCpf(cpf);
        return funcionario.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar funcionário", description = "Atualiza um funcionário existente")
    @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
    public ResponseEntity<FuncionarioEntity> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Funcionario funcionario) {
        try {
            FuncionarioEntity entity = funcionarioService.atualizar(id, funcionario);
            return ResponseEntity.ok(entity);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar funcionário", description = "Remove um funcionário do banco de dados")
    @ApiResponse(responseCode = "204", description = "Funcionário deletado com sucesso")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/filtro/tipo/{tipo}")
    @Operation(summary = "Filtrar por tipo", description = "Filtra funcionários por tipo usando Streams")
    public ResponseEntity<List<FuncionarioEntity>> filtrarPorTipo(@PathVariable String tipo) {
        List<FuncionarioEntity> funcionarios = funcionarioService.filtrarPorTipo(tipo);
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/filtro/cargo/{cargo}")
    @Operation(summary = "Filtrar por cargo", description = "Filtra funcionários por cargo usando Streams")
    public ResponseEntity<List<FuncionarioEntity>> filtrarPorCargo(@PathVariable String cargo) {
        List<FuncionarioEntity> funcionarios = funcionarioService.filtrarPorCargo(cargo);
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/filtro/salario")
    @Operation(summary = "Filtrar por faixa salarial", description = "Filtra funcionários por faixa salarial usando Streams")
    public ResponseEntity<List<FuncionarioEntity>> filtrarPorFaixaSalarial(
            @RequestParam(required = false) Double salarioMin,
            @RequestParam(required = false) Double salarioMax) {
        List<FuncionarioEntity> funcionarios;
        if (salarioMin != null && salarioMax != null) {
            funcionarios = funcionarioService.filtrarPorFaixaSalarial(salarioMin, salarioMax);
        } else if (salarioMin != null) {
            funcionarios = funcionarioService.filtrarPorSalarioMinimo(salarioMin);
        } else {
            funcionarios = funcionarioService.buscarTodos();
        }
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/filtro/avancado")
    @Operation(summary = "Filtro avançado", description = "Filtra funcionários usando múltiplos critérios com Streams")
    public ResponseEntity<List<FuncionarioEntity>> filtrarAvancado(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Double salarioMinimo,
            @RequestParam(required = false) Boolean comDependentes,
            @RequestParam(required = false) Boolean comPericulosidade) {
        List<FuncionarioEntity> funcionarios = funcionarioService.processarEFiltrar(
            tipo, salarioMinimo, comDependentes, comPericulosidade);
        return ResponseEntity.ok(funcionarios);
    }
    
    @GetMapping("/estatisticas")
    @Operation(summary = "Estatísticas", description = "Calcula estatísticas dos funcionários usando Streams")
    public ResponseEntity<FuncionarioService.EstatisticasFuncionarios> obterEstatisticas() {
        FuncionarioService.EstatisticasFuncionarios stats = funcionarioService.calcularEstatisticas();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/agrupar/cargo")
    @Operation(summary = "Agrupar por cargo", description = "Agrupa funcionários por cargo usando Streams")
    public ResponseEntity<Map<String, List<FuncionarioEntity>>> agruparPorCargo() {
        Map<String, List<FuncionarioEntity>> agrupados = funcionarioService.agruparPorCargo();
        return ResponseEntity.ok(agrupados);
    }
    
    @GetMapping("/agrupar/tipo")
    @Operation(summary = "Agrupar por tipo", description = "Agrupa funcionários por tipo usando Streams")
    public ResponseEntity<Map<String, List<FuncionarioEntity>>> agruparPorTipo() {
        Map<String, List<FuncionarioEntity>> agrupados = funcionarioService.agruparPorTipo();
        return ResponseEntity.ok(agrupados);
    }
    
    @GetMapping("/ordenar/salario")
    @Operation(summary = "Ordenar por salário", description = "Ordena funcionários por salário usando Streams")
    public ResponseEntity<List<FuncionarioEntity>> ordenarPorSalario(
            @RequestParam(defaultValue = "true") boolean ascendente) {
        List<FuncionarioEntity> funcionarios = funcionarioService.ordenarPorSalario(ascendente);
        return ResponseEntity.ok(funcionarios);
    }
}

