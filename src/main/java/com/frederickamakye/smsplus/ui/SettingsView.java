package com.frederickamakye.smsplus.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;

public class SettingsView extends VBox {

    // Default threshold
    private static double atRiskThreshold = 2.0;

    private final Spinner<Double> thresholdSpinner;

    public SettingsView() {

        Label header = new Label("Settings");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label thresholdLabel = new Label("At-Risk GPA Threshold");

        thresholdSpinner = new Spinner<>(0.0, 4.0, atRiskThreshold, 0.1);
        thresholdSpinner.setEditable(true);

        Button saveBtn = new Button("Save Settings");

        setSpacing(10);
        setStyle("-fx-padding: 20;");

        getChildren().addAll(
                header,
                thresholdLabel,
                thresholdSpinner,
                saveBtn
        );

        saveBtn.setOnAction(e -> saveSettings());
    }

    private void saveSettings() {

        atRiskThreshold = thresholdSpinner.getValue();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("At-Risk GPA Threshold Updated");
        alert.show();
    }

    public static double getAtRiskThreshold() {
        return atRiskThreshold;
    }
}