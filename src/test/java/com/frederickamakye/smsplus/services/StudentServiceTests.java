package com.frederickamakye.smsplus.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.frederickamakye.smsplus.exceptions.ValidationException;
import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.utils.Database;

class StudentServiceTest {

    private StudentService studentService;

    @BeforeEach
    void setup() throws SQLException {

        System.setProperty("db.url", "jdbc:sqlite:data/test.db");
        Database.init();
        studentService = new StudentService();

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM students");
        }
    }


    @Test
    void mustGenerateStudentIdIfMissing() throws SQLException {

        Student student = new Student(null, "John Doe", "B.Tech Electrical Engineering", 100, 3.2);

        studentService.createStudent(student);

        assertNotNull(student.getStudentId());
    }


    @Test
    void mustRejectInvalidStudent() {

        Student student = new Student(null, "Bad GPA", "B.Tech Electrical Engineering", 100, 5.5);

        try {

            studentService.createStudent(student);
            fail("Validation should have failed");

        } catch (ValidationException e) {

            assertEquals("GPA must be between 0.0 and 4.0", e.getMessage());

        } catch (SQLException e) {
            fail("Unexpected database exception");
        }
    }


    @Test
    void mustCreateStudentThroughService() throws SQLException {

        Student student = new Student(null, "Service Student", "B.Tech Electrical Engineering", 200, 3.5);

        studentService.createStudent(student);

        Student found = studentService.getStudentById(student.getStudentId());

        assertNotNull(found);
        assertEquals(student.getFullName(), found.getFullName());
    }


    @Test
    void mustSearchStudents() throws SQLException {

        studentService.createStudent(new Student(null, "John Doe", "B.Tech Electrical Engineering", 100, 3.0));
        studentService.createStudent(new Student(null, "Jane Smith", "B.Tech Electrical Engineering", 100, 3.8));

        List<Student> results = studentService.searchStudents("john");

        assertEquals(1, results.size());
    }


    @Test
    void mustRejectEmptySearchQuery() {

        try {

            studentService.searchStudents("");
            fail("Search validation should have failed");

        } catch (ValidationException e) {

            assertEquals("Search query cannot be empty", e.getMessage());

        } catch (SQLException e) {
            fail("Unexpected database exception");
        }
    }


    @Test
    void mustSortStudentsByGpa() throws SQLException {

        Student low = new Student(null, "Low GPA", "B.Tech Electrical Engineering", 100, 2.1);
        Student high = new Student(null, "High GPA", "B.Tech Electrical Engineering", 100, 3.9);

        studentService.createStudent(low);
        studentService.createStudent(high);

        List<Student> students = studentService.sortByGpa("desc");

        assertEquals(high.getStudentId(), students.get(0).getStudentId());
    }
}