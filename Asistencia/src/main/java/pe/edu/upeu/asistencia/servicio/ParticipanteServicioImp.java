package pe.edu.upeu.asistencia.servicio;

import pe.edu.upeu.asistencia.Modelo.Participante;
import pe.edu.upeu.asistencia.repositorio.ParticipanteRepository;

import java.util.List;

public class ParticipanteServicioImp extends ParticipanteRepository
            implements ParticipanteServicioI {


    @Override
    public void save(Participante participante) {

    }

    @Override
    public Participante update(Participante participante) {
        return null;
    }

    @Override
    public void delete(int index) {

    }

    @Override
    public Participante findById(int index) {
        return null;
    }
    @Override
    public List<Participante> findAll() {
        if (participantes.isEmpty()) {
            return super.findAll();
        }
        return participantes;
    }

}
