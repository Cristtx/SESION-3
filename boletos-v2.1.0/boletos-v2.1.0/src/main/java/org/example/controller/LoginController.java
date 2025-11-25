package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.entity.Usuario;
import org.example.service.UsuarioService;
import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML private TextField txtEmailLogin;
    @FXML private PasswordField txtPasswordLogin;
    @FXML private Button btnLogin;
    @FXML private Button btnIrARegistro;
    @FXML private Label lblLoginError;
    @FXML private VBox formLogin;
    @FXML private VBox formRegistro;

    @FXML private TextField txtNombreRegistro;
    @FXML private TextField txtEmailRegistro;
    @FXML private PasswordField txtPasswordRegistro;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private Button btnRegistrar;
    @FXML private Button btnIrALogin;
    @FXML private Label lblRegistroError;
    @FXML private Label lblRegistroSuccess;

    private UsuarioService usuarioService;
    private Usuario usuarioLogueado;

    public void initialize() {
        usuarioService = new UsuarioService();
        mostrarPantallaLogin();
    }

    @FXML
    private void login() {
        String email = txtEmailLogin.getText().trim();
        String password = txtPasswordLogin.getText();

        if (email.isEmpty() || password.isEmpty()) {
            mostrarErrorLogin("Por favor complete todos los campos");
            return;
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioService.login(email, password);
            if (usuarioOpt.isPresent()) {
                usuarioLogueado = usuarioOpt.get();

                // Redirigir según el rol
                if ("ADMIN".equals(usuarioLogueado.getRol())) {
                    abrirPanelAdministracion();
                } else {
                    abrirSistemaBoletos();
                }
            } else {
                mostrarErrorLogin("Email o contraseña incorrectos");
            }
        } catch (Exception e) {
            mostrarErrorLogin("Error durante el login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void registrar() {
        String nombre = txtNombreRegistro.getText().trim();
        String email = txtEmailRegistro.getText().trim();
        String password = txtPasswordRegistro.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        // Validaciones
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty()) {
            mostrarErrorRegistro("Por favor complete todos los campos");
            return;
        }

        if (!password.equals(confirmarPassword)) {
            mostrarErrorRegistro("Las contraseñas no coinciden");
            return;
        }

        if (password.length() < 6) {
            mostrarErrorRegistro("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        if (!email.contains("@")) {
            mostrarErrorRegistro("Por favor ingrese un email válido");
            return;
        }

        try {
            Usuario nuevoUsuario = new Usuario(nombre, email, password);
            nuevoUsuario.setRol("USER");

            Usuario usuarioRegistrado = usuarioService.registrarUsuario(nuevoUsuario);

            mostrarExitoRegistro("¡Registro exitoso! Ahora puede iniciar sesión");
            limpiarFormularioRegistro();

            // Cambiar automáticamente a la pantalla de login después de 2 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::mostrarPantallaLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            mostrarErrorRegistro("Error al registrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void irARegistro() {
        mostrarPantallaRegistro();
    }

    @FXML
    private void irALogin() {
        mostrarPantallaLogin();
    }

    private void mostrarPantallaLogin() {
        if (formRegistro != null && formLogin != null) {
            // Ocultar formulario de registro
            formRegistro.setVisible(false);
            formRegistro.setManaged(false);

            // Mostrar formulario de login
            formLogin.setVisible(true);
            formLogin.setManaged(true);
        } else {
            // Fallback al método original si las variables no están disponibles
            txtNombreRegistro.setVisible(false);
            txtEmailRegistro.setVisible(false);
            txtPasswordRegistro.setVisible(false);
            txtConfirmarPassword.setVisible(false);
            btnRegistrar.setVisible(false);
            btnIrALogin.setVisible(false);
            lblRegistroError.setVisible(false);
            lblRegistroSuccess.setVisible(false);

            txtEmailLogin.setVisible(true);
            txtPasswordLogin.setVisible(true);
            btnLogin.setVisible(true);
            btnIrARegistro.setVisible(true);
            lblLoginError.setVisible(true);
        }

        limpiarFormularioLogin();
    }

    private void mostrarPantallaRegistro() {
        if (formRegistro != null && formLogin != null) {
            // Ocultar formulario de login
            formLogin.setVisible(false);
            formLogin.setManaged(false);

            // Mostrar formulario de registro
            formRegistro.setVisible(true);
            formRegistro.setManaged(true);
        } else {
            // Fallback al método original si las variables no están disponibles
            txtEmailLogin.setVisible(false);
            txtPasswordLogin.setVisible(false);
            btnLogin.setVisible(false);
            btnIrARegistro.setVisible(false);
            lblLoginError.setVisible(false);

            txtNombreRegistro.setVisible(true);
            txtEmailRegistro.setVisible(true);
            txtPasswordRegistro.setVisible(true);
            txtConfirmarPassword.setVisible(true);
            btnRegistrar.setVisible(true);
            btnIrALogin.setVisible(true);
            lblRegistroError.setVisible(true);
            lblRegistroSuccess.setVisible(false);
        }

        limpiarFormularioRegistro();
    }

    private void abrirSistemaBoletos() {
        try {
            // Cerrar la ventana actual de login
            Stage stageActual = (Stage) btnLogin.getScene().getWindow();
            stageActual.close();

            // Abrir la ventana principal del sistema
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
            Parent root = loader.load();

            // Pasar el usuario logueado al controlador principal
            BoletoController boletoController = loader.getController();
            boletoController.setUsuarioLogueado(usuarioLogueado);

            Stage stage = new Stage();
            stage.setTitle("Sistema de Boletos AUREAC - Usuario: " + usuarioLogueado.getNombre());
            stage.setScene(new Scene(root, 900, 700));
            stage.show();

        } catch (IOException e) {
            mostrarErrorLogin("Error al abrir el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirPanelAdministracion() {
        try {
            Stage stageActual = (Stage) btnLogin.getScene().getWindow();
            stageActual.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Panel de Administración - AUREAC Airlines");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (IOException e) {
            mostrarErrorLogin("Error al abrir el panel de administración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void limpiarFormularioLogin() {
        txtEmailLogin.clear();
        txtPasswordLogin.clear();
        lblLoginError.setText("");
    }

    private void limpiarFormularioRegistro() {
        txtNombreRegistro.clear();
        txtEmailRegistro.clear();
        txtPasswordRegistro.clear();
        txtConfirmarPassword.clear();
        lblRegistroError.setText("");
        lblRegistroSuccess.setText("");
        lblRegistroSuccess.setVisible(false);
    }

    private void mostrarErrorLogin(String mensaje) {
        lblLoginError.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        lblLoginError.setText("❌ " + mensaje);
    }

    private void mostrarErrorRegistro(String mensaje) {
        lblRegistroError.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        lblRegistroError.setText("❌ " + mensaje);
    }

    private void mostrarExitoRegistro(String mensaje) {
        lblRegistroSuccess.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        lblRegistroSuccess.setText("✅ " + mensaje);
        lblRegistroSuccess.setVisible(true);
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }
}