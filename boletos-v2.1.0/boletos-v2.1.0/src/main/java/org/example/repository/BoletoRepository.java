package org.example.repository;

import org.example.entity.Boleto;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoletoRepository {
    private static final String URL = "jdbc:sqlite:boletos_avion.db";

    public BoletoRepository() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS boletos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_vuelo TEXT NOT NULL,
                pasajero TEXT NOT NULL,
                origen TEXT NOT NULL,
                destino TEXT NOT NULL,
                fecha_vuelo TEXT NOT NULL,
                asiento TEXT NOT NULL,
                precio REAL NOT NULL,
                codigo_reserva TEXT UNIQUE,
                fecha_creacion TEXT NOT NULL
            )
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'boletos' creada o ya existe");
        } catch (SQLException e) {
            System.err.println("Error creando tabla: " + e.getMessage());
        }
    }

    public Boleto save(Boleto boleto) {
        String sql = """
            INSERT INTO boletos (numero_vuelo, pasajero, origen, destino, 
                               fecha_vuelo, asiento, precio, codigo_reserva, fecha_creacion)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, boleto.getNumeroVuelo());
            pstmt.setString(2, boleto.getPasajero());
            pstmt.setString(3, boleto.getOrigen());
            pstmt.setString(4, boleto.getDestino());
            pstmt.setString(5, boleto.getFechaVuelo().toString());
            pstmt.setString(6, boleto.getAsiento());
            pstmt.setDouble(7, boleto.getPrecio());
            pstmt.setString(8, boleto.getCodigoReserva());
            pstmt.setString(9, boleto.getFechaCreacion().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        boleto.setId(rs.getLong(1));
                    }
                }
            }

            return boleto;
        } catch (SQLException e) {
            throw new RuntimeException("Error guardando boleto: " + e.getMessage(), e);
        }
    }

    public Boleto update(Boleto boleto) {
        String sql = """
            UPDATE boletos 
            SET numero_vuelo = ?, pasajero = ?, origen = ?, destino = ?, 
                fecha_vuelo = ?, asiento = ?, precio = ?
            WHERE id = ?
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, boleto.getNumeroVuelo());
            pstmt.setString(2, boleto.getPasajero());
            pstmt.setString(3, boleto.getOrigen());
            pstmt.setString(4, boleto.getDestino());
            pstmt.setString(5, boleto.getFechaVuelo().toString());
            pstmt.setString(6, boleto.getAsiento());
            pstmt.setDouble(7, boleto.getPrecio());
            pstmt.setLong(8, boleto.getId());

            pstmt.executeUpdate();
            return boleto;
        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando boleto: " + e.getMessage(), e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM boletos WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando boleto: " + e.getMessage(), e);
        }
    }

    public List<Boleto> findAll() {
        List<Boleto> boletos = new ArrayList<>();
        String sql = "SELECT * FROM boletos ORDER BY fecha_creacion DESC";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                boletos.add(mapearBoleto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo boletos: " + e.getMessage(), e);
        }

        return boletos;
    }

    public Optional<Boleto> findById(Long id) {
        String sql = "SELECT * FROM boletos WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearBoleto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando boleto: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    private Boleto mapearBoleto(ResultSet rs) throws SQLException {
        Boleto boleto = new Boleto();
        boleto.setId(rs.getLong("id"));
        boleto.setNumeroVuelo(rs.getString("numero_vuelo"));
        boleto.setPasajero(rs.getString("pasajero"));
        boleto.setOrigen(rs.getString("origen"));
        boleto.setDestino(rs.getString("destino"));
        boleto.setFechaVuelo(LocalDateTime.parse(rs.getString("fecha_vuelo")));
        boleto.setAsiento(rs.getString("asiento"));
        boleto.setPrecio(rs.getDouble("precio"));
        boleto.setCodigoReserva(rs.getString("codigo_reserva"));
        boleto.setFechaCreacion(LocalDateTime.parse(rs.getString("fecha_creacion")));

        return boleto;
    }
    public Optional<Boleto> findByCodigoReserva(String codigoReserva) {
        String sql = "SELECT * FROM boletos WHERE codigo_reserva = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoReserva);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearBoleto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando boleto por código de reserva: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

}