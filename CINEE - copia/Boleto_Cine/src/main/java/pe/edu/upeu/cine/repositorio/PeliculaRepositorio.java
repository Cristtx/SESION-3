package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.modelo.Pelicula;
import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeliculaRepositorio {

    public List<Pelicula> listar() {
        List<Pelicula> lista = new ArrayList<>();

        String sql = "SELECT id_pelicula, titulo, duracion, clasificacion FROM pelicula";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Pelicula(
                        rs.getInt("id_pelicula"),
                        rs.getString("titulo"),
                        rs.getInt("duracion"),
                        rs.getString("clasificacion")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void guardar(Pelicula p) {
        String sql = "INSERT INTO pelicula(titulo, duracion, clasificacion) VALUES(?,?,?)";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getTitulo());
            ps.setInt(2, p.getDuracion());
            ps.setString(3, p.getClasificacion());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizar(Pelicula p) {
        String sql = "UPDATE pelicula SET titulo=?, duracion=?, clasificacion=? WHERE id_pelicula=?";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getTitulo());
            ps.setInt(2, p.getDuracion());
            ps.setString(3, p.getClasificacion());
            ps.setInt(4, p.getIdPelicula());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM pelicula WHERE id_pelicula=?";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
