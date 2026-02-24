package com.frederickamakye.smsplus;

import com.frederickamakye.smsplus.utils.Database;
import com.frederickamakye.smsplus.ui.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Initialize database & schema before UI loads
        Database.init();

        // Load main UI container
        Scene scene = new Scene(new MainView(), 1000, 600);

        stage.setTitle("Student Management System Plus");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}