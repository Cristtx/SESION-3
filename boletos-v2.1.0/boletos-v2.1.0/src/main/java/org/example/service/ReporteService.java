package org.example.service;

import java.util.Map;

public class ReporteService {

    public Map<String, Integer> obtenerPaisMasConcurridos(String mes) {

        // Aquí iría tu consulta real SQL a la base de datos
        // Por ahora te dejo datos simulados

        return Map.of(
                "Chile", 120,
                "Argentina", 80,
                "Bolivia", 40,
                "Brasil", 30,
                "Perú", 200
        );
    }
}
