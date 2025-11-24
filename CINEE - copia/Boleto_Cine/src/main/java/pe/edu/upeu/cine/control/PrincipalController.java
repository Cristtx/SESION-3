package pe.edu.upeu.cine.control;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import pe.edu.upeu.cine.MainApp;

public class PrincipalController {

    @FXML private StackPane panelContenido;


    private void cargarVista(String fxml) {
        try {
            Node nodo = FXMLLoader.load(getClass().getResource("/fxml/" + fxml));
            panelContenido.getChildren().setAll(nodo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirPeliculas() {
        cargarVista("PeliculasView.fxml");
    }

    @FXML
    public void abrirFunciones() {
        cargarVista("FuncionesView.fxml");
    }

    @FXML
    public void abrirVentas() {
        cargarVista("VentaView.fxml");
    }


    @FXML
    public void abrirReportes() {
        cargarVista("ReportesView.fxml");
    }

    @FXML
    public void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Login");
            stage.show();

            // Cerrar ventana actual
            Stage actual = (Stage) panelContenido.getScene().getWindow();
            actual.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
