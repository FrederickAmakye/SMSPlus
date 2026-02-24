package com.frederickamakye.smsplus.ui;

import com.frederickamakye.smsplus.models.Student;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AddStudentDialog extends Dialog<Student> {
    private final Label errorLabel;

    public AddStudentDialog() {

        setTitle("Add Student");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField programmeField = new TextField();
        programmeField.setPromptText("Programme");

        ComboBox<Integer> levelField = new ComboBox<>();
        levelField.getItems().addAll(100, 200, 300, 400);
        levelField.setPromptText("Level");

        TextField gpaField = new TextField();
        gpaField.setPromptText("GPA");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        VBox layout = new VBox(10,
                errorLabel,
                nameField,
                programmeField,
                levelField,
                gpaField,
                emailField,
                phoneField
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

                double gpa;

                try {
                    gpa = Double.parseDouble(gpaField.getText());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid GPA value");
                }

                Student student = new Student(
                        null,
                        nameField.getText(),
                        programmeField.getText(),
                        levelField.getValue(),
                        gpa
                );

                student.setEmail(emailField.getText());
                student.setPhone(phoneField.getText());

                setResult(student);

            } catch (IllegalArgumentException e) {
                errorLabel.setText(e.getMessage());
                event.consume();
            }
        });

        setResultConverter(button -> {

            if (button == ButtonType.OK) {
                return getResult(); // Return Student you already set
            }

            return null;
        });
    }

    public void showValidationError(String message) {
        errorLabel.setText(message);
    }
}