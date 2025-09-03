package modelo;

/**
 * Clase Persona con encapsulamiento
 */
public class Persona {

    private String nombre;
    private int edad;
    private double talla;

    public Persona() {
    }

    // Métodos de acceso
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }
    public int getEdad() {
        return edad;
    }

    public void setTalla(double talla) {
        this.talla = talla;
    }
    public double getTalla() {
        return talla;
    }
}
