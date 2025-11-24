package pe.edu.upeu.cine.repositorio;

import pe.edu.upeu.cine.modelo.Asiento;
import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsientoRepositorio {


    public List<Asiento> listar() {
        List<Asiento> lista = new ArrayList<>();
        String sql = "SELECT * FROM asiento ORDER BY id_asiento";

        try (Connection con = ConexionSQLite.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Asiento a = new Asiento();
                a.setIdAsiento(rs.getInt("id_asiento"));
                a.setCodigo(rs.getString("codigo"));
                lista.add(a);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }


    public Asiento obtenerPorId(int id) {
        String sql = "SELECT * FROM asiento WHERE id_asiento = ?";
        Asiento a = null;

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                a = new Asiento();
                a.setIdAsiento(rs.getInt("id_asiento"));
                a.setCodigo(rs.getString("codigo"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return a;
    }


    public Asiento obtenerPorCodigo(String codigo) {
        String sql = "SELECT * FROM asiento WHERE codigo = ?";
        Asiento a = null;

        try (Connection con = ConexionSQLite.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                a = new Asiento();
                a.setIdAsiento(rs.getInt("id_asiento"));
                a.setCodigo(rs.getString("codigo"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return a;
    }
}

