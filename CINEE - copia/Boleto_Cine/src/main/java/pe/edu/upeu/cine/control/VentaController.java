package pe.edu.upeu.cine.control;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pe.edu.upeu.cine.modelo.Asiento;
import pe.edu.upeu.cine.modelo.Funcion;
import pe.edu.upeu.cine.modelo.Venta;
import pe.edu.upeu.cine.reporte.ReporteService;
import pe.edu.upeu.cine.repositorio.AsientoFuncionRepositorio;
import pe.edu.upeu.cine.repositorio.FuncionRepositorio;
import pe.edu.upeu.cine.repositorio.VentaRepositorio;

import java.util.ArrayList;
import java.util.List;

public class VentaController {

    @FXML private ComboBox<Funcion> cmbFuncion;
    @FXML private TextField txtPrecio;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TextField txtTotal;
    @FXML private TextField txtAsientos;

    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colPelicula;
    @FXML private TableColumn<Venta, String> colFecha;
    @FXML private TableColumn<Venta, String> colHora;
    @FXML private TableColumn<Venta, Integer> colCantidad;
    @FXML private TableColumn<Venta, Double> colTotal;

    private final FuncionRepositorio funcionRepo = new FuncionRepositorio();
    private final VentaReposit orio ventaRepo = new VentaRepositorio();
    private final AsientoFuncionRepositorio asientoFuncionRepo = new AsientoFuncionRepositorio();

    private List<Asiento> asientosSeleccionados = new ArrayList<>();

    @FXML
    public void initialize() {
        cargarFunciones();
        configurarSpinner();
        configurarTabla();
        cargarVentas();
    }

    private void cargarFunciones() {
        cmbFuncion.setItems(FXCollections.observableArrayList(funcionRepo.listar()));
        cmbFuncion.setOnAction(e -> {
            Funcion f = cmbFuncion.getValue();
            if (f != null) {
                txtPrecio.setText(String.valueOf(f.getPrecio()));
                calcularTotal();
            }
        });
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> vf =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1);

        spnCantidad.setValueFactory(vf);
        spnCantidad.valueProperty().addListener((o, a, b) -> calcularTotal());
    }

    private void calcularTotal() {
        try {
            Funcion f = cmbFuncion.getValue();
            if (f != null) {
                int cantidad = spnCantidad.getValue();
                txtTotal.setText(String.valueOf(f.getPrecio() * cantidad));
            }
        } catch (Exception ignore) {}
    }

    @FXML
    private void abrirAsientos() {
        Funcion f = cmbFuncion.getValue();
        if (f == null) {
            mostrar("Seleccione una función.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AsientosView.fxml"));
            Parent root = loader.load();

            AsientosController controller = loader.getController();
            controller.setFuncion(f.getIdFuncion());

            controller.setListener(asientos -> {
                asientosSeleccionados = asientos;

                String txt = asientos.stream()
                        .map(Asiento::getCodigo)
                        .reduce((a, b) -> a + ", " + b).orElse("");

                txtAsientos.setText(txt);
            });

            Stage s = new Stage();
            s.setScene(new Scene(root));
            s.setTitle("Seleccionar asientos");
            s.initModality(Modality.APPLICATION_MODAL);
            s.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al abrir selección de asientos.");
        }
    }

    @FXML
    private void venderBoleto() {

        Funcion f = cmbFuncion.getValue();
        if (f == null) {
            mostrar("Seleccione una función.");
            return;
        }

        if (asientosSeleccionados.isEmpty()) {
            mostrar("Seleccione asientos.");
            return;
        }

        int cantidad = spnCantidad.getValue();
        if (asientosSeleccionados.size() != cantidad) {
            mostrar("La cantidad no coincide con los asientos seleccionados.");
            return;
        }

        try {
            double total = Double.parseDouble(txtTotal.getText());

            String textoAsientos = asientosSeleccionados.stream()
                    .map(Asiento::getCodigo)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            Venta v = new Venta();
            v.setIdFuncion(f.getIdFuncion());
            v.setCantidad(cantidad);
            v.setTotal(total);
            v.setAsientos(textoAsientos);

            ventaRepo.registrar(v);

            for (Asiento a : asientosSeleccionados) {
                asientoFuncionRepo.registrarAsientoOcupado(f.getIdFuncion(), a.getIdAsiento());
            }

            mostrar("Venta realizada correctamente.\nPuedes imprimir el ticket desde el botón.");

            limpiarFormulario();
            cargarVentas();

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al registrar venta.");
        }
    }

    @FXML
    private void generarReporte() {
        mostrar("El reporte general ya está implementado arriba.");
    }

    @FXML
    private void imprimirTicket() {

        try {
            Venta v = tablaVentas.getSelectionModel().getSelectedItem();
            if (v == null) {
                mostrar("Seleccione una venta de la tabla para imprimir su ticket.");
                return;
            }

            String ruta = "tickets/ticket_" + v.getIdVenta() + ".pdf";

            new ReporteService().generarTicketVentaPDF(
                    ruta,
                    v.getPelicula(),
                    v.getFecha(),
                    v.getHora(),
                    v.getAsientos(),
                    v.getCantidad(),
                    v.getTotal()
            );

            mostrar("Ticket generado: " + ruta);

        } catch (Exception e) {
            e.printStackTrace();
            mostrar("Error al imprimir ticket.");
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(v -> v.getValue().idVentaProperty().asObject());
        colPelicula.setCellValueFactory(v -> v.getValue().peliculaProperty());
        colFecha.setCellValueFactory(v -> v.getValue().fechaProperty());
        colHora.setCellValueFactory(v -> v.getValue().horaProperty());
        colCantidad.setCellValueFactory(v -> v.getValue().cantidadProperty().asObject());
        colTotal.setCellValueFactory(v -> v.getValue().totalProperty().asObject());
    }

    private void cargarVentas() {
        tablaVentas.setItems(FXCollections.observableArrayList(ventaRepo.listar()));
    }

    private void limpiarFormulario() {
        cmbFuncion.getSelectionModel().clearSelection();
        txtPrecio.clear();
        txtTotal.clear();
        txtAsientos.clear();
        spnCantidad.getValueFactory().setValue(1);
        asientosSeleccionados.clear();
    }

    private void mostrar(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
