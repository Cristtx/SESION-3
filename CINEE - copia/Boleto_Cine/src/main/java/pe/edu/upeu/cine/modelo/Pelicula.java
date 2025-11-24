package pe.edu.upeu.cine.modelo;

public class Pelicula {
    private int idPelicula;
    private String titulo;
    private int duracion;
    private String clasificacion;

    public Pelicula() {}

    public Pelicula(int idPelicula, String titulo, int duracion, String clasificacion) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.duracion = duracion;
        this.clasificacion = clasificacion;
    }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getDuracion() { return duracion; }
    public void setDuracion(int duracion) { this.duracion = duracion; }

    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }

    @Override
    public String toString() {
        return titulo;
    }

}
