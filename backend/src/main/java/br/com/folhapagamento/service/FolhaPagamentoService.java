package br.com.folhapagamento.service;

import br.com.folhapagamento.event.FolhaPagamentoGeradaEvent;
import br.com.folhapagamento.model.FolhaPagamento;
import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import br.com.folhapagamento.repository.FolhaPagamentoRepository;
import br.com.folhapagamento.repository.FuncionarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class FolhaPagamentoService {
    
    private static final Logger logger = LoggerFactory.getLogger(FolhaPagamentoService.class);
    
    @Autowired
    private FolhaPagamentoRepository folhaPagamentoRepository;
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    @Autowired
    private CalculadoraFolha calculadoraFolha;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public FolhaPagamentoEntity salvar(FolhaPagamento folha, Long funcionarioId) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(funcionarioId)
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));
        
        FolhaPagamentoEntity entity = converterParaEntity(folha, funcionario);
        FolhaPagamentoEntity saved = folhaPagamentoRepository.save(entity);
        logger.info("Folha de pagamento salva com sucesso para funcionário: {}", funcionario.getNome());
        
        // Publicar evento de geração de folha
        FolhaPagamentoGeradaEvent event = new FolhaPagamentoGeradaEvent(
            saved,
            String.format("Folha de pagamento gerada para %s - Salário Líquido: R$ %.2f", 
                funcionario.getNome(), saved.getSalarioLiquido())
        );
        eventPublisher.publishEvent(event);
        
        return saved;
    }
    
    public List<FolhaPagamentoEntity> buscarTodas() {
        return folhaPagamentoRepository.findAll();
    }
    

    public Optional<FolhaPagamentoEntity> buscarPorId(Long id) {
        return folhaPagamentoRepository.findById(id);
    }
    
 
    public List<FolhaPagamentoEntity> buscarPorFuncionario(Long funcionarioId) {
        return folhaPagamentoRepository.findByFuncionarioId(funcionarioId);
    }
    

    public List<FolhaPagamentoEntity> buscarPorMesEAno(Integer mes, Integer ano) {
        return folhaPagamentoRepository.findByMesReferenciaAndAnoReferencia(mes, ano);
    }
    
    public List<FolhaPagamentoEntity> filtrarPorFaixaSalarioLiquido(Double salarioMin, Double salarioMax) {
        return folhaPagamentoRepository.findBySalarioLiquidoBetween(salarioMin, salarioMax);
    }
    

    public List<FolhaPagamentoEntity> filtrarPorSalarioLiquidoMinimo(Double salarioMinimo) {
        return folhaPagamentoRepository.findBySalarioLiquidoGreaterThanEqual(salarioMinimo);
    }
 
    public List<FolhaPagamentoEntity> processarEFiltrar(
            Integer mes,
            Integer ano,
            Double salarioLiquidoMinimo,
            Double salarioLiquidoMaximo,
            Long funcionarioId) {
        
        Stream<FolhaPagamentoEntity> stream = folhaPagamentoRepository.findAllAsStream();
        
        if (mes != null && ano != null) {
            stream = stream.filter(f -> f.getMesReferencia().equals(mes) && f.getAnoReferencia().equals(ano));
        }
        
        if (salarioLiquidoMinimo != null) {
            stream = stream.filter(f -> f.getSalarioLiquido() >= salarioLiquidoMinimo);
        }
        
        if (salarioLiquidoMaximo != null) {
            stream = stream.filter(f -> f.getSalarioLiquido() <= salarioLiquidoMaximo);
        }
        
        if (funcionarioId != null) {
            stream = stream.filter(f -> f.getFuncionario().getId().equals(funcionarioId));
        }
        
        return stream.collect(Collectors.toList());
    }
    
    public EstatisticasFolhas calcularEstatisticas(Integer mes, Integer ano) {
        List<FolhaPagamentoEntity> folhas;
        
        if (mes != null && ano != null) {
            folhas = folhaPagamentoRepository.findByMesReferenciaAndAnoReferencia(mes, ano);
        } else {
            folhas = folhaPagamentoRepository.findAll();
        }
        
        return folhas.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    EstatisticasFolhas stats = new EstatisticasFolhas();
                    stats.setTotalFolhas(list.size());
                    
                    if (!list.isEmpty()) {
                        stats.setTotalSalarioBruto(list.stream()
                            .mapToDouble(FolhaPagamentoEntity::getSalarioBruto)
                            .sum());
                        
                        stats.setTotalSalarioLiquido(list.stream()
                            .mapToDouble(FolhaPagamentoEntity::getSalarioLiquido)
                            .sum());
                        
                        stats.setTotalDescontos(list.stream()
                            .mapToDouble(FolhaPagamentoEntity::getTotalDescontos)
                            .sum());
                        
                        stats.setTotalFGTS(list.stream()
                            .mapToDouble(FolhaPagamentoEntity::getFgts)
                            .sum());
                        
                        stats.setSalarioLiquidoMedio(list.stream()
                            .mapToDouble(FolhaPagamentoEntity::getSalarioLiquido)
                            .average()
                            .orElse(0.0));
                        
                        stats.setSalarioLiquidoMaximo(list.stream()
                            .mapToDouble(FolhaPagamentoEntity::getSalarioLiquido)
                            .max()
                            .orElse(0.0));
                        
                        stats.setSalarioLiquidoMinimo(list.stream()
                            .mapToDouble(FolhaPagamentoEntity::getSalarioLiquido)
                            .min()
                            .orElse(0.0));
                    }
                    
                    return stats;
                }
            ));
    }
    
    public java.util.Map<String, List<FolhaPagamentoEntity>> agruparPorFuncionario() {
        return folhaPagamentoRepository.findAll().stream()
            .collect(Collectors.groupingBy(f -> f.getFuncionario().getNome()));
    }

    public List<FolhaPagamentoEntity> ordenarPorSalarioLiquido(boolean ascendente) {
        Stream<FolhaPagamentoEntity> stream = folhaPagamentoRepository.findAllAsStream();
        
        if (ascendente) {
            return stream.sorted((f1, f2) -> Double.compare(f1.getSalarioLiquido(), f2.getSalarioLiquido()))
                .collect(Collectors.toList());
        } else {
            return stream.sorted((f1, f2) -> Double.compare(f2.getSalarioLiquido(), f1.getSalarioLiquido()))
                .collect(Collectors.toList());
        }
    }
    

    public FolhaPagamentoEntity calcularESalvar(Long funcionarioId) {
        FuncionarioEntity funcionarioEntity = funcionarioRepository.findById(funcionarioId)
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + funcionarioId));
        
        logger.info("Calculando folha de pagamento para funcionário: {}", funcionarioEntity.getNome());
        br.com.folhapagamento.model.Funcionario funcionario = converterEntityParaFuncionario(funcionarioEntity);
        FolhaPagamento folha = calculadoraFolha.calcularFolha(funcionario);
        
        // O evento será publicado dentro do método salvar()
        return salvar(folha, funcionarioId);
    }
    
    private FolhaPagamentoEntity converterParaEntity(FolhaPagamento folha, FuncionarioEntity funcionario) {
        FolhaPagamentoEntity entity = new FolhaPagamentoEntity();
        entity.setFuncionario(funcionario);
        entity.setSalarioBruto(folha.getSalarioBruto());
        entity.setSalarioPorHora(folha.getSalarioPorHora());
        entity.setAdicionalPericulosidade(folha.getAdicionalPericulosidade());
        entity.setAdicionalInsalubridade(folha.getAdicionalInsalubridade());
        entity.setValeAlimentacao(folha.getValeAlimentacao());
        entity.setDescontoValeTransporte(folha.getDescontoValeTransporte());
        entity.setDescontoINSS(folha.getDescontoINSS());
        entity.setDescontoIRRF(folha.getDescontoIRRF());
        entity.setFgts(folha.getFgts());
        entity.setTotalAntesDescontos(folha.getTotalAntesDescontos());
        entity.setTotalDescontos(folha.getTotalDescontos());
        entity.setSalarioLiquido(folha.getSalarioLiquido());
        return entity;
    }
    
    private br.com.folhapagamento.model.Funcionario converterEntityParaFuncionario(FuncionarioEntity entity) {
        br.com.folhapagamento.model.Funcionario funcionario = new br.com.folhapagamento.model.Funcionario();
        funcionario.setNome(entity.getNome());
        funcionario.setCpf(entity.getCpf());
        funcionario.setCargo(entity.getCargo());
        funcionario.setTipo(entity.getTipo());
        funcionario.setSalarioBruto(entity.getSalarioBruto());
        funcionario.setNumeroDependentes(entity.getNumeroDependentes());
        funcionario.setRecebePericulosidade(Boolean.TRUE.equals(entity.getRecebePericulosidade()));
        funcionario.setGrauInsalubridade(entity.getGrauInsalubridade());
        funcionario.setValorValeTransporte(entity.getValorValeTransporte());
        funcionario.setValorValeAlimentacao(entity.getValorValeAlimentacao());
        return funcionario;
    }
    
    public static class EstatisticasFolhas {
        private int totalFolhas;
        private double totalSalarioBruto;
        private double totalSalarioLiquido;
        private double totalDescontos;
        private double totalFGTS;
        private double salarioLiquidoMedio;
        private double salarioLiquidoMaximo;
        private double salarioLiquidoMinimo;
        
        public int getTotalFolhas() {
            return totalFolhas;
        }
        
        public void setTotalFolhas(int totalFolhas) {
            this.totalFolhas = totalFolhas;
        }
        
        public double getTotalSalarioBruto() {
            return totalSalarioBruto;
        }
        
        public void setTotalSalarioBruto(double totalSalarioBruto) {
            this.totalSalarioBruto = totalSalarioBruto;
        }
        
        public double getTotalSalarioLiquido() {
            return totalSalarioLiquido;
        }
        
        public void setTotalSalarioLiquido(double totalSalarioLiquido) {
            this.totalSalarioLiquido = totalSalarioLiquido;
        }
        
        public double getTotalDescontos() {
            return totalDescontos;
        }
        
        public void setTotalDescontos(double totalDescontos) {
            this.totalDescontos = totalDescontos;
        }
        
        public double getTotalFGTS() {
            return totalFGTS;
        }
        
        public void setTotalFGTS(double totalFGTS) {
            this.totalFGTS = totalFGTS;
        }
        
        public double getSalarioLiquidoMedio() {
            return salarioLiquidoMedio;
        }
        
        public void setSalarioLiquidoMedio(double salarioLiquidoMedio) {
            this.salarioLiquidoMedio = salarioLiquidoMedio;
        }
        
        public double getSalarioLiquidoMaximo() {
            return salarioLiquidoMaximo;
        }
        
        public void setSalarioLiquidoMaximo(double salarioLiquidoMaximo) {
            this.salarioLiquidoMaximo = salarioLiquidoMaximo;
        }
        
        public double getSalarioLiquidoMinimo() {
            return salarioLiquidoMinimo;
        }
        
        public void setSalarioLiquidoMinimo(double salarioLiquidoMinimo) {
            this.salarioLiquidoMinimo = salarioLiquidoMinimo;
        }
    }
}

