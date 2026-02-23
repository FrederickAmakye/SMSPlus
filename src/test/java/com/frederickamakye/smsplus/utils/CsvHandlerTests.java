package com.frederickamakye.smsplus.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.services.StudentService;

class CsvHandlerTests {

    private StudentService studentService;
    private static final String TEST_CSV = "data/test_students.csv";

    @BeforeEach
    void setup() throws SQLException {

        System.setProperty("db.url", "jdbc:sqlite:data/test.db");
        Database.init();
        studentService = new StudentService();

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM students");
        }

        // seed testing dataset using service
        studentService.createStudent(new Student(null, "Student A", "B.Tech IT", 100, 3.9));
        studentService.createStudent(new Student(null, "Student B", "B.Tech IT", 100, 3.5));
    }


    @Test
    void mustExportStudentsToCsv() throws IOException, SQLException {

        List<Student> students = studentService.getAllStudents();

        CsvHandler.exportStudents(students, TEST_CSV);

        File file = new File(TEST_CSV);

        assertTrue(file.exists());
    }


    @Test
    void mustImportStudentsFromCsv() throws IOException, SQLException {

        // export the student records in the db into csv
        List<Student> students = studentService.getAllStudents();

        CsvHandler.exportStudents(students, TEST_CSV);

        // clear DB before import
        for (Student s : students) {
            studentService.deleteStudent(s.getStudentId());
        }

        // import the exported data
        CsvHandler.importStudents(TEST_CSV, studentService);

        List<Student> imported = studentService.getAllStudents();

        // total imported data must be the same number as the seeded data in db
        assertEquals(2, imported.size());
    }


   @Test
    void mustSkipInvalidRows() throws IOException, SQLException {

        // reset db to isolate this test. Because the db is already seeded with 2 records before this test runs.
        try (Connection conn = Database.connect();
            Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM students");
        }

        CsvHandler.exportStudents(
                List.of(
                        new Student("X001", "Valid Student", "B.Tech IT", 100, 3.2),
                        new Student("X002", "Bad GPA", "B.Tech IT", 100, 9.9)
                ),
                TEST_CSV
        );

        CsvHandler.importStudents(TEST_CSV, studentService);

        List<Student> students = studentService.getAllStudents();

        // only 1 valid student expected
        assertEquals(1, students.size());
    }
}