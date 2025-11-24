package pe.edu.upeu.cine;

import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {
    public static void main(String[] args) {
        try (Connection con = ConexionSQLite.getConexion()) {
            System.out.println("Conexión OK a SQLite");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
