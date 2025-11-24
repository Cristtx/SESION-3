package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.modelo.Venta;
import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaRepositorio {


    public void registrar(Venta v) {
        String sql = "INSERT INTO venta (id_funcion, cantidad, total, asientos) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, v.getIdFuncion());
            ps.setInt(2, v.getCantidad());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, v.getAsientos());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Venta> listar() {
        List<Venta> lista = new ArrayList<>();

        String sql = """
        SELECT 
            ve.id_venta,
            ve.cantidad,
            ve.total,
            ve.asientos,
            fu.fecha,
            fu.hora,
            pe.titulo AS pelicula
        FROM venta ve
        INNER JOIN funcion fu ON ve.id_funcion = fu.id_funcion
        INNER JOIN pelicula pe ON fu.id_pelicula = pe.id_pelicula
        ORDER BY ve.id_venta DESC
        """;


        try (Connection con = ConexionSQLite.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Venta v = new Venta();

                v.setIdVenta(rs.getInt("id_venta"));
                v.setCantidad(rs.getInt("cantidad"));
                v.setTotal(rs.getDouble("total"));

                v.setFecha(rs.getString("fecha"));
                v.setHora(rs.getString("hora"));
                v.setPelicula(rs.getString("pelicula"));
                v.setAsientos(rs.getString("asientos"));


                lista.add(v);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}
