package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.entity.Boleto;
import org.example.entity.Usuario;
import org.example.service.BoletoService;
import org.example.service.RutaService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class BoletoController {

    @FXML private ComboBox<String> comboOrigen;
    @FXML private ComboBox<String> comboDestino;
    @FXML private ComboBox<String> comboAsiento;
    @FXML private TextField txtNumeroVuelo;
    @FXML private TextField txtPasajero;
    @FXML private TextField txtFechaVuelo;
    @FXML private TextField txtPrecio;
    @FXML private ListView<Boleto> listBoletos;
    @FXML private ImageView imgQRCode;
    @FXML private Label lblInfo;
    @FXML private Button btnCrear;
    @FXML private Button btnActualizar;
    @FXML private Button btnCancelar;
    @FXML private ComboBox<String> comboFiltrarDestino;
    @FXML private Button btnCerrarSesion;

    private BoletoService boletoService;
    private RutaService rutaService;
    private ObservableList<Boleto> boletosObservable;
    private ObservableList<Boleto> boletosFiltrados;
    private Boleto boletoEditando;
    private Usuario usuarioLogueado;

    // Configuración de asientos disponibles
    private static final String[] ASIENTOS_DISPONIBLES = {
            "1A", "1B", "1C", "1D", "1E", "1F",
            "2A", "2B", "2C", "2D", "2E", "2F",
            "3A", "3B", "3C", "3D", "3E", "3F",
            "4A", "4B", "4C", "4D", "4E", "4F",
            "5A", "5B", "5C", "5D", "5E", "5F",
            "6A", "6B", "6C", "6D", "6E", "6F",
            "7A", "7B", "7C", "7D", "7E", "7F",
            "8A", "8B", "8C", "8D", "8E", "8F"
    };

    public void initialize() {
        System.out.println(" Inicializando BoletoController...");

        boletoService = new BoletoService();
        rutaService = new RutaService();

        // INICIALIZAR LISTAS
        boletosObservable = FXCollections.observableArrayList();
        boletosFiltrados = FXCollections.observableArrayList();

        // CONFIGURAR LISTVIEW CON LA LISTA FILTRADA
        listBoletos.setItems(boletosFiltrados);

        configurarComboboxes();
        configurarListView();
        configurarBotonesEdicion(false);

        // CARGAR BOLETOS INICIALES (sin usuario aún)
        System.out.println(" Cargando boletos iniciales (sin filtro)...");

        // Cargar boletos pero sin aplicar filtros hasta que se establezca el usuario
        try {
            List<Boleto> boletos = boletoService.obtenerTodosLosBoletos();
            boletosObservable.setAll(boletos);
            boletosFiltrados.setAll(boletos); // Mostrar todos temporalmente
            System.out.println(" Boletos cargados: " + boletos.size() + " (sin filtro aplicado)");
        } catch (Exception e) {
            System.err.println(" Error cargando boletos iniciales: " + e.getMessage());
        }

        // Listener para selección
        listBoletos.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        mostrarQRCode(newValue.getId());
                    }
                }
        );

        System.out.println(" BoletoController inicializado - Esperando usuario...");
    }

    // Método para establecer el usuario logueado (llamado desde LoginController)
    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
        System.out.println(" Usuario establecido en BoletoController: " +
                (usuario != null ? usuario.getNombre() + " - Rol: " + usuario.getRol() : "null"));
        actualizarInterfazSegunRol();
    }

    private void actualizarInterfazSegunRol() {
        if (usuarioLogueado == null) {
            System.out.println("⚠️ Usuario no logueado");
            return;
        }

        if ("ADMIN".equals(usuarioLogueado.getRol())) {
            // Admin ve todo y puede filtrar
            comboFiltrarDestino.setVisible(true);
            txtPasajero.setPromptText("Nombre del pasajero");
            txtPasajero.setEditable(true);
            System.out.println(" Modo ADMIN activado - Puede ver todos los boletos");

            aplicarFiltroDestino();
        } else {
            // Usuario normal solo ve sus boletos
            comboFiltrarDestino.setVisible(false);
            txtPasajero.setText(usuarioLogueado.getNombre());
            txtPasajero.setEditable(false);
            System.out.println(" Modo USUARIO normal activado - Solo ve sus boletos: " + usuarioLogueado.getNombre());

            aplicarFiltroUsuarioNormal();
        }
    }

    private void aplicarFiltroUsuarioNormal() {
        if (usuarioLogueado != null && !"ADMIN".equals(usuarioLogueado.getRol())) {
            // FILTRAR BOLETOS SOLO DEL USUARIO ACTUAL
            List<Boleto> boletosUsuario = boletosObservable.stream()
                    .filter(b -> usuarioLogueado.getNombre().equals(b.getPasajero()))
                    .toList();

            boletosFiltrados.setAll(boletosUsuario);
        }
    }

    private void refrescarListView() {
        // Forzar actualización del ListView
        listBoletos.refresh();

        // También puedes probar reconstruyendo los items
        List<Boleto> itemsActuales = new ArrayList<>(boletosFiltrados);
        boletosFiltrados.setAll(itemsActuales);

        System.out.println("🔄 ListView refrescado. Items en ListView: " + listBoletos.getItems().size());
    }

    private void configurarComboboxes() {
        // Configurar origen
        List<String> ciudades = rutaService.getCiudadesDisponibles();
        comboOrigen.getItems().addAll(ciudades);
        comboOrigen.setValue("Lima"); // Ciudad por defecto

        // Listener para origen - actualizar destinos disponibles
        comboOrigen.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                comboDestino.getItems().clear();
                Map<String, Double> destinos = rutaService.getDestinosDesde(newVal);
                if (destinos != null && !destinos.isEmpty()) {
                    comboDestino.getItems().addAll(destinos.keySet());
                    // Seleccionar el primer destino disponible
                    if (!destinos.keySet().isEmpty()) {
                        comboDestino.setValue(destinos.keySet().iterator().next());
                    }
                } else {
                    mostrarError("No hay destinos disponibles desde " + newVal);
                    comboDestino.getItems().clear();
                    txtPrecio.clear();
                    txtNumeroVuelo.clear();
                }
                actualizarNumeroVueloYPrecio();
            }
        });

        // Listener para destino - actualizar precio automáticamente
        comboDestino.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && comboOrigen.getValue() != null) {
                actualizarNumeroVueloYPrecio();
            }
        });

        // Configurar asientos
        actualizarAsientosDisponibles();

        // Configurar filtro por destino
        comboFiltrarDestino.getItems().add("Todos los destinos");
        comboFiltrarDestino.getItems().addAll(rutaService.getCiudadesDisponibles());
        comboFiltrarDestino.setValue("Todos los destinos");

        // Listener para filtro
        comboFiltrarDestino.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltroDestino();
        });

        // Inicializar destinos desde Lima
        Map<String, Double> destinos = rutaService.getDestinosDesde("Lima");
        if (destinos != null && !destinos.isEmpty()) {
            comboDestino.getItems().addAll(destinos.keySet());
            if (!comboDestino.getItems().isEmpty()) {
                comboDestino.setValue(comboDestino.getItems().get(0));
            }
        }
    }

    private void actualizarNumeroVueloYPrecio() {
        if (comboOrigen.getValue() != null && comboDestino.getValue() != null) {
            // Generar número de vuelo automático
            String numeroVuelo = rutaService.generarNumeroVuelo(comboOrigen.getValue(), comboDestino.getValue());
            txtNumeroVuelo.setText(numeroVuelo);

            // Actualizar precio
            Double precio = rutaService.getPrecio(comboOrigen.getValue(), comboDestino.getValue());
            if (precio != null) {
                // Formatear el precio siempre con 2 decimales
                txtPrecio.setText(String.format("%.2f", precio));
            } else {
                txtPrecio.setText("0.00");
                mostrarError("No se encontró precio para esta ruta. Contacte al administrador.");
            }

            // Actualizar asientos disponibles para este vuelo
            actualizarAsientosDisponibles();
        }
    }

    private void aplicarFiltroDestino() {
        String destinoFiltro = comboFiltrarDestino.getValue();
        if (destinoFiltro == null || "Todos los destinos".equals(destinoFiltro)) {
            boletosFiltrados.setAll(boletosObservable);
        } else {
            List<Boleto> filtrados = boletosObservable.stream()
                    .filter(b -> destinoFiltro.equals(b.getDestino()))
                    .toList();
            boletosFiltrados.setAll(filtrados);
        }
        refrescarListView();
    }

    private void actualizarAsientosDisponibles() {
        comboAsiento.getItems().clear();

        if (comboOrigen.getValue() == null || comboDestino.getValue() == null) {
            return;
        }

        // Obtener asientos ocupados para el vuelo específico (origen-destino)
        String numeroVuelo = txtNumeroVuelo.getText();
        List<String> asientosOcupados = boletosObservable.stream()
                .filter(b -> numeroVuelo != null && numeroVuelo.equals(b.getNumeroVuelo()))
                .map(Boleto::getAsiento)
                .toList();

        for (String asiento : ASIENTOS_DISPONIBLES) {
            if (!asientosOcupados.contains(asiento)) {
                comboAsiento.getItems().add(asiento + " ✓");
            } else {
                comboAsiento.getItems().add(asiento + " ✗ (Ocupado)");
            }
        }

        // Seleccionar el primer asiento disponible si hay
        if (!comboAsiento.getItems().isEmpty()) {
            String primerAsientoDisponible = comboAsiento.getItems().stream()
                    .filter(item -> item.contains("✓"))
                    .findFirst()
                    .orElse(comboAsiento.getItems().get(0));
            comboAsiento.setValue(primerAsientoDisponible);
        }
    }

    private void configurarListView() {
        listBoletos.setCellFactory(new Callback<ListView<Boleto>, ListCell<Boleto>>() {
            @Override
            public ListCell<Boleto> call(ListView<Boleto> param) {
                return new ListCell<Boleto>() {
                    @Override
                    protected void updateItem(Boleto boleto, boolean empty) {
                        super.updateItem(boleto, empty);
                        if (empty || boleto == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                            String info = String.format(
                                    "✈️ %s | 👤 %s | %s → %s | %s | Asiento: %s | $%.2f",
                                    boleto.getNumeroVuelo(),
                                    boleto.getPasajero(),
                                    boleto.getOrigen(),
                                    boleto.getDestino(),
                                    boleto.getFechaVuelo().format(formatter),
                                    boleto.getAsiento(),
                                    boleto.getPrecio()
                            );
                            setText(info);
                            setStyle("-fx-font-family: 'Arial'; -fx-font-size: 11px; -fx-padding: 5px;");
                        }
                    }
                };
            }
        });
    }

    private void configurarBotonesEdicion(boolean editando) {
        btnCrear.setDisable(editando);
        btnActualizar.setVisible(editando);
        btnCancelar.setVisible(editando);

        if (editando) {
            btnCrear.setText("Crear Nuevo");
        } else {
            btnCrear.setText("Crear Boleto");
            limpiarFormulario();
            boletoEditando = null;
        }
    }
    @FXML
    private void crearBoleto() {
        if (boletoEditando != null) {
            configurarBotonesEdicion(false);
            return;
        }

        try {
            if (!validarFormulario()) return;

            LocalDateTime fechaVuelo = LocalDateTime.parse(txtFechaVuelo.getText());

            // Validar y obtener el precio
            String precioText = txtPrecio.getText().trim();
            if (precioText.isEmpty() || "0.00".equals(precioText)) {
                mostrarError("El precio no está disponible para esta ruta. Seleccione otra ruta.");
                return;
            }

            precioText = precioText.replace("$", "").trim().replace(",", ".");
            Double precio = Double.parseDouble(precioText);

            if (precio <= 0) {
                mostrarError("El precio debe ser mayor a 0");
                return;
            }

            String asientoSeleccionado = comboAsiento.getValue();
            if (asientoSeleccionado == null) {
                mostrarError("Seleccione un asiento");
                return;
            }

            String asientoLimpio = asientoSeleccionado.split(" ")[0];

            if (asientoSeleccionado.contains("✗")) {
                mostrarError("El asiento seleccionado está ocupado. Por favor elija otro.");
                return;
            }

            // CREAR BOLETO
            Boleto boleto = new Boleto(
                    txtNumeroVuelo.getText(),
                    txtPasajero.getText(),
                    comboOrigen.getValue(),
                    comboDestino.getValue(),
                    fechaVuelo,
                    asientoLimpio,
                    precio
            );

            Boleto boletoCreado = boletoService.crearBoleto(boleto);

            // AGREGAR A LA LISTA OBSERVABLE
            boletosObservable.add(0, boletoCreado);

            // APLICAR FILTRO SEGÚN ROL
            if (usuarioLogueado != null && !"ADMIN".equals(usuarioLogueado.getRol())) {
                aplicarFiltroUsuarioNormal();
                // MOSTRAR SOLO EL NUEVO CÓDIGO PARA USUARIO NORMAL
                System.out.println(boletoCreado.getCodigoReserva());
            } else {
                aplicarFiltroDestino();
                // PARA ADMIN, se mostrará en la siguiente recarga
            }

            // FORZAR ACTUALIZACIÓN
            listBoletos.refresh();

            // SELECCIONAR EL NUEVO BOLETO SI PASA EL FILTRO
            if (boletosFiltrados.contains(boletoCreado)) {
                listBoletos.getSelectionModel().select(boletoCreado);
            }

            limpiarFormulario();
            mostrarInfo("Boleto creado exitosamente: " + boletoCreado.getCodigoReserva());

            // ACTUALIZAR ASIENTOS DISPONIBLES
            actualizarAsientosDisponibles();

        } catch (DateTimeParseException e) {
            mostrarError("Formato de fecha incorrecto. Use: YYYY-MM-DDTHH:MM (ej: 2025-11-15T14:30)");
        } catch (NumberFormatException e) {
            mostrarError("Formato de precio inválido. Use números con punto o coma decimal");
        } catch (Exception e) {
            mostrarError("Error creando boleto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarListaBoletosCompleta() {
        System.out.println(" Actualizando lista completa de boletos...");

        try {
            // Recargar desde la base de datos
            List<Boleto> todosLosBoletos = boletoService.obtenerTodosLosBoletos();
            boletosObservable.setAll(todosLosBoletos);

            System.out.println(" Boletos cargados desde BD: " + todosLosBoletos.size());

            // Aplicar filtros según el rol
            if (usuarioLogueado != null && !"ADMIN".equals(usuarioLogueado.getRol())) {
                aplicarFiltroUsuarioNormal();
            } else {
                aplicarFiltroDestino();
            }

            // Forzar refresh del ListView
            listBoletos.refresh();

            // DEBUG: Mostrar info de los boletos
            for (Boleto b : todosLosBoletos) {
                System.out.println("    " + b.getCodigoReserva() + " - " + b.getPasajero() + " - " + b.getOrigen() + "→" + b.getDestino());
            }
        } catch (Exception e) {
            System.err.println(" Error actualizando lista: " + e.getMessage());
        }
    }

    @FXML
    private void editarBoleto() {
        Boleto boletoSeleccionado = listBoletos.getSelectionModel().getSelectedItem();
        if (boletoSeleccionado != null) {
            cargarBoletoEnFormulario(boletoSeleccionado);
            boletoEditando = boletoSeleccionado;
            configurarBotonesEdicion(true);
            mostrarInfo("Editando boleto: " + boletoSeleccionado.getCodigoReserva());
        } else {
            mostrarError("Selecciona un boleto para editar");
        }
    }

    @FXML
    private void actualizarBoleto() {
        try {
            if (!validarFormulario()) return;

            LocalDateTime fechaVuelo = LocalDateTime.parse(txtFechaVuelo.getText());

            // Validar y obtener el precio - manejar mejor el formato
            String precioText = txtPrecio.getText().trim();
            if (precioText.isEmpty() || "0.00".equals(precioText)) {
                mostrarError("El precio no está disponible para esta ruta. Seleccione otra ruta.");
                return;
            }

            // Limpiar el texto del precio (remover $ si existe y espacios)
            precioText = precioText.replace("$", "").trim();

            // Convertir coma decimal a punto decimal para soportar ambos formatos
            precioText = precioText.replace(",", ".");

            Double precio;
            try {
                precio = Double.parseDouble(precioText);
            } catch (NumberFormatException e) {
                mostrarError("Formato de precio inválido. Use números con punto o coma decimal (ej: 500.00 o 500,00)");
                return;
            }

            // Validar que el precio sea mayor a 0
            if (precio <= 0) {
                mostrarError("El precio debe ser mayor a 0");
                return;
            }

            // Limpiar el estado del asiento
            String asientoSeleccionado = comboAsiento.getValue();
            if (asientoSeleccionado == null) {
                mostrarError("Seleccione un asiento");
                return;
            }

            String asientoLimpio = asientoSeleccionado.split(" ")[0];

            // Validar que el asiento no esté ocupado
            if (asientoSeleccionado.contains("✗")) {
                mostrarError("El asiento seleccionado está ocupado. Por favor elija otro.");
                return;
            }

            boletoEditando.setNumeroVuelo(txtNumeroVuelo.getText());
            boletoEditando.setPasajero(txtPasajero.getText());
            boletoEditando.setOrigen(comboOrigen.getValue());
            boletoEditando.setDestino(comboDestino.getValue());
            boletoEditando.setFechaVuelo(fechaVuelo);
            boletoEditando.setAsiento(asientoLimpio);
            boletoEditando.setPrecio(precio);

            Boleto boletoActualizado = boletoService.actualizarBoleto(boletoEditando);

            // Actualizar la lista
            int index = boletosObservable.indexOf(boletoEditando);
            if (index >= 0) {
                boletosObservable.set(index, boletoActualizado);
            }

            if (usuarioLogueado != null && !"ADMIN".equals(usuarioLogueado.getRol())) {
                aplicarFiltroUsuarioNormal();
            } else {
                aplicarFiltroDestino();
            }

            actualizarAsientosDisponibles();

            configurarBotonesEdicion(false);
            mostrarInfo("Boleto actualizado exitosamente");

        } catch (Exception e) {
            mostrarError("Error actualizando boleto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelarEdicion() {
        configurarBotonesEdicion(false);
        mostrarInfo("Edición cancelada");
    }

    @FXML
    private void generarQRCode() {
        Boleto boletoSeleccionado = listBoletos.getSelectionModel().getSelectedItem();
        if (boletoSeleccionado != null) {
            mostrarQRCode(boletoSeleccionado.getId());
        } else {
            mostrarError("Selecciona un boleto de la lista");
        }
    }

    @FXML
    private void cerrarSesion() {
        try {
            // Cerrar la ventana actual
            Stage stageActual = (Stage) btnCerrarSesion.getScene().getWindow();
            stageActual.close();

            // Abrir la ventana de login
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

    @FXML
    private void recargarBoletos() {
        cargarBoletos(); // Esto mostrará la información según el rol
        mostrarInfo("Boletos recargados - Mostrando: " + boletosFiltrados.size() + " boletos");
    }
    @FXML
    private void eliminarBoleto() {
        Boleto boletoSeleccionado = listBoletos.getSelectionModel().getSelectedItem();
        if (boletoSeleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Estás seguro de eliminar este boleto?");
            alert.setContentText("Vuelo: " + boletoSeleccionado.getNumeroVuelo() +
                    "\nPasajero: " + boletoSeleccionado.getPasajero());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    boolean eliminado = boletoService.eliminarBoleto(boletoSeleccionado.getId());
                    if (eliminado) {
                        // Remover de ambas listas
                        boletosObservable.remove(boletoSeleccionado);
                        boletosFiltrados.remove(boletoSeleccionado);

                        // Aplicar filtro nuevamente
                        if (usuarioLogueado != null && !"ADMIN".equals(usuarioLogueado.getRol())) {
                            aplicarFiltroUsuarioNormal();
                        }

                        listBoletos.refresh();
                        imgQRCode.setImage(null);

                        mostrarInfo("Boleto eliminado exitosamente");
                    } else {
                        mostrarError("Error eliminando el boleto");
                    }
                } catch (Exception e) {
                    mostrarError("Error eliminando boleto: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            mostrarError("Selecciona un boleto para eliminar");
        }
    }

    private void cargarBoletoEnFormulario(Boleto boleto) {
        txtPasajero.setText(boleto.getPasajero());
        comboOrigen.setValue(boleto.getOrigen());
        comboDestino.setValue(boleto.getDestino());
        txtNumeroVuelo.setText(boleto.getNumeroVuelo());
        txtFechaVuelo.setText(boleto.getFechaVuelo().toString());
        txtPrecio.setText(String.format("%.2f", boleto.getPrecio()));

        // Buscar el asiento en la lista
        String asientoBuscado = boleto.getAsiento();
        String asientoEncontrado = comboAsiento.getItems().stream()
                .filter(item -> item.startsWith(asientoBuscado))
                .findFirst()
                .orElse(asientoBuscado + " ✓");
        comboAsiento.setValue(asientoEncontrado);
    }

    private void mostrarQRCode(Long boletoId) {
        try {
            Image qrImage = boletoService.generarQRCodeParaBoleto(boletoId, 200, 200);
            imgQRCode.setImage(qrImage);
            mostrarInfo("QR Code generado - Escanéalo para ver info en tu buscador");
        } catch (Exception e) {
            mostrarError("Error generando QR: " + e.getMessage());
        }
    }

    private void cargarBoletos() {
        try {
            System.out.println(" Cargando boletos desde la base de datos...");

            List<Boleto> boletos = boletoService.obtenerTodosLosBoletos();

            // Limpiar y agregar nuevos boletos
            boletosObservable.clear();
            boletosObservable.addAll(boletos);

            // Aplicar filtros según el rol del usuario
            if (usuarioLogueado != null && !"ADMIN".equals(usuarioLogueado.getRol())) {
                // USUARIO NORMAL
                aplicarFiltroUsuarioNormal();

                // Mostrar solo sus códigos de reserva
                System.out.println(" Usuario establecido: " + usuarioLogueado.getNombre() + " - Rol: " + usuarioLogueado.getRol());
                List<Boleto> boletosUsuario = boletosObservable.stream()
                        .filter(b -> usuarioLogueado.getNombre().equals(b.getPasajero()))
                        .toList();

                for (Boleto b : boletosUsuario) {
                    System.out.println(b.getCodigoReserva());
                }

            } else if (usuarioLogueado != null && "ADMIN".equals(usuarioLogueado.getRol())) {
                // ADMIN
                aplicarFiltroDestino();

                // Mostrar todos los códigos de reserva
                System.out.println(" ADMIN - TODOS los boletos en BD:");
                for (Boleto b : boletos) {
                    System.out.println(b.getCodigoReserva());
                }
            }

            // Forzar actualización del ListView
            listBoletos.refresh();

        } catch (Exception e) {
            System.err.println(" Error cargando boletos: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error cargando boletos: " + e.getMessage());
        }
    }

    private boolean validarFormulario() {
        if (txtPasajero.getText().isEmpty() ||
                comboOrigen.getValue() == null ||
                comboDestino.getValue() == null ||
                txtFechaVuelo.getText().isEmpty() ||
                comboAsiento.getValue() == null) {
            mostrarError("Todos los campos son obligatorios");
            return false;
        }

        // Validar que el precio esté disponible
        String precioText = txtPrecio.getText().trim();
        if (precioText.isEmpty() || "0.00".equals(precioText)) {
            mostrarError("No hay precio disponible para esta ruta. Seleccione otra ruta.");
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        if (usuarioLogueado == null || "ADMIN".equals(usuarioLogueado.getRol())) {
            txtPasajero.clear();
        } else {
            // Para usuarios normales, mantener su nombre
            txtPasajero.setText(usuarioLogueado.getNombre());
        }
        txtFechaVuelo.clear();
        // Los demás campos se mantienen porque son automáticos
        actualizarAsientosDisponibles();
    }

    private void mostrarError(String mensaje) {
        lblInfo.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        lblInfo.setText("❌ " + mensaje);
    }

    private void mostrarInfo(String mensaje) {
        lblInfo.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        lblInfo.setText("✅ " + mensaje);
    }
}