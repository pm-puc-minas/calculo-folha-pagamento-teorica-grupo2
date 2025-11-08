package br.com.folhapagamento.controller;

import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.interfaces.FolhaPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Cálculo de Folha", description = "API para cálculo de folha de pagamento (compatibilidade)")
public class CalculoController {
    
    @Autowired
    private FolhaPagamentoService folhaPagamentoService;
    
    @PostMapping("/calcular")
    @Operation(summary = "Calcular folha de pagamento", description = "Calcula a folha de pagamento completa de um funcionário")
    @ApiResponse(responseCode = "200", description = "Folha calculada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    public ResponseEntity<FolhaPagamento> calcularFolha(@Valid @RequestBody Funcionario funcionario) {
        FolhaPagamento folha = folhaPagamentoService.calcularFolha(funcionario);
        return ResponseEntity.ok(folha);
    }
    
    @GetMapping("/teste")
    @Operation(summary = "Testar API", description = "Verifica se a API está funcionando")
    @ApiResponse(responseCode = "200", description = "API funcionando")
    public String teste() {
        return "API de Folha de Pagamento funcionando!";
    }
}

