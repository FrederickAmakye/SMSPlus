package com.frederickamakye.smsplus.ui;

import java.sql.SQLException;

import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.services.StudentService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class StudentsView extends BorderPane {

    private final TableView<Student> studentsTable;
    private final StudentService studentService;

    public StudentsView() {

        studentService = new StudentService();

        // ================= TOP SECTION =================
        HBox topControls = new HBox(10);
        TextField searchField = new TextField();
        searchField.setPromptText("Search students...");

        ComboBox<String> programmeFilter = new ComboBox<>();
        programmeFilter.getItems().add("Programme");
        try {
            programmeFilter.getItems().addAll(studentService.getProgrammes());
        } catch (SQLException e) {
            showError("Failed to load programmes");
        }
        programmeFilter.setPromptText("Programme");

        ComboBox<Integer> levelFilter = new ComboBox<>();
        levelFilter.getItems().add(null);
        levelFilter.getItems().addAll(100, 200, 300, 400);
        levelFilter.setPromptText("Level");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().add("Status");
        statusFilter.getItems().addAll("Active", "Inactive");
        statusFilter.setPromptText("Status");

        ComboBox<String> sortField = new ComboBox<>();
        sortField.getItems().addAll("GPA", "Full Name");
        sortField.setPromptText("Sort Field");

        ComboBox<String> sortDirection = new ComboBox<>();
        sortDirection.getItems().addAll("Ascending", "Descending");
        sortDirection.setPromptText("Order");


        topControls.getChildren().addAll(
            searchField,
            programmeFilter,
            levelFilter,
            statusFilter,
            sortField,
            sortDirection
        );

        // ================= TABLE =================
        studentsTable = new TableView<>();
        studentsTable.setPlaceholder(new Label("No student records found"));

        TableColumn<Student, String> idColumn = new TableColumn<>("Student ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> nameColumn = new TableColumn<>("Full Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<Student, String> programmeColumn = new TableColumn<>("Programme");
        programmeColumn.setCellValueFactory(new PropertyValueFactory<>("programme"));

        TableColumn<Student, Integer> levelColumn = new TableColumn<>("Level");
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));

        TableColumn<Student, Double> gpaColumn = new TableColumn<>("GPA");
        gpaColumn.setCellValueFactory(new PropertyValueFactory<>("gpa"));

        TableColumn<Student, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        studentsTable.getColumns().addAll(
                idColumn,
                nameColumn,
                programmeColumn,
                levelColumn,
                gpaColumn,
                statusColumn
        );

        // ================= BOTTOM SECTION =================
        HBox bottomControls = new HBox(10);

        Button addBtn = new Button("Add Student");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        bottomControls.getChildren().addAll(addBtn, updateBtn, deleteBtn);



        // Set up layout
        setTop(topControls);
        setCenter(studentsTable);
        setBottom(bottomControls);

        // Load table data when UI opens
        loadStudents();

        // search student by ID or name
        searchField.setOnAction(e -> performSearch(searchField.getText()));


        // sort/filter students
        sortField.setOnAction(e -> performSort(sortField, sortDirection));
        sortDirection.setOnAction(e -> performSort(sortField, sortDirection));
        programmeFilter.setOnAction(e -> performFilter(programmeFilter, levelFilter, statusFilter));
        levelFilter.setOnAction(e -> performFilter(programmeFilter, levelFilter, statusFilter));
        statusFilter.setOnAction(e -> performFilter(programmeFilter, levelFilter, statusFilter));

        // Open dialog box for adding a new user when add button us clicked
        addBtn.setOnAction(e -> {

            AddStudentDialog dialog = new AddStudentDialog();

            while (true) {

                var result = dialog.showAndWait();

                if (result.isEmpty())
                    return;

                try {

                    studentService.createStudent(result.get());

                    loadStudents();
                    return;

                } catch (Exception ex) {

                    dialog.showValidationError(ex.getMessage());
                }
            }
        });

        // Delete selected user
        deleteBtn.setOnAction(e -> {
            Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();

            if (selectedStudent == null) {
                showError("Select a student to delete");
                return;
            }

            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirm Deletion");
            confirmDialog.setContentText("STUDENT ID: %s\nAre you sure you want to delete this student?".formatted(selectedStudent.getStudentId()));

            confirmDialog.showAndWait().ifPresent(response -> {

                if (response == ButtonType.OK) {

                    try {

                        studentService.deleteStudent(selectedStudent.getStudentId());

                        loadStudents(); // refresh table

                    } catch (Exception ex) {

                        showError(ex.getMessage());
                    }
                }
            });
        });

        // Update details of selected user
        updateBtn.setOnAction(e -> {
            Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();

            if (selectedStudent == null) {
                showError("Select a student to update");
                return;
            }

            UpdateStudentDialog dialog = new UpdateStudentDialog(selectedStudent);

            while (true) {

                var result = dialog.showAndWait();

                if (result.isEmpty())
                    return;

                try {

                    studentService.updateStudent(result.get());

                    loadStudents();
                    return;

                } catch (Exception ex) {

                    dialog.showValidationError(ex.getMessage());
                }
            }
        });
    }


    // ================= TABLE LOADING =================
    private void loadStudents() {

        try {

            ObservableList<Student> students =
                    FXCollections.observableArrayList(studentService.getAllStudents());

            studentsTable.setItems(students);

        } catch (SQLException e) {

            showError("Failed to load students");
        }
    }



    private void performSearch(String query) {

        try {

            if (query == null || query.isBlank()) {
                loadStudents();
                return;
            }

            ObservableList<Student> students =
                    FXCollections.observableArrayList(studentService.searchStudents(query));

            studentsTable.setItems(students);

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }


    private void performSort(ComboBox<String> fieldBox, ComboBox<String> directionBox) {
        String field = fieldBox.getValue();
        String direction = directionBox.getValue();

        if (field == null || direction == null)
            return;

        try {

            String sortDirection = direction.equals("Descending") ? "desc" : "asc";

            ObservableList<Student> students;

            switch (field) {

                case "GPA" -> students =
                        FXCollections.observableArrayList(
                                studentService.sortByGpa(sortDirection)
                        );

                case "Full Name" -> students =
                        FXCollections.observableArrayList(
                                studentService.sortByFullName(sortDirection)
                        );

                default -> {
                    showError("Invalid sorting field");
                    return;
                }
            }

            studentsTable.setItems(students);

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }


    private void performFilter(ComboBox<String> programmeBox, ComboBox<Integer> levelBox, ComboBox<String> statusBox) {
        String programme = programmeBox.getValue();
        Integer level = levelBox.getValue();
        String status = statusBox.getValue();

        final String programmeValue;
        final Integer levelValue;
        final String statusValue;

        if (programme != null) {
            if (programme.equals("Programme")) {
                programmeValue = null;
            } else {
                programmeValue = programme;
            }
        } else {
            programmeValue = null;
        }

        if (status != null) {
            if (status.equals("Status")) {
                statusValue = null;
            } else {
                statusValue = status;
            }
        } else {
            statusValue = null;
        }

        levelValue = level;

        try {

            ObservableList<Student> students = FXCollections.observableArrayList(
                    studentService.getAllStudents()
            );

            if (programmeValue != null) {
                students = FXCollections.observableArrayList(
                        studentService.filterByProgramme(programmeValue)
                );
            }

            if (levelValue != null) {
                students = FXCollections.observableArrayList(
                        students.stream()
                                .filter(s -> s.getLevel() == levelValue)
                                .toList()
                );
            }

            if (statusValue != null) {
                students = FXCollections.observableArrayList(
                        students.stream()
                                .filter(s -> s.getStatus().equalsIgnoreCase(statusValue))
                                .toList()
                );
            }

            studentsTable.setItems(students);

        } catch (Exception e) {

            showError(e.getMessage());
        }
    }

    public void refresh() {
        loadStudents();
    }

    private void showError(String message) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }
}