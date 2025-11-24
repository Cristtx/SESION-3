package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.util.ConexionSQLite;
import java.sql.*;

public class UsuarioRepositorio {

    public boolean validarUsuario(String user, String pass) {
        String sql = "SELECT * FROM usuario WHERE username=? AND password=?";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
