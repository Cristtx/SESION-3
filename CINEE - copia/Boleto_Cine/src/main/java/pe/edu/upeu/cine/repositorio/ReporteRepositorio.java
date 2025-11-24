package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.util.ConexionSQLite;
import pe.edu.upeu.cine.modelo.ReportePelicula;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteRepositorio {

    public List<ReportePelicula> ventasPorPelicula() {
        List<ReportePelicula> lista = new ArrayList<>();

        String sql = """
                SELECT 
                    p.titulo AS pelicula,
                    SUM(v.cantidad) AS total_entradas,
                    SUM(v.total) AS total_recaudado
                FROM venta v
                INNER JOIN funcion f ON v.id_funcion = f.id_funcion
                INNER JOIN pelicula p ON f.id_pelicula = p.id_pelicula
                GROUP BY p.id_pelicula
                ORDER BY total_recaudado DESC
                """;

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(
                        new ReportePelicula(
                                rs.getString("pelicula"),
                                rs.getInt("total_entradas"),
                                rs.getDouble("total_recaudado")
                        )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}
