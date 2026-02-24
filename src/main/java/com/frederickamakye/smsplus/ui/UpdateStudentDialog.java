package com.frederickamakye.smsplus.ui;

import com.frederickamakye.smsplus.models.Student;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class UpdateStudentDialog extends Dialog<Student> {

    private final Label errorLabel;

    public UpdateStudentDialog(Student student) {

        setTitle("Update Student");

        TextField nameField = new TextField(student.getFullName());
        TextField programmeField = new TextField(student.getProgramme());

        ComboBox<Integer> levelField = new ComboBox<>();
        levelField.getItems().addAll(100, 200, 300, 400);
        levelField.setValue(student.getLevel());

        TextField gpaField = new TextField(String.valueOf(student.getGpa()));
        TextField emailField = new TextField(student.getEmail());
        TextField phoneField = new TextField(student.getPhone());

        ComboBox<String> statusField = new ComboBox<>();
        statusField.getItems().addAll("Active", "Inactive");
        statusField.setValue(student.getStatus());

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");


        VBox layout = new VBox(10,
                errorLabel,
                nameField,
                programmeField,
                levelField,
                gpaField,
                emailField,
                phoneField,
                statusField
        );

        getDialogPane().setContent(layout);

        getDialogPane().getButtonTypes().addAll(
                ButtonType.OK,
                ButtonType.CANCEL
        );

        Button okButton = (Button) getDialogPane().lookupButton(ButtonType.OK);

        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {

            try {

                if (nameField.getText().isBlank()) {
                    throw new IllegalArgumentException("Full Name is required");
                }

                if (programmeField.getText().isBlank()) {
                    throw new IllegalArgumentException("Programme is required");
                }

                if (levelField.getValue() == null) {
                    throw new IllegalArgumentException("Level is required");
                }

                if (gpaField.getText().isBlank()) {
                    throw new IllegalArgumentException("GPA is required");
                }

                if (statusField.getValue() == null) {
                    throw new IllegalArgumentException("Status is required");
                }

                double gpa;

                try {
                    gpa = Double.parseDouble(gpaField.getText());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid GPA value");
                }

                student.setFullName(nameField.getText());
                student.setProgramme(programmeField.getText());
                student.setLevel(levelField.getValue());
                student.setGpa(gpa);
                student.setEmail(emailField.getText());
                student.setPhone(phoneField.getText());
                student.setStatus(statusField.getValue());
                
                setResult(student);

            } catch (IllegalArgumentException e) {

                errorLabel.setText(e.getMessage());
                event.consume();
            }
        });

        setResultConverter(button -> {

            if (button == ButtonType.OK)
                return getResult();

            return null;
        });
    }

    public void showValidationError(String message) {
        errorLabel.setText(message);
    }
}