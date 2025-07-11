package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.MateriaDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.repository.EstudianteRepository;
import com.asistenteVirtual.repository.HistoriaAcademicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingFinalesServiceImpl implements RankingFinalesService {

    private final EstudianteRepository estudianteRepo;
    private final HistoriaAcademicaRepository historiaAcademicaRepo;
    private final FinalesCalculator finalesCalculator;

    @Override
    public List<FinalDTO> obtenerFinalesParaRendir(Long estudianteId, OrdenFinales orden) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        HistoriaAcademica historia = historiaAcademicaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada"));

        return finalesCalculator.calcularFinalesParaRendir(historia, orden);
    }

    @Override
    public List<MateriaDTO> obtenerFinalesParaInscribirse(Long estudianteId) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        HistoriaAcademica historia = historiaAcademicaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada"));
        List<FinalDTO> finales = finalesCalculator.mapearARenglonDTO(finalesCalculator.obtenerRegularesAprobadas(historia));
        List<MateriaDTO> finalesAInscribirse = new ArrayList<>();
        for (FinalDTO finalDTO : finales) {
            finalesAInscribirse.add(MateriaDTO.builder().codigo(finalDTO.getCodigoMateria()).nombre(finalDTO.getNombreMateria()).build());
        }
        return finalesAInscribirse;
    }
}