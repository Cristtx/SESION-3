package pe.edu.upeu.cine.control;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.upeu.cine.modelo.ReportePelicula;
import pe.edu.upeu.cine.repositorio.ReporteRepositorio;

public class ReportesController {

    @FXML private TableView<ReportePelicula> tablaReporte;
    @FXML private TableColumn<ReportePelicula, String> colPelicula;
    @FXML private TableColumn<ReportePelicula, Integer> colEntradas;
    @FXML private TableColumn<ReportePelicula, Double> colRecaudado;

    private final ReporteRepositorio repo = new ReporteRepositorio();

    @FXML
    public void initialize() {
        colPelicula.setCellValueFactory(new PropertyValueFactory<>("pelicula"));
        colEntradas.setCellValueFactory(new PropertyValueFactory<>("totalEntradas"));
        colRecaudado.setCellValueFactory(new PropertyValueFactory<>("totalRecaudado"));

        tablaReporte.setItems(FXCollections.observableArrayList(repo.ventasPorPelicula()));
    }
}
