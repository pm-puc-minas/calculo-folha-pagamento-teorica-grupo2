package br.com.folhapagamento.repository;

import br.com.folhapagamento.model.entity.FolhaPagamentoEntity;
import br.com.folhapagamento.model.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface FolhaPagamentoRepository extends JpaRepository<FolhaPagamentoEntity, Long> {
    
    List<FolhaPagamentoEntity> findByFuncionario(FuncionarioEntity funcionario);
    
    List<FolhaPagamentoEntity> findByFuncionarioId(Long funcionarioId);
    
    List<FolhaPagamentoEntity> findByMesReferenciaAndAnoReferencia(Integer mes, Integer ano);
    
    @Query("SELECT f FROM FolhaPagamentoEntity f WHERE f.salarioLiquido >= :salarioMinimo")
    List<FolhaPagamentoEntity> findBySalarioLiquidoGreaterThanEqual(Double salarioMinimo);
    
    @Query("SELECT f FROM FolhaPagamentoEntity f WHERE f.salarioLiquido BETWEEN :salarioMin AND :salarioMax")
    List<FolhaPagamentoEntity> findBySalarioLiquidoBetween(Double salarioMin, Double salarioMax);
    
    @Query("SELECT f FROM FolhaPagamentoEntity f WHERE f.totalDescontos > :valorMinimo")
    List<FolhaPagamentoEntity> findByTotalDescontosGreaterThan(Double valorMinimo);
    
    // Métodos que retornam Stream para processamento eficiente
    @Query("SELECT f FROM FolhaPagamentoEntity f")
    Stream<FolhaPagamentoEntity> findAllAsStream();
    
    @Query("SELECT f FROM FolhaPagamentoEntity f WHERE f.mesReferencia = :mes AND f.anoReferencia = :ano")
    Stream<FolhaPagamentoEntity> findByMesAndAnoAsStream(Integer mes, Integer ano);
    
    @Query("SELECT f FROM FolhaPagamentoEntity f WHERE f.funcionario.id = :funcionarioId")
    Stream<FolhaPagamentoEntity> findByFuncionarioIdAsStream(Long funcionarioId);
    
    @Query("SELECT f FROM FolhaPagamentoEntity f WHERE f.salarioLiquido >= :salarioMinimo")
    Stream<FolhaPagamentoEntity> findBySalarioLiquidoGreaterThanEqualAsStream(Double salarioMinimo);
}

