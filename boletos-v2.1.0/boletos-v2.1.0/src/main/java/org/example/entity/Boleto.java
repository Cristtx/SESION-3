package org.example.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Boleto {
    private Long id;
    private String numeroVuelo;
    private String pasajero;
    private String origen;
    private String destino;
    private LocalDateTime fechaVuelo;
    private String asiento;
    private Double precio;
    private String codigoReserva;
    private LocalDateTime fechaCreacion;

    public Boleto() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Boleto(String numeroVuelo, String pasajero, String origen, String destino,
                  LocalDateTime fechaVuelo, String asiento, Double precio) {
        this();
        this.numeroVuelo = numeroVuelo;
        this.pasajero = pasajero;
        this.origen = origen;
        this.destino = destino;
        this.fechaVuelo = fechaVuelo;
        this.asiento = asiento;
        this.precio = precio;
        this.codigoReserva = generarCodigoReserva();
    }

    private String generarCodigoReserva() {
        return "RES" + System.currentTimeMillis();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroVuelo() { return numeroVuelo; }
    public void setNumeroVuelo(String numeroVuelo) { this.numeroVuelo = numeroVuelo; }

    public String getPasajero() { return pasajero; }
    public void setPasajero(String pasajero) { this.pasajero = pasajero; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public LocalDateTime getFechaVuelo() { return fechaVuelo; }
    public void setFechaVuelo(LocalDateTime fechaVuelo) { this.fechaVuelo = fechaVuelo; }

    public String getAsiento() { return asiento; }
    public void setAsiento(String asiento) { this.asiento = asiento; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getCodigoReserva() { return codigoReserva; }
    public void setCodigoReserva(String codigoReserva) { this.codigoReserva = codigoReserva; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("%s - %s a %s - %s (%s)",
                numeroVuelo, origen, destino,
                fechaVuelo.format(formatter), pasajero);
    }
}