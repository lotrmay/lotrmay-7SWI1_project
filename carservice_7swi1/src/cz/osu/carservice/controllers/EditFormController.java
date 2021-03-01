package cz.osu.carservice.controllers;

import cz.osu.carservice.models.utils.DragWindowUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class EditFormController {
    @FXML
    private void returnToMainScene(MouseEvent event){
        try {
            Parent home_page = FXMLLoader.load(getClass().getResource("../forms/mainForm.fxml"));
            Stage app = (Stage)((Node) event.getSource()).getScene().getWindow();

            DragWindowUtils.moveWindow(home_page,app);

            Scene scene = new Scene(home_page);
            scene.setFill(Color.TRANSPARENT);

            app.setScene(scene);
            app.show();
        } catch (IOException e) {
            System.err.println("Došlo k chybě při načítání formuláře mainForm!");
            System.err.println(e.getMessage());
        }
    }
    @FXML
    private void closeApplication(MouseEvent event){
        Platform.exit();
        System.exit(0);
    }
}