package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanDeEstudioRepository extends JpaRepository<PlanDeEstudio, String> {

    Optional<Object> findByPropuesta(String nombrePlan);
}

