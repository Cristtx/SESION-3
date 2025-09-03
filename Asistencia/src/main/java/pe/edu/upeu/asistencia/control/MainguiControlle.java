package pe.edu.upeu.asistencia.control;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import jdk.jfr.Event;
import org.springframework.stereotype.Controller;


@Controller
public class MainguiControlle {
    @FXML
    private BorderPane bp;
    @FXML
    private MenuBar menuBar;
    @FXML
    private TabPane tabPane;
    @FXML
    private Menu menu1;
    @FXML
    private MenuItem menuItem1;

    @FXML
    public void initialize() {
        MenuListener mL = new MenuListener();
        MenuItemListener mIL = new MenuItemListener();
        menuItem1.setOnAction(mIL::handle);
    }

    class MenuItemListener{
        public void handle(ActionEvent e){
            System.out.println(menuItem1.getText());
        }
    }
    class MenuListener{
        public void menuSelected(Event e){
        }
    }
}