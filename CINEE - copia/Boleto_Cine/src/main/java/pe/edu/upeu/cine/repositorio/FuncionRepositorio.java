package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.modelo.Funcion;
import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionRepositorio {

    public List<Funcion> listar() {
        List<Funcion> lista = new ArrayList<>();

        String sql = """
                SELECT f.id_funcion, f.id_pelicula, p.titulo,
                       f.fecha, f.hora, f.sala, f.precio
                FROM funcion f
                JOIN pelicula p ON f.id_pelicula = p.id_pelicula
                """;

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Funcion(
                        rs.getInt("id_funcion"),
                        rs.getInt("id_pelicula"),
                        rs.getString("titulo"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("sala"),
                        rs.getDouble("precio")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void guardar(Funcion f) {
        String sql = "INSERT INTO funcion(id_pelicula, fecha, hora, sala, precio) VALUES (?,?,?,?,?)";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, f.getIdPelicula());
            ps.setString(2, f.getFecha());
            ps.setString(3, f.getHora());
            ps.setString(4, f.getSala());
            ps.setDouble(5, f.getPrecio());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Funcion f) {
        String sql = """
                UPDATE funcion
                SET id_pelicula=?, fecha=?, hora=?, sala=?, precio=?
                WHERE id_funcion=?
                """;

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, f.getIdPelicula());
            ps.setString(2, f.getFecha());
            ps.setString(3, f.getHora());
            ps.setString(4, f.getSala());
            ps.setDouble(5, f.getPrecio());
            ps.setInt(6, f.getIdFuncion());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM funcion WHERE id_funcion=?";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
