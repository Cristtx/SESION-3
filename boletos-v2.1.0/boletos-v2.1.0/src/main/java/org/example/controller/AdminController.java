package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.entity.Usuario;
import org.example.entity.Boleto;
import org.example.service.RutaService;
import org.example.service.UsuarioService;
import org.example.service.BoletoService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class AdminController {

    // Componentes existentes
    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Long> colIdUsuario;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colEmail;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colFechaRegistro;

    @FXML private TableView<Ruta> tablaRutas;
    @FXML private TableColumn<Ruta, String> colOrigen;
    @FXML private TableColumn<Ruta, String> colDestino;
    @FXML private TableColumn<Ruta, Double> colPrecio;

    @FXML private ComboBox<String> comboOrigenAdmin;
    @FXML private ComboBox<String> comboDestinoAdmin;
    @FXML private TextField txtNuevoPrecio;

    @FXML private ComboBox<String> comboNuevoOrigen;
    @FXML private ComboBox<String> comboNuevoDestino;
    @FXML private TextField txtPrecioNuevaRuta;

    @FXML private Label lblAdminInfo;
    @FXML private Button btnCerrarSesion;

    // NUEVOS COMPONENTES PARA REPORTES
    @FXML private BarChart<String, Number> chartViajesPorMes;
    @FXML private PieChart chartDestinosPopulares;
    @FXML private BarChart<String, Number> chartUsuariosTop;
    @FXML private ComboBox<Integer> comboAnioReporte;
    @FXML private CategoryAxis xAxisMeses;
    @FXML private NumberAxis yAxisViajes;

    @FXML private Label lblTotalViajes;
    @FXML private Label lblIngresoTotal;
    @FXML private Label lblMesMasViajes;
    @FXML private Label lblDestinoPopular;

    private UsuarioService usuarioService;
    private RutaService rutaService;
    private BoletoService boletoService; // NUEVO servicio
    private ObservableList<Usuario> usuariosObservable;
    private ObservableList<Ruta> rutasObservable;

    public static class Ruta {
        private String origen;
        private String destino;
        private double precio;

        public Ruta(String origen, String destino, double precio) {
            this.origen = origen;
            this.destino = destino;
            this.precio = precio;
        }

        public String getOrigen() { return origen; }
        public String getDestino() { return destino; }
        public double getPrecio() { return precio; }
    }

    public void initialize() {
        usuarioService = new UsuarioService();
        rutaService = new RutaService();
        boletoService = new BoletoService(); // NUEVO

        configurarTablaUsuarios();
        configurarTablaRutas();
        configurarReportes(); // NUEVO
        cargarDatos();
    }

    // ========== MÉTODOS EXISTENTES ==========
    @FXML
    private void cerrarSesion() {
        try {
            Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
            stageActual.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Fly AUREAC - Sistema de Boletos");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

        } catch (Exception e) {
            mostrarError("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarTablaUsuarios() {
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));

        usuariosObservable = FXCollections.observableArrayList();
        tablaUsuarios.setItems(usuariosObservable);
    }

    private void configurarTablaRutas() {
        colOrigen.setCellValueFactory(new PropertyValueFactory<>("origen"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destino"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        rutasObservable = FXCollections.observableArrayList();
        tablaRutas.setItems(rutasObservable);

        comboOrigenAdmin.getItems().addAll(rutaService.getCiudadesDisponibles());
        comboDestinoAdmin.getItems().addAll(rutaService.getCiudadesDisponibles());
        comboNuevoOrigen.getItems().addAll(rutaService.getCiudadesDisponibles());
        comboNuevoDestino.getItems().addAll(rutaService.getCiudadesDisponibles());
    }

    private void cargarDatos() {
        usuariosObservable.setAll(usuarioService.obtenerTodosLosUsuarios());

        rutasObservable.clear();
        Map<String, Map<String, Double>> todasLasRutas = rutaService.obtenerTodasLasRutas();

        for (Map.Entry<String, Map<String, Double>> entryOrigen : todasLasRutas.entrySet()) {
            String origen = entryOrigen.getKey();
            Map<String, Double> destinos = entryOrigen.getValue();

            for (Map.Entry<String, Double> entryDestino : destinos.entrySet()) {
                rutasObservable.add(new Ruta(origen, entryDestino.getKey(), entryDestino.getValue()));
            }
        }
    }

    @FXML
    private void actualizarPrecio() {
        String origen = comboOrigenAdmin.getValue();
        String destino = comboDestinoAdmin.getValue();
        String precioStr = txtNuevoPrecio.getText();

        if (origen == null || destino == null || precioStr.isEmpty()) {
            mostrarError("Seleccione origen, destino y ingrese un precio");
            return;
        }

        try {
            double nuevoPrecio = Double.parseDouble(precioStr);
            if (nuevoPrecio <= 0) {
                mostrarError("El precio debe ser mayor a 0");
                return;
            }

            boolean exito = rutaService.actualizarPrecioRuta(origen, destino, nuevoPrecio);
            if (exito) {
                mostrarInfo("Precio actualizado exitosamente");
                cargarDatos();
                limpiarCamposPrecio();
            } else {
                mostrarError("No se pudo actualizar el precio. Verifique la ruta.");
            }
        } catch (NumberFormatException e) {
            mostrarError("Ingrese un precio válido");
        }
    }

    @FXML
    private void agregarRuta() {
        String origen = comboNuevoOrigen.getValue();
        String destino = comboNuevoDestino.getValue();
        String precioStr = txtPrecioNuevaRuta.getText();

        if (origen == null || destino == null || precioStr.isEmpty()) {
            mostrarError("Complete todos los campos para agregar una ruta");
            return;
        }

        if (origen.equals(destino)) {
            mostrarError("El origen y destino no pueden ser iguales");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                mostrarError("El precio debe ser mayor a 0");
                return;
            }

            boolean exito = rutaService.agregarRuta(origen, destino, precio);
            if (exito) {
                mostrarInfo("Ruta agregada exitosamente");
                cargarDatos();
                limpiarCamposNuevaRuta();
            } else {
                mostrarError("No se pudo agregar la ruta. Puede que ya exista.");
            }
        } catch (NumberFormatException e) {
            mostrarError("Ingrese un precio válido");
        }
    }

    @FXML
    private void eliminarRuta() {
        Ruta rutaSeleccionada = tablaRutas.getSelectionModel().getSelectedItem();
        if (rutaSeleccionada != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Está seguro de eliminar esta ruta?");
            alert.setContentText(rutaSeleccionada.getOrigen() + " → " + rutaSeleccionada.getDestino());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean exito = rutaService.eliminarRuta(rutaSeleccionada.getOrigen(), rutaSeleccionada.getDestino());
                    if (exito) {
                        mostrarInfo("Ruta eliminada exitosamente");
                        cargarDatos();
                    } else {
                        mostrarError("No se pudo eliminar la ruta");
                    }
                }
            });
        } else {
            mostrarError("Seleccione una ruta para eliminar");
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            if ("ADMIN".equals(usuarioSeleccionado.getRol())) {
                mostrarError("No se pueden eliminar cuentas de administrador");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Está seguro de eliminar este usuario?");
            alert.setContentText(usuarioSeleccionado.getNombre() + " - " + usuarioSeleccionado.getEmail());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean exito = usuarioService.eliminarUsuario(usuarioSeleccionado.getId());
                    if (exito) {
                        mostrarInfo("Usuario eliminado exitosamente");
                        cargarDatos();
                    } else {
                        mostrarError("No se pudo eliminar el usuario");
                    }
                }
            });
        } else {
            mostrarError("Seleccione un usuario para eliminar");
        }
    }

    @FXML
    private void refrescarDatos() {
        cargarDatos();
        generarReporteMensual(); // NUEVO: actualizar reportes también
        mostrarInfo("Datos actualizados");
    }

    private void limpiarCamposPrecio() {
        comboOrigenAdmin.setValue(null);
        comboDestinoAdmin.setValue(null);
        txtNuevoPrecio.clear();
    }

    private void limpiarCamposNuevaRuta() {
        comboNuevoOrigen.setValue(null);
        comboNuevoDestino.setValue(null);
        txtPrecioNuevaRuta.clear();
    }

    private void mostrarError(String mensaje) {
        lblAdminInfo.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        lblAdminInfo.setText("❌ " + mensaje);
    }

    private void mostrarInfo(String mensaje) {
        lblAdminInfo.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        lblAdminInfo.setText("✅ " + mensaje);
    }

    // ========== NUEVOS MÉTODOS PARA REPORTES ==========

    private void configurarReportes() {
        System.out.println("📊 Configurando sistema de reportes...");

        // Configurar años disponibles (últimos 3 años)
        int añoActual = LocalDateTime.now().getYear();
        comboAnioReporte.getItems().addAll(añoActual, añoActual - 1, añoActual - 2);
        comboAnioReporte.setValue(añoActual);

        // Configurar ejes del gráfico
        xAxisMeses.setLabel("Meses");
        yAxisViajes.setLabel("Cantidad de Viajes");

        // Generar reporte inicial
        generarReporteMensual();
    }

    @FXML
    private void generarReporteMensual() {
        try {
            Integer añoSeleccionado = comboAnioReporte.getValue();
            if (añoSeleccionado == null) {
                mostrarError("Seleccione un año para generar el reporte");
                return;
            }

            System.out.println("📈 Generando reporte para el año: " + añoSeleccionado);

            // Obtener todos los boletos
            List<Boleto> todosLosBoletos = boletoService.obtenerTodosLosBoletos();

            // Generar gráficos
            generarGraficoViajesPorMes(todosLosBoletos, añoSeleccionado);
            generarGraficoDestinosPopulares(todosLosBoletos);
            generarGraficoUsuariosTop(todosLosBoletos);
            actualizarEstadisticasResumen(todosLosBoletos, añoSeleccionado);

            mostrarInfo("Reporte generado exitosamente para " + añoSeleccionado);

        } catch (Exception e) {
            mostrarError("Error generando reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generarGraficoViajesPorMes(List<Boleto> boletos, int año) {
        // Limpiar gráfico anterior
        chartViajesPorMes.getData().clear();

        // Contar viajes por mes
        Map<Integer, Long> viajesPorMes = boletos.stream()
                .filter(b -> b.getFechaVuelo().getYear() == año)
                .collect(Collectors.groupingBy(
                        b -> b.getFechaVuelo().getMonthValue(),
                        Collectors.counting()
                ));

        // Crear serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Viajes " + año);

        // Nombres de los meses
        String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};

        // Agregar datos al gráfico
        for (int mes = 1; mes <= 12; mes++) {
            long cantidad = viajesPorMes.getOrDefault(mes, 0L);
            series.getData().add(new XYChart.Data<>(nombresMeses[mes-1], cantidad));
        }

        chartViajesPorMes.getData().add(series);
        System.out.println("✅ Gráfico de viajes por mes generado");
    }

    private void generarGraficoDestinosPopulares(List<Boleto> boletos) {
        chartDestinosPopulares.getData().clear();

        // Contar viajes por destino
        Map<String, Long> viajesPorDestino = boletos.stream()
                .collect(Collectors.groupingBy(
                        Boleto::getDestino,
                        Collectors.counting()
                ));

        // Crear datos para el gráfico de torta
        List<PieChart.Data> pieData = viajesPorDestino.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(8) // Top 8 destinos
                .map(entry -> new PieChart.Data(
                        entry.getKey() + " (" + entry.getValue() + ")",
                        entry.getValue()
                ))
                .collect(Collectors.toList());

        chartDestinosPopulares.getData().addAll(pieData);
        System.out.println("✅ Gráfico de destinos populares generado");
    }

    private void generarGraficoUsuariosTop(List<Boleto> boletos) {
        chartUsuariosTop.getData().clear();

        // Contar viajes por usuario
        Map<String, Long> viajesPorUsuario = boletos.stream()
                .collect(Collectors.groupingBy(
                        Boleto::getPasajero,
                        Collectors.counting()
                ));

        // Crear serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Viajes por Usuario");

        // Top 10 usuarios
        viajesPorUsuario.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });

        chartUsuariosTop.getData().add(series);
        System.out.println("✅ Gráfico de usuarios top generado");
    }

    private void actualizarEstadisticasResumen(List<Boleto> boletos, int año) {
        // Total de viajes
        long totalViajes = boletos.stream()
                .filter(b -> b.getFechaVuelo().getYear() == año)
                .count();
        lblTotalViajes.setText(String.valueOf(totalViajes));

        // Ingreso total
        double ingresoTotal = boletos.stream()
                .filter(b -> b.getFechaVuelo().getYear() == año)
                .mapToDouble(Boleto::getPrecio)
                .sum();
        lblIngresoTotal.setText(String.format("$%.2f", ingresoTotal));

        // Mes con más viajes
        Map<Integer, Long> viajesPorMes = boletos.stream()
                .filter(b -> b.getFechaVuelo().getYear() == año)
                .collect(Collectors.groupingBy(
                        b -> b.getFechaVuelo().getMonthValue(),
                        Collectors.counting()
                ));

        if (!viajesPorMes.isEmpty()) {
            Map.Entry<Integer, Long> mesMax = Collections.max(viajesPorMes.entrySet(),
                    Map.Entry.comparingByValue());

            String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            lblMesMasViajes.setText(nombresMeses[mesMax.getKey()-1] + " (" + mesMax.getValue() + " viajes)");
        } else {
            lblMesMasViajes.setText("No hay datos");
        }

        // Destino más popular
        Map<String, Long> viajesPorDestino = boletos.stream()
                .collect(Collectors.groupingBy(
                        Boleto::getDestino,
                        Collectors.counting()
                ));

        if (!viajesPorDestino.isEmpty()) {
            Map.Entry<String, Long> destinoMax = Collections.max(viajesPorDestino.entrySet(),
                    Map.Entry.comparingByValue());
            lblDestinoPopular.setText(destinoMax.getKey() + " (" + destinoMax.getValue() + " viajes)");
        } else {
            lblDestinoPopular.setText("No hay datos");
        }

        System.out.println("✅ Estadísticas resumen actualizadas");
    }
}