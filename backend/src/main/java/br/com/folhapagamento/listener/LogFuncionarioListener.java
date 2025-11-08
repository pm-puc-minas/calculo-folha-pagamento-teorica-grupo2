package main.java.br.com.folhapagamento.listener;

import main.java.br.com.folhapagamento.event.FuncionarioCadastradoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LogFuncionarioListener {

    private static final Logger logger = LoggerFactory.getLogger(LogFuncionarioListener.class);

    @EventListener
    public void aoCadastrarFuncionario(FuncionarioCadastradoEvent event) {
        
        logger.info("[LOG DE EVENTO] Novo funcionário cadastrado!");
        logger.info("ID: {}, Nome: {}, Cargo: {}",
            event.funcionario().getId(),
            event.funcionario().getNome(),
            event.funcionario().getCargo()
        );
    }
}