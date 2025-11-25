package org.example.dto;

import java.time.LocalDateTime;

public class BoletoRequest {
    private String numeroVuelo;
    private String pasajero;
    private String origen;
    private String destino;
    private LocalDateTime fechaVuelo;
    private String asiento;
    private Double precio;

    // Getters y Setters
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
}