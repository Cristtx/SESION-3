package org.example.repository;

import org.example.entity.Usuario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {
    private static final String URL = "jdbc:sqlite:boletos_avion.db";

    public UsuarioRepository() {
        crearTablaSiNoExiste();
    }

    private void crearTablaSiNoExiste() {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                rol TEXT DEFAULT 'USER',
                fecha_registro TEXT NOT NULL
            )
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'usuarios' creada o ya existe");

            // Insertar usuario admin por defecto si no existe
            insertarUsuarioAdmin();
        } catch (SQLException e) {
            System.err.println("Error creando tabla usuarios: " + e.getMessage());
        }
    }

    private void insertarUsuarioAdmin() {
        String checkSql = "SELECT COUNT(*) FROM usuarios WHERE email = 'jimy@aureac.sac'";
        String insertSql = """
            INSERT INTO usuarios (nombre, email, password, rol, fecha_registro)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setString(1, "Jimy Admin");
                    pstmt.setString(2, "jimy@aureac.sac");
                    pstmt.setString(3, "123456");
                    pstmt.setString(4, "ADMIN");
                    pstmt.setString(5, LocalDateTime.now().toString());
                    pstmt.executeUpdate();
                    System.out.println("Usuario admin creado: jimy@aureac.sac / 123456");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error insertando usuario admin: " + e.getMessage());
        }
    }

    public Usuario save(Usuario usuario) {
        String sql = """
            INSERT INTO usuarios (nombre, email, password, rol, fecha_registro)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getEmail());
            pstmt.setString(3, usuario.getPassword());
            pstmt.setString(4, usuario.getRol());
            pstmt.setString(5, usuario.getFechaRegistro().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getLong(1));
                    }
                }
            }

            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Error guardando usuario: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando usuario: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public Optional<Usuario> findByEmailAndPassword(String email, String password) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error buscando usuario: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    public List<Usuario> findAll() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY fecha_registro DESC";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error obteniendo usuarios: " + e.getMessage(), e);
        }

        return usuarios;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando usuario: " + e.getMessage(), e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setRol(rs.getString("rol"));
        usuario.setFechaRegistro(LocalDateTime.parse(rs.getString("fecha_registro")));
        return usuario;
    }

    public boolean existeEmail(String email) {
        return findByEmail(email).isPresent();
    }
}