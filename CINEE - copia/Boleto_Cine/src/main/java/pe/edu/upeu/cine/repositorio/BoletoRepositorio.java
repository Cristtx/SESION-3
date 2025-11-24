package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.modelo.Boleto;
import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoletoRepositorio {

    public void insertar(String cliente, int idFuncion, int cantidad, double total) {
        String sql = "INSERT INTO boleto (id_funcion, cliente, cantidad, total) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFuncion);
            ps.setString(2, cliente);
            ps.setInt(3, cantidad);
            ps.setDouble(4, total);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Boleto> listar() {
        String sql = "SELECT b.id_boleto, b.cliente, b.cantidad, b.total, " +
                "       p.titulo || ' - ' || f.hora AS funcion " +
                "FROM boleto b " +
                "JOIN funcion f ON b.id_funcion = f.id_funcion " +
                "JOIN pelicula p ON f.id_pelicula = p.id_pelicula";

        List<Boleto> lista = new ArrayList<>();

        try (Connection con = ConexionSQLite.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Boleto(
                        rs.getInt("id_boleto"),
                        rs.getString("cliente"),
                        rs.getString("funcion"),
                        rs.getInt("cantidad"),
                        rs.getDouble("total")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
