package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AsientoFuncionRepositorio {


    public void registrarAsientoOcupado(int idFuncion, int idAsiento) {
        String sql = """
                INSERT INTO asiento_funcion (id_funcion, id_asiento, estado)
                VALUES (?, ?, 'O')
                """;

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFuncion);
            ps.setInt(2, idAsiento);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public List<Integer> listarAsientosOcupados(int idFuncion) {

        List<Integer> ocupados = new ArrayList<>();

        String sql = """
                SELECT id_asiento 
                FROM asiento_funcion
                WHERE id_funcion = ? AND estado = 'O'
                """;

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idFuncion);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ocupados.add(rs.getInt("id_asiento"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ocupados;
    }
}
