package pe.edu.upeu.asistencia.servicio;

import pe.edu.upeu.asistencia.Modelo.Participante;

import java.util.List;

public interface ParticipanteServicioI {
    void save(Participante participante);
    List<Participante> findAll();
    Participante update(Participante participante);
    void delete(int index);

    Participante findById(int index);
}
