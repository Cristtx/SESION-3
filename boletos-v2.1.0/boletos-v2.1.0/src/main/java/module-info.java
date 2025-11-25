module org.example.bel2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires jdk.httpserver;

    opens org.example to javafx.fxml;
    opens org.example.controller to javafx.fxml;
    opens org.example.entity to javafx.fxml;
    opens org.example.webserver to javafx.fxml;
    exports org.example;
    exports org.example.controller;
    exports org.example.entity;
    exports org.example.webserver;
}