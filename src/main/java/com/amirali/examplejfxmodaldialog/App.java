package com.amirali.examplejfxmodaldialog;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("ExampleModalDialog");

        var label = new Label("Click show button to show modal dialog");
        label.setFont(Font.font(18));

        var showButton = new Button("Show");
        showButton.setPrefSize(75, 25);
        showButton.setOnAction(event -> {
            var modalDialog = new ModalDialog.Builder(stage, "Title", "Message")
                    .setPositiveButton("OK", event1 -> System.out.println("OK clicked"))
                    .setNegativeButton("Cancel", event2 -> System.out.println("Cancel clicked"))
                    .setBlurRadius(5)
                    .create();
            modalDialog.openDialog();
        });

        var root = new VBox(3, label, showButton);
        root.setAlignment(Pos.TOP_CENTER);

        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
