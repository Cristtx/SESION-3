package pe.edu.upeu.cine.control;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pe.edu.upeu.cine.modelo.Asiento;
import pe.edu.upeu.cine.repositorio.AsientoFuncionRepositorio;
import pe.edu.upeu.cine.repositorio.AsientoRepositorio;

import java.util.ArrayList;
import java.util.List;

public class AsientosController {

    @FXML
    private GridPane gridAsientos;

    private final AsientoRepositorio asientoRepo = new AsientoRepositorio();
    private final AsientoFuncionRepositorio asientoFuncionRepo = new AsientoFuncionRepositorio();

    private int idFuncion;
    private List<Asiento> seleccionados = new ArrayList<>();

    private AsientosListener listener;

    public interface AsientosListener {
        void onAsientosSeleccionados(List<Asiento> lista);
    }

    public void setListener(AsientosListener listener) {
        this.listener = listener;
    }

    public void setFuncion(int idFuncion) {
        this.idFuncion = idFuncion;
        cargarAsientos();
    }

    private void cargarAsientos() {

        List<Integer> ocupados = asientoFuncionRepo.listarAsientosOcupados(idFuncion);
        List<Asiento> todos = asientoRepo.listar();

        gridAsientos.getChildren().clear();

        int col = 0;
        int row = 0;

        for (Asiento a : todos) {

            Button b = new Button(a.getCodigo());
            b.setPrefSize(50, 40);


            if (ocupados.contains(a.getIdAsiento())) {
                b.setStyle("-fx-background-color: #FF4C4C; -fx-text-fill: white;");
                b.setDisable(true);
            }

            b.setOnAction(e -> seleccionarAsiento(a, b));

            gridAsientos.add(b, col, row);

            col++;
            if (col == 10) {
                col = 0;
                row++;
            }
        }
    }

    private void seleccionarAsiento(Asiento a, Button b) {


        if (seleccionados.contains(a)) {
            seleccionados.remove(a);
            b.setStyle("");
            return;
        }

        // Máximo 3 asientos
        if (seleccionados.size() >= 3) {
            alerta("Solo puedes seleccionar hasta 3 asientos.");
            return;
        }

        seleccionados.add(a);
        b.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // verde seleccionado
    }

    @FXML
    private void confirmar() {
        if (seleccionados.isEmpty()) {
            alerta("Seleccione al menos un asiento.");
            return;
        }


        if (listener != null) {
            listener.onAsientosSeleccionados(seleccionados);
        }

        cerrarVentana();
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) gridAsientos.getScene().getWindow();
        stage.close();
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
