package com.example.dymcyber.repository;

import com.example.dymcyber.model.CandidataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidataRepository extends JpaRepository<CandidataEntity, Long> {

	List<CandidataEntity> findAllByOrderByIdDesc();
}
