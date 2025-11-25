package org.example.repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class RutaRepository {
    private static final String URL = "jdbc:sqlite:boletos_avion.db";

    public RutaRepository() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS rutas_precios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                origen TEXT NOT NULL,
                destino TEXT NOT NULL,
                precio REAL NOT NULL,
                activo BOOLEAN DEFAULT 1,
                UNIQUE(origen, destino)
            )
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'rutas_precios' creada o ya existe");
            insertarRutasPorDefecto();
        } catch (SQLException e) {
            System.err.println("Error creando tabla rutas_precios: " + e.getMessage());
        }
    }

    private void insertarRutasPorDefecto() {
        String[][] rutas = {
                // Desde Lima
                {"Lima", "Bogotá", "350.00"},
                {"Lima", "Buenos Aires", "550.00"},
                {"Lima", "Santiago", "280.00"},
                {"Lima", "México DF", "480.00"},
                {"Lima", "Madrid", "850.00"},
                {"Lima", "Roma", "920.00"},
                {"Lima", "París", "890.00"},
                {"Lima", "Londres", "950.00"},
                {"Lima", "Nueva York", "720.00"},
                {"Lima", "Miami", "680.00"},
                {"Lima", "Los Ángeles", "820.00"},
                {"Lima", "Tokio", "1500.00"},
                {"Lima", "Sídney", "1800.00"},
                {"Lima", "Dubái", "1300.00"},
                {"Lima", "Brasil", "550.00"},
                {"Lima", "Chile", "300.00"},
                {"Lima", "Argentina", "500.00"},

                // Desde Brasil
                {"Brasil", "Lima", "600.00"},
                {"Brasil", "Chile", "750.00"},
                {"Brasil", "Miami", "900.00"},
                {"Brasil", "Buenos Aires", "350.00"},
                {"Brasil", "Madrid", "1000.00"},
                {"Brasil", "Bogotá", "500.00"},
                {"Brasil", "Nueva York", "1200.00"},
                {"Brasil", "París", "1100.00"},

                // Desde Argentina
                {"Buenos Aires", "Lima", "500.00"},
                {"Buenos Aires", "Chile", "400.00"},
                {"Buenos Aires", "Brasil", "350.00"},
                {"Buenos Aires", "Miami", "850.00"},
                {"Buenos Aires", "Madrid", "950.00"},
                {"Buenos Aires", "Bogotá", "550.00"},
                {"Buenos Aires", "Roma", "1200.00"},

                // Desde Bogotá
                {"Bogotá", "Lima", "350.00"},
                {"Bogotá", "Buenos Aires", "600.00"},
                {"Bogotá", "Miami", "500.00"},
                {"Bogotá", "Madrid", "800.00"},
                {"Bogotá", "México DF", "400.00"},

                // Desde México DF
                {"México DF", "Lima", "480.00"},
                {"México DF", "Bogotá", "400.00"},
                {"México DF", "Miami", "300.00"},
                {"México DF", "Madrid", "750.00"},
                {"México DF", "Los Ángeles", "350.00"}
        };

        String sql = "INSERT OR IGNORE INTO rutas_precios (origen, destino, precio) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String[] ruta : rutas) {
                pstmt.setString(1, ruta[0]);
                pstmt.setString(2, ruta[1]);
                pstmt.setDouble(3, Double.parseDouble(ruta[2]));
                pstmt.executeUpdate();
            }
            System.out.println("Rutas por defecto insertadas");
        } catch (SQLException e) {
            System.err.println("Error insertando rutas por defecto: " + e.getMessage());
        }
    }

    public Map<String, Double> obtenerRutasDesde(String origen) {
        Map<String, Double> rutas = new HashMap<>();
        String sql = "SELECT destino, precio FROM rutas_precios WHERE origen = ? AND activo = 1";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, origen);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                rutas.put(rs.getString("destino"), rs.getDouble("precio"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo rutas: " + e.getMessage(), e);
        }

        return rutas;
    }

    public boolean actualizarPrecio(String origen, String destino, double nuevoPrecio) {
        String sql = "UPDATE rutas_precios SET precio = ? WHERE origen = ? AND destino = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, nuevoPrecio);
            pstmt.setString(2, origen);
            pstmt.setString(3, destino);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando precio: " + e.getMessage(), e);
        }
    }

    public boolean agregarRuta(String origen, String destino, double precio) {
        String sql = "INSERT INTO rutas_precios (origen, destino, precio) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, origen);
            pstmt.setString(2, destino);
            pstmt.setDouble(3, precio);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error agregando ruta: " + e.getMessage(), e);
        }
    }

    public boolean eliminarRuta(String origen, String destino) {
        String sql = "UPDATE rutas_precios SET activo = 0 WHERE origen = ? AND destino = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, origen);
            pstmt.setString(2, destino);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando ruta: " + e.getMessage(), e);
        }
    }

    public Map<String, Map<String, Double>> obtenerTodasLasRutas() {
        Map<String, Map<String, Double>> todasLasRutas = new HashMap<>();
        String sql = "SELECT origen, destino, precio FROM rutas_precios WHERE activo = 1 ORDER BY origen, destino";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String origen = rs.getString("origen");
                String destino = rs.getString("destino");
                double precio = rs.getDouble("precio");

                todasLasRutas.computeIfAbsent(origen, k -> new HashMap<>()).put(destino, precio);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo todas las rutas: " + e.getMessage(), e);
        }

        return todasLasRutas;
    }
}
