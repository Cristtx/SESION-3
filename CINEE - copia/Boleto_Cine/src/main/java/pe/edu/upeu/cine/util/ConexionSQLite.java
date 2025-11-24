package pe.edu.upeu.cine.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionSQLite {

    private static final String URL = "jdbc:sqlite:data/cine.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC"); // cargar driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static Connection getConexion() throws SQLException {
        File f = new File("data/cine.db");
        System.out.println("EXISTE?: " + f.exists() + "  → " + f.getAbsolutePath());
        return DriverManager.getConnection(URL);
    }





}

