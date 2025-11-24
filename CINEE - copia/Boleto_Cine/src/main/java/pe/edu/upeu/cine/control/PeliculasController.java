package pe.edu.upeu.cine.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.cine.modelo.Pelicula;
import pe.edu.upeu.cine.repositorio.PeliculaRepositorio;

public class PeliculasController {

    @FXML private TextField txtTitulo;
    @FXML private TextField txtDuracion;
    @FXML private TextField txtClasificacion;

    @FXML private TableView<Pelicula> tablaPeliculas;
    @FXML private TableColumn<Pelicula, Integer> colId;
    @FXML private TableColumn<Pelicula, String> colTitulo;
    @FXML private TableColumn<Pelicula, Integer> colDuracion;
    @FXML private TableColumn<Pelicula, String> colClasificacion;

    private PeliculaRepositorio repo = new PeliculaRepositorio();
    private ObservableList<Pelicula> listaPeliculas;

    private Pelicula peliculaSeleccionada; // <-- ESTA ES LA CLAVE

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdPelicula()).asObject());
        colTitulo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitulo()));
        colDuracion.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getDuracion()).asObject());
        colClasificacion.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getClasificacion()));

        cargarPeliculas();
        detectarSeleccion();
    }

    private void cargarPeliculas() {
        listaPeliculas = FXCollections.observableArrayList(repo.listar());
        tablaPeliculas.setItems(listaPeliculas);
    }

    private void detectarSeleccion() {
        tablaPeliculas.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                peliculaSeleccionada = newV;
                txtTitulo.setText(newV.getTitulo());
                txtDuracion.setText(String.valueOf(newV.getDuracion()));
                txtClasificacion.setText(newV.getClasificacion());
            }
        });
    }

    @FXML
    public void guardarPelicula() {
        Pelicula p = new Pelicula();
        p.setTitulo(txtTitulo.getText());
        p.setDuracion(Integer.parseInt(txtDuracion.getText()));
        p.setClasificacion(txtClasificacion.getText());

        repo.guardar(p);
        cargarPeliculas();
        limpiarFormulario();
    }

    @FXML
    public void actualizarPelicula() {
        if (peliculaSeleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una película de la tabla").show();
            return;
        }

        peliculaSeleccionada.setTitulo(txtTitulo.getText());
        peliculaSeleccionada.setDuracion(Integer.parseInt(txtDuracion.getText()));
        peliculaSeleccionada.setClasificacion(txtClasificacion.getText());

        repo.actualizar(peliculaSeleccionada);
        cargarPeliculas();
        limpiarFormulario();
    }

    @FXML
    public void eliminarPelicula() {
        if (peliculaSeleccionada == null) {
            new Alert(Alert.AlertType.WARNING, "Seleccione una película para eliminar").show();
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "¿Eliminar la película seleccionada?",
                ButtonType.YES, ButtonType.NO);
        alerta.showAndWait();

        if (alerta.getResult() == ButtonType.YES) {
            repo.eliminar(peliculaSeleccionada.getIdPelicula());
            cargarPeliculas();
            limpiarFormulario();
        }
    }

    @FXML
    public void limpiarFormulario() {
        txtTitulo.clear();
        txtDuracion.clear();
        txtClasificacion.clear();
        tablaPeliculas.getSelectionModel().clearSelection();
        peliculaSeleccionada = null;
    }
}
