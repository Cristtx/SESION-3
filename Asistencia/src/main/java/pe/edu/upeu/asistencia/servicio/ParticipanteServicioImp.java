package pe.edu.upeu.asistencia.servicio;

import org.springframework.stereotype.Service;
import pe.edu.upeu.asistencia.modelo.Participante;
import pe.edu.upeu.asistencia.repositorio.ParticipanteRepository;
import java.util.List;

@Service
public class ParticipanteServicioImp extends ParticipanteRepository
        implements ParticipanteServicioI {
    @Override
    public void save(Participante participante) {
        super.save(participante);
    }
    @Override
    public Participante update(Participante participante, int index) {
        return participantes.set(index, participante);
    }
    @Override
    public void delete(int index) {
        participantes.remove(index);
    }
    @Override
    public Participante findById(int index) {
        return participantes.get(index);
    }

    @Override
    public List<Participante> findAll(){
            return super.findAll();

    }
}
