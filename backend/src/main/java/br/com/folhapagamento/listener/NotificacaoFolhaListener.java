package br.com.folhapagamento.listener;

import br.com.folhapagamento.event.FolhaPagamentoGeradaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoFolhaListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoFolhaListener.class);

    @EventListener
    @Async
    public void aoGerarFolhaPagamento(FolhaPagamentoGeradaEvent event) {
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("📊 [EVENTO] Nova Folha de Pagamento Gerada!");
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("🆔 ID Folha: {}", event.getFolhaPagamento().getId());
        logger.info("👤 Funcionário: {}", event.getFolhaPagamento().getFuncionario().getNome());
        logger.info("💵 Salário Bruto: R$ {}", String.format("%.2f", event.getFolhaPagamento().getSalarioBruto()));
        logger.info("💰 Salário Líquido: R$ {}", String.format("%.2f", event.getFolhaPagamento().getSalarioLiquido()));
        logger.info("📉 Total Descontos: R$ {}", String.format("%.2f", event.getFolhaPagamento().getTotalDescontos()));
        logger.info("📅 Mês/Ano: {}/{}", event.getFolhaPagamento().getMesReferencia(), event.getFolhaPagamento().getAnoReferencia());
        logger.info("📝 Mensagem: {}", event.getMensagem());
        logger.info("═══════════════════════════════════════════════════════");
        logger.info("✅ Notificação processada com sucesso!");
    }
}

