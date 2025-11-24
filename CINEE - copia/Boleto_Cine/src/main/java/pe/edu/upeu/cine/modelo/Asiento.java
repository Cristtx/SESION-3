package pe.edu.upeu.cine.modelo;

public class Asiento {

    private int idAsiento;
    private String codigo;  // Ej. "A1", "C5", etc.

    public Asiento() {}

    public Asiento(int idAsiento, String codigo) {
        this.idAsiento = idAsiento;
        this.codigo = codigo;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }
}
