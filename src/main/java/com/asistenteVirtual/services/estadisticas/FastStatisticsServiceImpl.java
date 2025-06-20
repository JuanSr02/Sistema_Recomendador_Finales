package com.asistenteVirtual.services.estadisticas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.DTOs.MateriaRankingDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.EstadisticasGenerales;
import com.asistenteVirtual.model.EstadisticasMateria;
import com.asistenteVirtual.repository.EstadisticasGeneralesRepository;
import com.asistenteVirtual.repository.EstadisticasMateriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FastStatisticsServiceImpl implements FastStatisticsService {

    private final EstadisticasGeneralesRepository estadisticasGeneralesRepo;
    private final EstadisticasMateriaRepository estadisticasMateriaRepo;
    private final ObjectMapper objectMapper;
    private final EstadisticasMapper estadisticasMapper;

    @Override
    public EstadisticasGeneralesDTO getCachedGeneralStatistics() {
        EstadisticasGenerales stats = estadisticasGeneralesRepo.findFirstByOrderByFechaUltimaActualizacionDesc()
                .orElseThrow(() -> new ResourceNotFoundException("No hay estadísticas generales almacenadas"));

        try {
            return EstadisticasGeneralesDTO.builder()
                    .estudiantesActivos(stats.getEstudiantesActivos())
                    .totalMaterias(stats.getTotalMaterias())
                    .totalExamenesRendidos(stats.getTotalExamenesRendidos())
                    .porcentajeAprobadosGeneral(stats.getPorcentajeAprobadosGeneral())
                    .promedioGeneral(stats.getPromedioGeneral())
                    .distribucionEstudiantesPorCarrera(
                            objectMapper.readValue(stats.getDistribucionEstudiantesPorCarrera(),
                                    new TypeReference<Map<String, Integer>>() {}))
                    .distribucionExamenesPorMateria(
                            objectMapper.readValue(stats.getDistribucionExamenesPorMateria(),
                                    new TypeReference<Map<String, Integer>>() {}))
                    .top5Aprobadas(
                            objectMapper.readValue(stats.getTop5Aprobadas(),
                                    new TypeReference<List<MateriaRankingDTO>>() {}))
                    .top5Reprobadas(
                            objectMapper.readValue(stats.getTop5Reprobadas(),
                                    new TypeReference<List<MateriaRankingDTO>>() {}))
                    .promedioNotasPorMateria(
                            objectMapper.readValue(stats.getPromedioNotasPorMateria(),
                                    new TypeReference<Map<String, Double>>() {}))
                    .materiaMasRendida(
                            objectMapper.readValue(stats.getMateriaMasRendida(), MateriaRankingDTO.class))
                    .cantidadMateriaMasRendida(stats.getCantidadMateriaMasRendida())
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Error al deserializar estadísticas generales", e);
            throw new RuntimeException("Error al procesar estadísticas almacenadas", e);
        }
    }

    @Override
    public EstadisticasMateriaDTO getCachedMateriaStatistics(String codigoMateria) {
        EstadisticasMateria stats = estadisticasMateriaRepo.findById(codigoMateria)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay estadísticas almacenadas para la materia: " + codigoMateria));
        return estadisticasMapper.convertToDTO(stats);
    }
}