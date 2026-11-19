package com.example.dymcyber.repository;

import com.example.dymcyber.model.EvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluacionRepository extends JpaRepository<EvaluacionEntity, Long> {

	Optional<EvaluacionEntity> findByCandidataId(Long candidataId);

	boolean existsByCandidataId(Long candidataId);

	List<EvaluacionEntity> findAllByOrderByPuntajePromedioDesc();
}
