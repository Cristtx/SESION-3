package pe.edu.upeu.cine.modelo;

public class Funcion {

    private int idFuncion;
    private int idPelicula;
    private String tituloPelicula;
    private String fecha;
    private String hora;
    private String sala;
    private double precio;


    public Funcion(int idFuncion, int idPelicula, String tituloPelicula,
                   String fecha, String hora, String sala, double precio) {
        this.idFuncion = idFuncion;
        this.idPelicula = idPelicula;
        this.tituloPelicula = tituloPelicula;
        this.fecha = fecha;
        this.hora = hora;
        this.sala = sala;
        this.precio = precio;
    }


    public Funcion() {}

    public int getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
    }

    public int getIdPelicula() {
        return idPelicula;
    }

    public void setIdPelicula(int idPelicula) {
        this.idPelicula = idPelicula;
    }

    public String getTituloPelicula() {
        return tituloPelicula;
    }

    public void setTituloPelicula(String tituloPelicula) {
        this.tituloPelicula = tituloPelicula;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }


    public String getPeliculaNombre() {
        return tituloPelicula;
    }


    @Override
    public String toString() {
        return tituloPelicula + " | " + fecha + " | " + hora;
    }
}
