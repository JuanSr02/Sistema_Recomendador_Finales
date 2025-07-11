package com.asistenteVirtual.services.planEstudio;

import com.asistenteVirtual.exceptions.PlanEstudioValidationException;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.repository.PlanDeEstudioRepository;
import com.asistenteVirtual.services.ExcelProcessingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanValidatorImpl implements PlanValidator {

    private final PlanDeEstudioRepository planDeEstudioRepo;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("xls", "xlsx");

    @Override
    public void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo no puede estar vacío");
        }

        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new PlanEstudioValidationException(
                    "Solo se permiten archivos Excel (.xls, .xlsx)");
        }
    }

    @Override
    public void validarFilaPlan(Row planRow) {
        if (planRow == null || planRow.getCell(0) == null) {
            throw new PlanEstudioValidationException(
                    "No se encontró la información del plan de estudios en la primera fila");
        }

        String propuesta = ExcelProcessingUtils.extractCellValue(planRow.getCell(0));
        if (propuesta == null || propuesta.trim().isEmpty()) {
            throw new PlanEstudioValidationException(
                    "La propuesta del plan de estudios no puede estar vacía");
        }

        String codigoPlan = ExcelProcessingUtils.extractCellValue(planRow.getCell(1));
        if (codigoPlan == null || codigoPlan.trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se pudo encontrar el código del plan de estudios");
        }
        validarPlan(codigoPlan);
    }

    @Override
    public void validarPlan (String codigoPlan){
        if(planDeEstudioRepo.existsById(codigoPlan)){
            throw new PlanEstudioValidationException("El plan de estudios con codigo" + codigoPlan + "ya esta cargado");
        }
    }

    @Override
    public void validarFilaMateria(Row row, int rowNum) {
        if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(5) == null) {
            throw new PlanEstudioValidationException(
                    String.format("Fila %d: Faltan datos requeridos para la materia", rowNum + 1));
        }

        String codigoMateria = ExcelProcessingUtils.extractCellValue(row.getCell(1));
        if (codigoMateria == null || codigoMateria.trim().isEmpty()) {
            throw new PlanEstudioValidationException(
                    String.format("Fila %d: El código de la materia no puede estar vacío", rowNum + 1));
        }
    }
}