package br.com.folhapagamento.controller;

import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.interfaces.FolhaPagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Folha de Pagamento", description = "API para cálculo de folha de pagamento")
public class FolhaPagamentoController {
    
    @Autowired
    private FolhaPagamentoService folhaPagamentoService;
    
    @PostMapping("/calcular")
    @Operation(summary = "Calcular folha de pagamento", description = "Calcula a folha de pagamento completa de um funcionário")
    @ApiResponse(responseCode = "200", description = "Folha calculada com sucesso")
    public FolhaPagamento calcularFolha(@RequestBody Funcionario funcionario) {
        return folhaPagamentoService.calcularFolha(funcionario);
    }
    
    @GetMapping("/teste")
    @Operation(summary = "Testar API", description = "Verifica se a API está funcionando")
    @ApiResponse(responseCode = "200", description = "API funcionando")
    public String teste() {
        return "API de Folha de Pagamento funcionando!";
    }
    
}