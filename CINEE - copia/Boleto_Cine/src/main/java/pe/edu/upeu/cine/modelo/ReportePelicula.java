package pe.edu.upeu.cine.modelo;

public class ReportePelicula {

    private String pelicula;
    private int totalEntradas;
    private double totalRecaudado;

    public ReportePelicula(String pelicula, int totalEntradas, double totalRecaudado) {
        this.pelicula = pelicula;
        this.totalEntradas = totalEntradas;
        this.totalRecaudado = totalRecaudado;
    }

    public String getPelicula() {
        return pelicula;
    }

    public int getTotalEntradas() {
        return totalEntradas;
    }

    public double getTotalRecaudado() {
        return totalRecaudado;
    }
}
