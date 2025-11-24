package pe.edu.upeu.cine.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import pe.edu.upeu.cine.modelo.Boleto;
import pe.edu.upeu.cine.repositorio.BoletoRepositorio;
import pe.edu.upeu.cine.util.ConexionSQLite;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML private ComboBox<String> cmbFuncion;
    @FXML private TextField txtCliente;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtTotal;

    @FXML private TableView<Boleto> tablaBoletos;
    @FXML private TableColumn<Boleto, String> colCliente;
    @FXML private TableColumn<Boleto, String> colFuncion;
    @FXML private TableColumn<Boleto, Integer> colCantidad;
    @FXML private TableColumn<Boleto, Double> colTotal;

    private BoletoRepositorio boletoRepo = new BoletoRepositorio();


    private Map<String, Integer> mapaFunciones = new HashMap<>();
    private Map<String, Double> mapaPrecios = new HashMap<>();

    @FXML
    public void initialize() {

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1);
        spnCantidad.setValueFactory(valueFactory);

        cargarFunciones();
        configurarTabla();
        cargarBoletos();

        cmbFuncion.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                Double precio = mapaPrecios.get(newV);
                if (precio != null) {
                    txtPrecio.setText(String.valueOf(precio));
                    actualizarTotal();
                }
            }
        });

        spnCantidad.valueProperty().addListener((obs, oldV, newV) -> actualizarTotal());
    }

    private void cargarFunciones() {
        String sql = "SELECT f.id_funcion, p.titulo || ' - ' || f.fecha || ' ' || f.hora AS nombre, f.precio " +
                "FROM funcion f JOIN pelicula p ON f.id_pelicula = p.id_pelicula";

        try (Connection con = ConexionSQLite.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            ObservableList<String> funciones = FXCollections.observableArrayList();

            while (rs.next()) {
                int id = rs.getInt("id_funcion");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");

                mapaFunciones.put(nombre, id);
                mapaPrecios.put(nombre, precio);
                funciones.add(nombre);
            }

            cmbFuncion.setItems(funciones);
            if (!funciones.isEmpty()) {
                cmbFuncion.getSelectionModel().selectFirst();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configurarTabla() {
        colCliente.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCliente()));
        colFuncion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getFuncion()));
        colCantidad.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getCantidad()).asObject());
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotal()).asObject());
    }

    private void cargarBoletos() {
        ObservableList<Boleto> data = FXCollections.observableArrayList(boletoRepo.listar());
        tablaBoletos.setItems(data);
    }

    private void actualizarTotal() {
        try {
            double precio = Double.parseDouble(txtPrecio.getText().isEmpty() ? "0" : txtPrecio.getText());
            int cant = spnCantidad.getValue();
            txtTotal.setText(String.valueOf(precio * cant));
        } catch (Exception e) {
            txtTotal.setText("0");
        }
    }

    @FXML
    private void registrarBoleto() {
        String funcionTexto = cmbFuncion.getSelectionModel().getSelectedItem();
        String cliente = txtCliente.getText();

        if (funcionTexto == null || cliente.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Selecciona función y escribe el nombre del cliente.").show();
            return;
        }

        int idFuncion = mapaFunciones.get(funcionTexto);
        int cantidad = spnCantidad.getValue();
        double total = Double.parseDouble(txtTotal.getText().isEmpty() ? "0" : txtTotal.getText());


        boletoRepo.insertar(cliente, idFuncion, cantidad, total);
        cargarBoletos();
        txtCliente.clear();
    }
}

