package br.com.folhapagamento.repository;

import br.com.folhapagamento.model.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface FuncionarioRepository extends JpaRepository<FuncionarioEntity, Long> {
    
    Optional<FuncionarioEntity> findByCpf(String cpf);
    
    List<FuncionarioEntity> findByTipo(String tipo);
    
    List<FuncionarioEntity> findByCargo(String cargo);
    
    @Query("SELECT f FROM FuncionarioEntity f WHERE f.salarioBruto >= :salarioMinimo")
    List<FuncionarioEntity> findBySalarioBrutoGreaterThanEqual(Double salarioMinimo);
    
    @Query("SELECT f FROM FuncionarioEntity f WHERE f.salarioBruto BETWEEN :salarioMin AND :salarioMax")
    List<FuncionarioEntity> findBySalarioBrutoBetween(Double salarioMin, Double salarioMax);
    
    @Query("SELECT f FROM FuncionarioEntity f WHERE f.numeroDependentes > 0")
    List<FuncionarioEntity> findComDependentes();
    
    @Query("SELECT f FROM FuncionarioEntity f WHERE f.recebePericulosidade = true")
    List<FuncionarioEntity> findComPericulosidade();
    
    @Query("SELECT f FROM FuncionarioEntity f WHERE f.grauInsalubridade IS NOT NULL AND f.grauInsalubridade != ''")
    List<FuncionarioEntity> findComInsalubridade();
    
    @Query("SELECT f FROM FuncionarioEntity f")
    Stream<FuncionarioEntity> findAllAsStream();
    
    @Query("SELECT f FROM FuncionarioEntity f WHERE f.tipo = :tipo")
    Stream<FuncionarioEntity> findByTipoAsStream(String tipo);
    
    @Query("SELECT f FROM FuncionarioEntity f WHERE f.salarioBruto >= :salarioMinimo")
    Stream<FuncionarioEntity> findBySalarioBrutoGreaterThanEqualAsStream(Double salarioMinimo);
}

