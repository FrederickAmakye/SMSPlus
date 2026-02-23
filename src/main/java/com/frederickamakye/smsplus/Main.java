package com.frederickamakye.smsplus;

import com.frederickamakye.smsplus.utils.Database;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Database.init();
        stage.setTitle("Student Management System Plus");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}