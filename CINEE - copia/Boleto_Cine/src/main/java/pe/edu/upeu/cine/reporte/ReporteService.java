package pe.edu.upeu.cine.reporte;

import net.sf.jasperreports.engine.*;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class ReporteService {

    private final String DB_URL = "jdbc:sqlite:data/cine.db"; // Ruta correcta a SQLite
    private final String REPORT_DIR = "src/main/resources/reportes/";


    public void generarReporteVentasPDF(String rutaSalida) {

        try {

            InputStream archivo = getClass().getResourceAsStream("/reportes/reporte_ventas.jrxml");
            if (archivo == null) {
                throw new RuntimeException("No se encontró reporte_ventas.jrxml");
            }


            JasperReport jasperReport = JasperCompileManager.compileReport(archivo);


            Map<String, Object> parametros = new HashMap<>();
            parametros.put("REPORT_DIR", REPORT_DIR);


            Connection conn = DriverManager.getConnection(DB_URL);


            JasperPrint print = JasperFillManager.fillReport(jasperReport, parametros, conn);


            JasperExportManager.exportReportToPdfFile(print, rutaSalida);

            System.out.println("Reporte generado: " + rutaSalida);

            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar reporte: " + e.getMessage());
        }
    }


    public void generarTicketVentaPDF(String rutaSalida,
                                      String pelicula,
                                      String fecha,
                                      String hora,
                                      String asientos,
                                      int cantidad,
                                      double total) {

        try {
            InputStream archivo = getClass().getResourceAsStream("/reportes/ticket_venta.jrxml");
            if (archivo == null) {
                throw new RuntimeException("No se encontró ticket_venta.jrxml");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(archivo);

            Map<String, Object> params = new HashMap<>();
            params.put("P_PELICULA", pelicula);
            params.put("P_FECHA", fecha);
            params.put("P_HORA", hora);
            params.put("P_ASIENTOS", asientos);
            params.put("P_CANTIDAD", cantidad);
            params.put("P_TOTAL", total);
            params.put("REPORT_DIR", REPORT_DIR);

            JasperPrint print = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

            JasperExportManager.exportReportToPdfFile(print, rutaSalida);

            System.out.println("🎟 Ticket generado: " + rutaSalida);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar ticket: " + e.getMessage());
        }
    }
}
