package com.frederickamakye.smsplus.ui;

import java.io.File;

import com.frederickamakye.smsplus.models.ImportResult;
import com.frederickamakye.smsplus.services.StudentService;
import com.frederickamakye.smsplus.utils.CsvHandler;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ImportExportView extends VBox {

    private final StudentService studentService;

    private final Label successLabel;
    private final Label errorLabel;

    public ImportExportView() {

        studentService = new StudentService();

        Label header = new Label("Import / Export");
        header.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // ================= IMPORT SECTION =================

        Button importBtn = new Button("Import CSV");

        successLabel = new Label("Success: 0");
        errorLabel = new Label("Errors: 0");

        VBox importSection = new VBox(5,
                importBtn,
                successLabel,
                errorLabel
        );

        // ================= EXPORT SECTION =================

        Label exportHeader = new Label("Export Options");
        exportHeader.setStyle("-fx-font-weight: bold;");

        Button exportBtn = new Button("Export Students");

        VBox exportSection = new VBox(5,
                exportHeader,
                exportBtn
        );

        setSpacing(15);
        setStyle("-fx-padding: 20;");

        getChildren().addAll(
                header,
                importSection,
                new Separator(),
                exportSection
        );

        // ================= EVENTS =================

        importBtn.setOnAction(e -> importStudents());
        exportBtn.setOnAction(e -> exportStudents());
    }

    // ================= IMPORT LOGIC =================

    private void importStudents() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Students");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showOpenDialog(getScene().getWindow());

        if (file == null)
            return;

        try {

            ImportResult result = CsvHandler.importStudents(file.getAbsolutePath(), studentService);

            successLabel.setText("Success: " + result.getSuccessCount());
            errorLabel.setText("Errors: " + result.getErrorCount());

            showSuccess("Import completed");

        } catch (Exception e) {

            showError("Import failed: " + e.getMessage());
        }
    }

    // ================= EXPORT LOGIC =================

    private void exportStudents() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Students");

        File defaultDir = new File("data");

        if (!defaultDir.exists()) {
            defaultDir.mkdirs();
        }

        fileChooser.setInitialDirectory(defaultDir);

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );

        File file = fileChooser.showSaveDialog(getScene().getWindow());

        if (file == null) {
            return;
        }

        try {

            CsvHandler.exportStudents(
                    studentService.getAllStudents(),
                    file.getAbsolutePath()
            );

            showSuccess("Export completed successfully");

        } catch (Exception e) {

            showError("Export failed: " + e.getMessage());
        }
    }
    // ================= DIALOG HELPERS =================

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