package com.frederickamakye.smsplus.ui;

import com.frederickamakye.smsplus.services.StudentService;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardView extends VBox {

    private final StudentService studentService;

    private final Label totalStudentsLabel;
    private final Label activeStudentsLabel;
    private final Label inactiveStudentsLabel;
    private final Label averageGpaLabel;

    public DashboardView() {

        studentService = new StudentService();

        Label header = new Label("Summary");
        header.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        totalStudentsLabel = new Label();
        activeStudentsLabel = new Label();
        inactiveStudentsLabel = new Label();
        averageGpaLabel = new Label();   

        totalStudentsLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;"
        );     
        activeStudentsLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;"
        );     
        inactiveStudentsLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;"
        );     
        averageGpaLabel.setStyle(
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;"
        );     

        VBox totalCard = createCard("Total Students", totalStudentsLabel);
        VBox activeCard = createCard("Active Students", activeStudentsLabel);
        VBox inactiveCard = createCard("Inactive Students", inactiveStudentsLabel);
        VBox gpaCard = createCard("Average GPA", averageGpaLabel);

        HBox row1 = new HBox(15, totalCard, activeCard);
        HBox row2 = new HBox(15, inactiveCard, gpaCard);

        setSpacing(20);
        setStyle("-fx-padding: 20;");

        getChildren().addAll(header, row1, row2);

        refreshStats();
    }

    private void refreshStats() {
        try {

            int total = studentService.getAllStudents().size();

            int active = studentService.filterByStatus("Active").size();
            int inactive = studentService.filterByStatus("Inactive").size();

            double averageGpa = studentService.getAllStudents()
                    .stream()
                    .mapToDouble(s -> s.getGpa())
                    .average()
                    .orElse(0.0);

            totalStudentsLabel.setText(String.valueOf(total));
            activeStudentsLabel.setText(String.valueOf(active));
            inactiveStudentsLabel.setText(String.valueOf(inactive));
            averageGpaLabel.setText(String.format("%.2f", averageGpa));

        } catch (Exception e) {

            totalStudentsLabel.setText("Failed to load statistics");
        }
    }

    public void refresh() {
        refreshStats();
    }

    private VBox createCard(String title, Label valueLabel) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 13px;" +
            "-fx-font-weight: bold;"
        );
        VBox card = new VBox(5, titleLabel, valueLabel);

        card.setStyle(
            "-fx-padding: 18;" +
            "-fx-border-color: lightgray;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-background-color: white;"
        );

        card.setPrefWidth(200);

        return card;
    }
}