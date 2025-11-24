package pe.edu.upeu.cine.modelo;

import javafx.beans.property.*;

public class Venta {

    private IntegerProperty idVenta = new SimpleIntegerProperty();
    private IntegerProperty idFuncion = new SimpleIntegerProperty();
    private IntegerProperty cantidad = new SimpleIntegerProperty();
    private DoubleProperty total = new SimpleDoubleProperty();

    private StringProperty pelicula = new SimpleStringProperty();
    private StringProperty fecha = new SimpleStringProperty();
    private StringProperty hora = new SimpleStringProperty();
    private StringProperty asientos = new SimpleStringProperty();

    public int getIdVenta() { return idVenta.get(); }
    public IntegerProperty idVentaProperty() { return idVenta; }
    public void setIdVenta(int id) { this.idVenta.set(id); }

    public int getIdFuncion() { return idFuncion.get(); }
    public void setIdFuncion(int id) { this.idFuncion.set(id); }

    public int getCantidad() { return cantidad.get(); }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public void setCantidad(int c) { this.cantidad.set(c); }

    public double getTotal() { return total.get(); }
    public DoubleProperty totalProperty() { return total; }
    public void setTotal(double t) { this.total.set(t); }

    public String getPelicula() { return pelicula.get(); }
    public StringProperty peliculaProperty() { return pelicula; }
    public void setPelicula(String p) { this.pelicula.set(p); }

    public String getFecha() { return fecha.get(); }
    public StringProperty fechaProperty() { return fecha; }
    public void setFecha(String f) { this.fecha.set(f); }

    public String getHora() { return hora.get(); }
    public StringProperty horaProperty() { return hora; }
    public void setHora(String h) { this.hora.set(h); }

    public String getAsientos() { return asientos.get(); }
    public StringProperty asientosProperty() { return asientos; }
    public void setAsientos(String a) { this.asientos.set(a); }
}
