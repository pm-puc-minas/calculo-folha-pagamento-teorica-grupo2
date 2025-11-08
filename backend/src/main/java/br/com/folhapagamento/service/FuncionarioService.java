package br.com.folhapagamento.service;

import br.com.folhapagamento.event.FuncionarioCadastradoEvent;
import br.com.folhapagamento.model.Funcionario;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
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
public class FuncionarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(FuncionarioService.class);
    
    @Autowired
    private FuncionarioRepository funcionarioRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
   
    public FuncionarioEntity salvar(Funcionario funcionario) {
        FuncionarioEntity entity = converterParaEntity(funcionario);
        FuncionarioEntity saved = funcionarioRepository.save(entity);
        logger.info("Funcionário salvo com sucesso: {}", saved.getNome());
      
        FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(
            saved, 
            "Funcionário cadastrado no sistema"
        );
        eventPublisher.publishEvent(event);
        
        return saved;
    }
    
    public List<FuncionarioEntity> buscarTodos() {
        return funcionarioRepository.findAll();
    }
    
    public Optional<FuncionarioEntity> buscarPorId(Long id) {
        return funcionarioRepository.findById(id);
    }
    
    public Optional<FuncionarioEntity> buscarPorCpf(String cpf) {
        return funcionarioRepository.findByCpf(cpf);
    }
  
    public FuncionarioEntity atualizar(Long id, Funcionario funcionario) {
        FuncionarioEntity entity = funcionarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Funcionário não encontrado com ID: " + id));
        
        atualizarEntity(entity, funcionario);
        FuncionarioEntity updated = funcionarioRepository.save(entity);
        logger.info("Funcionário atualizado com sucesso: {}", updated.getNome());
      
        FuncionarioCadastradoEvent event = new FuncionarioCadastradoEvent(
            updated, 
            "Dados do funcionário atualizados"
        );
        eventPublisher.publishEvent(event);
        
        return updated;
    }
  
    public void deletar(Long id) {
        funcionarioRepository.deleteById(id);
        logger.info("Funcionário deletado com sucesso: ID {}", id);
    }
   
    public List<FuncionarioEntity> filtrarPorTipo(String tipo) {
        return funcionarioRepository.findByTipo(tipo);
    }
 
    public List<FuncionarioEntity> filtrarPorCargo(String cargo) {
        return funcionarioRepository.findByCargo(cargo);
    }
 
    public List<FuncionarioEntity> filtrarPorFaixaSalarial(Double salarioMin, Double salarioMax) {
        return funcionarioRepository.findBySalarioBrutoBetween(salarioMin, salarioMax);
    }
   
    public List<FuncionarioEntity> filtrarPorSalarioMinimo(Double salarioMinimo) {
        return funcionarioRepository.findBySalarioBrutoGreaterThanEqual(salarioMinimo);
    }
    
    public List<FuncionarioEntity> processarEFiltrar(
            String tipo,
            Double salarioMinimo,
            Boolean comDependentes,
            Boolean comPericulosidade) {
        
        Stream<FuncionarioEntity> stream = funcionarioRepository.findAllAsStream();
        
        if (tipo != null && !tipo.isEmpty()) {
            stream = stream.filter(f -> tipo.equalsIgnoreCase(f.getTipo()));
        }
        
        if (salarioMinimo != null) {
            stream = stream.filter(f -> f.getSalarioBruto() >= salarioMinimo);
        }
        
        if (comDependentes != null && comDependentes) {
            stream = stream.filter(f -> f.getNumeroDependentes() > 0);
        }
        
        if (comPericulosidade != null && comPericulosidade) {
            stream = stream.filter(f -> Boolean.TRUE.equals(f.getRecebePericulosidade()));
        }
        
        return stream.collect(Collectors.toList());
    }
   
    public EstatisticasFuncionarios calcularEstatisticas() {
        List<FuncionarioEntity> funcionarios = funcionarioRepository.findAll();
        
        return funcionarios.stream()
            .collect(Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    EstatisticasFuncionarios stats = new EstatisticasFuncionarios();
                    stats.setTotalFuncionarios(list.size());
                    
                    if (!list.isEmpty()) {
                        stats.setSalarioMedio(list.stream()
                            .mapToDouble(FuncionarioEntity::getSalarioBruto)
                            .average()
                            .orElse(0.0));
                        
                        stats.setSalarioMaximo(list.stream()
                            .mapToDouble(FuncionarioEntity::getSalarioBruto)
                            .max()
                            .orElse(0.0));
                        
                        stats.setSalarioMinimo(list.stream()
                            .mapToDouble(FuncionarioEntity::getSalarioBruto)
                            .min()
                            .orElse(0.0));
                        
                        stats.setTotalDependentes(list.stream()
                            .mapToInt(FuncionarioEntity::getNumeroDependentes)
                            .sum());
                        
                        stats.setFuncionariosComPericulosidade(list.stream()
                            .filter(f -> Boolean.TRUE.equals(f.getRecebePericulosidade()))
                            .count());
                        
                        stats.setFuncionariosComInsalubridade(list.stream()
                            .filter(f -> f.getGrauInsalubridade() != null && !f.getGrauInsalubridade().isEmpty())
                            .count());
                    }
                    
                    return stats;
                }
            ));
    }
  
    public java.util.Map<String, List<FuncionarioEntity>> agruparPorCargo() {
        return funcionarioRepository.findAll().stream()
            .collect(Collectors.groupingBy(FuncionarioEntity::getCargo));
    }

    public java.util.Map<String, List<FuncionarioEntity>> agruparPorTipo() {
        return funcionarioRepository.findAll().stream()
            .collect(Collectors.groupingBy(FuncionarioEntity::getTipo));
    }
  
    public List<FuncionarioEntity> ordenarPorSalario(boolean ascendente) {
        Stream<FuncionarioEntity> stream = funcionarioRepository.findAllAsStream();
        
        if (ascendente) {
            return stream.sorted((f1, f2) -> Double.compare(f1.getSalarioBruto(), f2.getSalarioBruto()))
                .collect(Collectors.toList());
        } else {
            return stream.sorted((f1, f2) -> Double.compare(f2.getSalarioBruto(), f1.getSalarioBruto()))
                .collect(Collectors.toList());
        }
    }
  
    private FuncionarioEntity converterParaEntity(Funcionario funcionario) {
        FuncionarioEntity entity = new FuncionarioEntity();
        entity.setNome(funcionario.getNome());
        entity.setCpf(funcionario.getCpf());
        entity.setCargo(funcionario.getCargo());
        entity.setTipo(funcionario.getTipo());
        entity.setSalarioBruto(funcionario.getSalarioBruto());
        entity.setNumeroDependentes(funcionario.getNumeroDependentes());
        entity.setRecebePericulosidade(funcionario.isRecebePericulosidade());
        entity.setGrauInsalubridade(funcionario.getGrauInsalubridade());
        entity.setValorValeTransporte(funcionario.getValorValeTransporte());
        entity.setValorValeAlimentacao(funcionario.getValorValeAlimentacao());
        return entity;
    }
   
    private void atualizarEntity(FuncionarioEntity entity, Funcionario funcionario) {
        entity.setNome(funcionario.getNome());
        entity.setCpf(funcionario.getCpf());
        entity.setCargo(funcionario.getCargo());
        entity.setTipo(funcionario.getTipo());
        entity.setSalarioBruto(funcionario.getSalarioBruto());
        entity.setNumeroDependentes(funcionario.getNumeroDependentes());
        entity.setRecebePericulosidade(funcionario.isRecebePericulosidade());
        entity.setGrauInsalubridade(funcionario.getGrauInsalubridade());
        entity.setValorValeTransporte(funcionario.getValorValeTransporte());
        entity.setValorValeAlimentacao(funcionario.getValorValeAlimentacao());
    }
    
    public static class EstatisticasFuncionarios {
        private int totalFuncionarios;
        private double salarioMedio;
        private double salarioMaximo;
        private double salarioMinimo;
        private int totalDependentes;
        private long funcionariosComPericulosidade;
        private long funcionariosComInsalubridade;
        
        // Getters e Setters
        public int getTotalFuncionarios() {
            return totalFuncionarios;
        }
        
        public void setTotalFuncionarios(int totalFuncionarios) {
            this.totalFuncionarios = totalFuncionarios;
        }
        
        public double getSalarioMedio() {
            return salarioMedio;
        }
        
        public void setSalarioMedio(double salarioMedio) {
            this.salarioMedio = salarioMedio;
        }
        
        public double getSalarioMaximo() {
            return salarioMaximo;
        }
        
        public void setSalarioMaximo(double salarioMaximo) {
            this.salarioMaximo = salarioMaximo;
        }
        
        public double getSalarioMinimo() {
            return salarioMinimo;
        }
        
        public void setSalarioMinimo(double salarioMinimo) {
            this.salarioMinimo = salarioMinimo;
        }
        
        public int getTotalDependentes() {
            return totalDependentes;
        }
        
        public void setTotalDependentes(int totalDependentes) {
            this.totalDependentes = totalDependentes;
        }
        
        public long getFuncionariosComPericulosidade() {
            return funcionariosComPericulosidade;
        }
        
        public void setFuncionariosComPericulosidade(long funcionariosComPericulosidade) {
            this.funcionariosComPericulosidade = funcionariosComPericulosidade;
        }
        
        public long getFuncionariosComInsalubridade() {
            return funcionariosComInsalubridade;
        }
        
        public void setFuncionariosComInsalubridade(long funcionariosComInsalubridade) {
            this.funcionariosComInsalubridade = funcionariosComInsalubridade;
        }
    }
}

