package br.com.folhapagamento.listener;

import br.com.folhapagamento.event.FuncionarioCadastradoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
@Component
public class LogFuncionarioListener {

    private static final Logger logger = LoggerFactory.getLogger(LogFuncionarioListener.class);

    @EventListener
    public void aoCadastrarFuncionario(FuncionarioCadastradoEvent event) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🎉 [EVENTO] Novo Funcionário Cadastrado!");
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("📋 ID: {}", event.getFuncionario().getId());
        logger.info("👤 Nome: {}", event.getFuncionario().getNome());
        logger.info("📧 CPF: {}", event.getFuncionario().getCpf());
        logger.info("💼 Cargo: {}", event.getFuncionario().getCargo());
        logger.info("📊 Tipo: {}", event.getFuncionario().getTipo());
        logger.info("💰 Salário: R$ {}", event.getFuncionario().getSalarioBruto());
        logger.info("⚡ Ação: {}", event.getAcaoRealizada());
        logger.info("═══════════════════════════════════════════════════════");
    }
}
