package pe.edu.upeu.cine.modelo;

public class Boleto {
    private int idBoleto;
    private String cliente;
    private String funcion;
    private int cantidad;
    private double total;

    public Boleto(int idBoleto, String cliente, String funcion, int cantidad, double total) {
        this.idBoleto = idBoleto;
        this.cliente = cliente;
        this.funcion = funcion;
        this.cantidad = cantidad;
        this.total = total;
    }

    public int getIdBoleto() { return idBoleto; }
    public String getCliente() { return cliente; }
    public String getFuncion() { return funcion; }
    public int getCantidad() { return cantidad; }
    public double getTotal() { return total; }
}
