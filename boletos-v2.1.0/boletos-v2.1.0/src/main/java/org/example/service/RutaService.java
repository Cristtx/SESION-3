package org.example.service;

import org.example.repository.RutaRepository;
import java.util.*;

public class RutaService {
    private RutaRepository rutaRepository;
    private static final List<String> CIUDADES_DISPONIBLES = Arrays.asList(
            "Lima", "Bogotá", "Buenos Aires", "Santiago", "México DF",
            "Madrid", "Roma", "París", "Londres", "Nueva York",
            "Miami", "Los Ángeles", "Tokio", "Sídney", "Dubái",
            "Berlín", "Ámsterdam", "Toronto", "São Paulo", "Shanghái"
    );

    public RutaService() {
        this.rutaRepository = new RutaRepository();
    }

    public List<String> getCiudadesDisponibles() {
        return new ArrayList<>(CIUDADES_DISPONIBLES);
    }

    public Map<String, Double> getDestinosDesde(String origen) {
        return rutaRepository.obtenerRutasDesde(origen);
    }

    public Double getPrecio(String origen, String destino) {
        Map<String, Double> destinos = rutaRepository.obtenerRutasDesde(origen);
        return destinos != null ? destinos.get(destino) : null;
    }

    public String generarNumeroVuelo(String origen, String destino) {
        Map<String, String> codigosAeropuerto = new HashMap<>();
        codigosAeropuerto.put("Lima", "LIM");
        codigosAeropuerto.put("Bogotá", "BOG");
        codigosAeropuerto.put("Buenos Aires", "EZE");
        codigosAeropuerto.put("Santiago", "SCL");
        codigosAeropuerto.put("México DF", "MEX");
        codigosAeropuerto.put("Madrid", "MAD");
        codigosAeropuerto.put("Roma", "FCO");
        codigosAeropuerto.put("París", "CDG");
        codigosAeropuerto.put("Londres", "LHR");
        codigosAeropuerto.put("Nueva York", "JFK");
        codigosAeropuerto.put("Miami", "MIA");
        codigosAeropuerto.put("Los Ángeles", "LAX");
        codigosAeropuerto.put("Tokio", "NRT");
        codigosAeropuerto.put("Sídney", "SYD");
        codigosAeropuerto.put("Dubái", "DXB");
        codigosAeropuerto.put("Berlín", "BER");
        codigosAeropuerto.put("Ámsterdam", "AMS");
        codigosAeropuerto.put("Toronto", "YYZ");
        codigosAeropuerto.put("São Paulo", "GRU");
        codigosAeropuerto.put("Shanghái", "PVG");

        String codigoOrigen = codigosAeropuerto.getOrDefault(origen, "FLY");
        String codigoDestino = codigosAeropuerto.getOrDefault(destino, "DST");

        Random random = new Random();
        int numeroVuelo = random.nextInt(900) + 100;
        return codigoOrigen + codigoDestino + numeroVuelo;
    }

    public boolean actualizarPrecioRuta(String origen, String destino, double nuevoPrecio) {
        return rutaRepository.actualizarPrecio(origen, destino, nuevoPrecio);
    }

    public boolean agregarRuta(String origen, String destino, double precio) {
        return rutaRepository.agregarRuta(origen, destino, precio);
    }

    public boolean eliminarRuta(String origen, String destino) {
        return rutaRepository.eliminarRuta(origen, destino);
    }

    public Map<String, Map<String, Double>> obtenerTodasLasRutas() {
        return rutaRepository.obtenerTodasLasRutas();
    }

    private void insertarRutasPorDefecto() {
        String[][] rutas = {
                // Desde Lima
                {"Lima", "Bogotá", "350.00"},
                {"Lima", "Buenos Aires", "550.00"},
                {"Lima", "Santiago", "280.00"},
                {"Lima", "Roma", "920.00"},
                {"Lima", "Brasil", "550.00"},

                // Desde Chile
                {"Chile", "Brasil", "800.00"},
                {"Chile", "Buenos Aires", "450.00"},
                {"Chile", "Lima", "300.00"},

                // Desde Brasil
                {"Brasil", "Lima", "600.00"},
                {"Brasil", "Chile", "750.00"},
                {"Brasil", "Miami", "900.00"},

                // Desde Argentina
                {"Buenos Aires", "Lima", "500.00"},
                {"Buenos Aires", "Chile", "400.00"},
                {"Buenos Aires", "Brasil", "350.00"}
        };}}
