package com.frederickamakye.smsplus.ui;

import java.sql.SQLException;
import java.util.List;

import com.frederickamakye.smsplus.models.GpaBandSummary;
import com.frederickamakye.smsplus.models.ProgrammeSummary;
import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.services.StudentService;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import java.io.File;

import com.frederickamakye.smsplus.utils.CsvHandler;
import com.frederickamakye.smsplus.models.Student;

public class ReportsView extends BorderPane {

    private final StudentService studentService;
    private final TableView<Object> resultsTable;

    private final ComboBox<String> reportSelector;

    private final ComboBox<String> programmeFilter;
    private final ComboBox<Integer> levelFilter;
    private final Spinner<Double> thresholdSpinner;

    private final VBox filtersContainer;

    public ReportsView() {

        studentService = new StudentService();

        // ================= HEADER =================

        Label header = new Label("Reports");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // ================= REPORT SELECTOR =================

        reportSelector = new ComboBox<>();
        reportSelector.getItems().addAll(
                "Top Performers",
                "At Risk Students",
                "GPA Distribution",
                "Programme Summary"
        );
        reportSelector.setPromptText("Select Report");

        HBox controlsBar = new HBox(10);
        controlsBar.getChildren().addAll(
                new Label("Report Type:"),
                reportSelector
        );

        controlsBar.setStyle(
                "-fx-padding: 10;" +
                "-fx-alignment: center-left;"
        );

        // ================= FILTERS =================

        programmeFilter = new ComboBox<>();
        programmeFilter.setPromptText("Programme");

        levelFilter = new ComboBox<>();
        levelFilter.getItems().addAll(100, 200, 300, 400);
        levelFilter.setPromptText("Level");

        thresholdSpinner = new Spinner<>(0.0, 4.0, 2.0, 0.1);
        thresholdSpinner.setEditable(true);

        filtersContainer = new VBox(10);
        filtersContainer.setStyle("-fx-padding: 0 10 10 10;");

        VBox topSection = new VBox(5);
        topSection.getChildren().addAll(header, controlsBar, filtersContainer);
        topSection.setStyle("-fx-padding: 10 15 5 15;");

        setTop(topSection);

        // ================= TABLE =================

        resultsTable = new TableView<>();
        resultsTable.setPlaceholder(new Label("No report data"));
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        setCenter(resultsTable);

        // ================= EXPORT BUTTON =================

        Button exportBtn = new Button("Export Report");

        HBox bottomBar = new HBox();
        bottomBar.getChildren().add(exportBtn);

        bottomBar.setStyle(
                "-fx-padding: 10;" +
                "-fx-alignment: center-right;"
        );

        exportBtn.setOnAction(e -> exportCurrentReport());

        setBottom(bottomBar);

        // ================= EVENTS =================

        reportSelector.setOnAction(e -> updateReport());

        programmeFilter.setOnAction(e -> updateReport());
        levelFilter.setOnAction(e -> updateReport());
        thresholdSpinner.valueProperty().addListener((obs, o, n) -> updateReport());

        loadProgrammes();
    }

    // ================= REPORT SWITCHING =================

    private void updateReport() {

        String selectedReport = reportSelector.getValue();

        if (selectedReport == null) {
            return;
        }

        filtersContainer.getChildren().clear();
        resultsTable.getColumns().clear();

        try {

            if (selectedReport.equals("Top Performers")) {

                filtersContainer.getChildren().add(
                        new HBox(10, programmeFilter, levelFilter)
                );

                TableColumn<Object, String> idCol = new TableColumn<>("Student ID");
                idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

                TableColumn<Object, String> nameCol = new TableColumn<>("Full Name");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

                TableColumn<Object, String> programmeCol = new TableColumn<>("Programme");
                programmeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));

                TableColumn<Object, Integer> levelCol = new TableColumn<>("Level");
                levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));

                TableColumn<Object, Double> gpaCol = new TableColumn<>("GPA");
                gpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));

                resultsTable.getColumns().addAll(
                        idCol,
                        nameCol,
                        programmeCol,
                        levelCol,
                        gpaCol
                );

                List<Student> results = studentService.getTopPerformersReport(
                        programmeFilter.getValue(),
                        levelFilter.getValue()
                );

                resultsTable.setItems(FXCollections.observableArrayList(results));
            }

            else if (selectedReport.equals("At Risk Students")) {

                filtersContainer.getChildren().add(
                        new HBox(10, new Label("GPA Threshold:"), thresholdSpinner)
                );

                TableColumn<Object, String> idCol = new TableColumn<>("Student ID");
                idCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

                TableColumn<Object, String> nameCol = new TableColumn<>("Full Name");
                nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

                TableColumn<Object, String> programmeCol = new TableColumn<>("Programme");
                programmeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));

                TableColumn<Object, Integer> levelCol = new TableColumn<>("Level");
                levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));

                TableColumn<Object, Double> gpaCol = new TableColumn<>("GPA");
                gpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));

                resultsTable.getColumns().addAll(
                        idCol,
                        nameCol,
                        programmeCol,
                        levelCol,
                        gpaCol
                );

                List<Student> results = studentService.getAtRiskStudentsReport(
                        thresholdSpinner.getValue()
                );

                resultsTable.setItems(FXCollections.observableArrayList(results));
            }

            else if (selectedReport.equals("GPA Distribution")) {

                TableColumn<Object, String> bandCol = new TableColumn<>("GPA Band");
                bandCol.setCellValueFactory(new PropertyValueFactory<>("band"));

                TableColumn<Object, Integer> totalCol = new TableColumn<>("Total Students");
                totalCol.setCellValueFactory(new PropertyValueFactory<>("totalStudents"));

                resultsTable.getColumns().addAll(bandCol, totalCol);

                List<GpaBandSummary> results = studentService.getGpaDistributionReport();

                resultsTable.setItems(FXCollections.observableArrayList(results));
            }

            else if (selectedReport.equals("Programme Summary")) {

                TableColumn<Object, String> programmeCol = new TableColumn<>("Programme");
                programmeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));

                TableColumn<Object, Integer> totalCol = new TableColumn<>("Total Students");
                totalCol.setCellValueFactory(new PropertyValueFactory<>("totalStudents"));

                TableColumn<Object, Double> avgCol = new TableColumn<>("Average GPA");
                avgCol.setCellValueFactory(new PropertyValueFactory<>("averageGpa"));

                resultsTable.getColumns().addAll(programmeCol, totalCol, avgCol);

                List<ProgrammeSummary> results = studentService.getProgrammeSummaryReport();

                resultsTable.setItems(FXCollections.observableArrayList(results));
            }

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }

    // ================= HELPERS =================

    private void loadProgrammes() {

        try {

            List<String> programmes = studentService.getProgrammes();

            programmeFilter.getItems().clear();
            programmeFilter.getItems().addAll(programmes);

        } catch (SQLException e) {

            showError("Failed to load programmes");
        }
    }

    private void exportCurrentReport() {
        if (resultsTable.getItems() == null || resultsTable.getItems().isEmpty()) {
            showError("No report data to export");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file == null)
            return;

        try {
            List<Object> items = resultsTable.getItems();

            Object firstItem = items.get(0);

            if (firstItem instanceof Student) {

                CsvHandler.exportStudents(
                        items.stream()
                                .map(item -> (Student) item)
                                .toList(),
                        file.getAbsolutePath()
                );
            }

            else if (firstItem instanceof GpaBandSummary) {

                CsvHandler.exportGpaDistribution(
                        items.stream()
                                .map(item -> (GpaBandSummary) item)
                                .toList(),
                        file.getAbsolutePath()
                );
            }

            else if (firstItem instanceof ProgrammeSummary) {

                CsvHandler.exportProgrammeSummary(
                        items.stream()
                                .map(item -> (ProgrammeSummary) item)
                                .toList(),
                        file.getAbsolutePath()
                );
            }

            showSuccess("Report exported successfully");

        } catch (Exception e) {

            showError("Export failed: " + e.getMessage());
        }
    }

    public void refresh() {
        // use the threshold set in settings
        thresholdSpinner.getValueFactory().setValue(SettingsView.getAtRiskThreshold());

        updateReport();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}