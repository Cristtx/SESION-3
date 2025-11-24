package pe.edu.upeu.cine.control;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pe.edu.upeu.cine.repositorio.UsuarioRepositorio;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;

    private UsuarioRepositorio usuarioRepo = new UsuarioRepositorio();

    @FXML
    public void login() {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (usuarioRepo.validarUsuario(user, pass)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Principal.fxml"));

                Stage stage = new Stage();
                stage.setScene(new Scene(loader.load()));
                stage.setTitle("Sistema Cine");
                stage.show();


                ((Stage) txtUsuario.getScene().getWindow()).close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            new Alert(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos").show();
        }
    }
}
