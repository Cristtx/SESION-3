package pe.edu.upeu.asistencia.repositorio;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import pe.edu.upeu.asistencia.Modelo.Participante;
import pe.edu.upeu.asistencia.enums.Carrera;
import pe.edu.upeu.asistencia.enums.TipoParticipante;

import java.util.ArrayList;
import java.util.List;

public  abstract class ParticipanteRepository {
    protected List<Participante> participantes = new ArrayList<>();

    public List<Participante> findAll() {
        // Agregamos solo si la lista está vacía, para evitar duplicados
        if (participantes.isEmpty()) {
            participantes.add(new Participante(
                    new SimpleStringProperty("J225255"),
                    new SimpleStringProperty("juan"),
                    new SimpleStringProperty("mamani"),
                    Carrera.SISTEMAS,
                    TipoParticipante.ASISTENTE,
                    new SimpleBooleanProperty(true)
            ));
        }
        return participantes; // ← Aquí retornamos la lista
    }
}
