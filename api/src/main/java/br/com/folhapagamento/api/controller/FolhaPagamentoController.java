// src/main/java/br/com/folhapagamento/api/controller/FolhaPagamentoController.java
package br.com.folhapagamento.api.controller;

import br.com.folhapagamento.api.dto.FuncionarioInputDTO;
import br.com.folhapagamento.api.dto.RelatorioFolhaDTO;
import br.com.folhapagamento.api.service.FolhaPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folha-pagamento")
public class FolhaPagamentoController {

    @Autowired
    private FolhaPagamentoService folhaPagamentoService;

    @PostMapping("/calcular")
    public ResponseEntity<RelatorioFolhaDTO> calcular(@RequestBody FuncionarioInputDTO funcionarioInput) {
        RelatorioFolhaDTO relatorio = folhaPagamentoService.calcularFolha(funcionarioInput);
        return ResponseEntity.ok(relatorio);
    }
}