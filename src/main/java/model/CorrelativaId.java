package model;

import lombok.Data;
import java.io.Serializable;

@Data
public class CorrelativaId implements Serializable {
    private String materia;
    private String correlativa;
    private String planDeEstudio;
}


