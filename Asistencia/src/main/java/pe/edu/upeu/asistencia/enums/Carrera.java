package pe.edu.upeu.asistencia.enums;

public enum Carrera {
    SISTEMAS(Facultad.FIA),
    CIVIL(Facultad.FIA),
    NUTRICIONAL(Facultad.FCS),
    ENFERMERIA(Facultad.FCS),
    ADMINISTRACION(Facultad.FCE),
    GEGNERAL(Facultad.GENERAL),
    EEDUCACION(Facultad.FACIHED),
    ;

    private Facultad facultad;
    Carrera(Facultad facultad){
        this.facultad = facultad;
    }
    public Facultad getFacultad() {
        return facultad;
    }
}
