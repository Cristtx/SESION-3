package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.webserver.SimpleWebServer;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Iniciar servidor web en segundo plano
        new Thread(() -> {
            SimpleWebServer.startServer();
        }).start();

        // Esperar un poco para que el servidor inicie
        Thread.sleep(1000);

        // Cargar la interfaz de Login en lugar de la principal
        Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
        primaryStage.setTitle("Fly AUREAC - Sistema de Boletos");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
///admin@aereo.com  admin123