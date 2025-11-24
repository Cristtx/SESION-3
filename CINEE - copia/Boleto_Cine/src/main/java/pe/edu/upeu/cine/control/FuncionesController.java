package pe.edu.upeu.cine.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.cine.modelo.Funcion;
import pe.edu.upeu.cine.modelo.Pelicula;
import pe.edu.upeu.cine.repositorio.FuncionRepositorio;
import pe.edu.upeu.cine.repositorio.PeliculaRepositorio;

public class FuncionesController {

    @FXML private ComboBox<Pelicula> cmbPeliculas;
    @FXML private TextField txtFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtSala;
    @FXML private TextField txtPrecio;

    @FXML private TableView<Funcion> tablaFunciones;
    @FXML private TableColumn<Funcion, Integer> colId;
    @FXML private TableColumn<Funcion, String> colPelicula;
    @FXML private TableColumn<Funcion, String> colFecha;
    @FXML private TableColumn<Funcion, String> colHora;
    @FXML private TableColumn<Funcion, String> colSala;
    @FXML private TableColumn<Funcion, Double> colPrecio;

    private final FuncionRepositorio funcionRepo = new FuncionRepositorio();
    private final PeliculaRepositorio peliculaRepo = new PeliculaRepositorio();

    private Integer idSeleccionado = null;

    @FXML
    public void initialize() {

        cargarPeliculas();
        configurarTabla();
        cargarFunciones();

        tablaFunciones.setOnMouseClicked(event -> seleccionarFila());
    }


    private void cargarPeliculas() {
        ObservableList<Pelicula> lista = FXCollections.observableArrayList(
                peliculaRepo.listar()
        );
        cmbPeliculas.setItems(lista);
    }


    private void configurarTabla() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getIdFuncion()).asObject());
        colPelicula.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getTituloPelicula()));
        colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFecha()));
        colHora.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getHora()));
        colSala.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getSala()));
        colPrecio.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrecio()).asObject());
    }


    private void cargarFunciones() {
        ObservableList<Funcion> lista = FXCollections.observableArrayList(
                funcionRepo.listar()
        );
        tablaFunciones.setItems(lista);
    }


    @FXML
    private void guardarFuncion() {
        Pelicula peli = cmbPeliculas.getValue();
        if (peli == null) {
            mostrar("Seleccione una película");
            return;
        }

        try {
            Funcion f = new Funcion();
            f.setIdPelicula(peli.getIdPelicula());
            f.setFecha(txtFecha.getText());
            f.setHora(txtHora.getText());
            f.setSala(txtSala.getText());
            f.setPrecio(Double.parseDouble(txtPrecio.getText()));

            funcionRepo.guardar(f);

            mostrar("Función registrada");
            limpiarFormulario();
            cargarFunciones();
        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al guardar: revise los datos");
        }
    }


    private void seleccionarFila() {
        Funcion f = tablaFunciones.getSelectionModel().getSelectedItem();
        if (f == null) return;

        idSeleccionado = f.getIdFuncion();


        for (Pelicula p : cmbPeliculas.getItems()) {
            if (p.getIdPelicula() == f.getIdPelicula()) {
                cmbPeliculas.setValue(p);
                break;
            }
        }

        txtFecha.setText(f.getFecha());
        txtHora.setText(f.getHora());
        txtSala.setText(f.getSala());
        txtPrecio.setText(String.valueOf(f.getPrecio()));
    }


    @FXML
    private void actualizarFuncion() {
        if (idSeleccionado == null) {
            mostrar("Seleccione una función para actualizar");
            return;
        }

        Pelicula peli = cmbPeliculas.getValue();
        if (peli == null) {
            mostrar("Seleccione una película");
            return;
        }

        try {
            Funcion f = new Funcion();
            f.setIdFuncion(idSeleccionado);
            f.setIdPelicula(peli.getIdPelicula());
            f.setFecha(txtFecha.getText());
            f.setHora(txtHora.getText());
            f.setSala(txtSala.getText());
            f.setPrecio(Double.parseDouble(txtPrecio.getText()));

            funcionRepo.actualizar(f);

            mostrar("Función actualizada");
            limpiarFormulario();
            cargarFunciones();
        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al actualizar");
        }
    }


    @FXML
    private void eliminarFuncion() {
        if (idSeleccionado == null) {
            mostrar("Seleccione una función para eliminar");
            return;
        }

        funcionRepo.eliminar(idSeleccionado);

        mostrar("Función eliminada");
        limpiarFormulario();
        cargarFunciones();
    }


    @FXML
    private void limpiarFormulario() {
        cmbPeliculas.getSelectionModel().clearSelection();
        txtFecha.clear();
        txtHora.clear();
        txtSala.clear();
        txtPrecio.clear();
        idSeleccionado = null;
    }


    private void mostrar(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
