package com.frederickamakye.smsplus.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.frederickamakye.smsplus.models.GpaBandSummary;
import com.frederickamakye.smsplus.models.ProgrammeSummary;
import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.utils.Database;

class ReportsTests {

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

        // seed testing dataset
        studentService.createStudent(new Student(null, "Student A", "B.Tech IT", 100, 3.9));
        studentService.createStudent(new Student(null, "Student B", "B.Tech IT", 100, 3.5));
        studentService.createStudent(new Student(null, "Student C", "B.Tech Computer Engineering", 200, 2.8));
        studentService.createStudent(new Student(null, "Student D", "B.Tech Computer Engineering", 200, 1.5));
        studentService.createStudent(new Student(null, "Student E", "B.Tech Electrical Engineering", 300, 2.0));
    }

    @Test
    void mustReturnTopPerformers() throws SQLException {

        List<Student> top = studentService.getTopPerformersReport(null, null);

        assertFalse(top.isEmpty());

        for (int i = 0; i < top.size() - 1; i++) {
            // GPA of students must be in descreasing order
            assertTrue(top.get(i).getGpa() >= top.get(i + 1).getGpa());
        }
    }

    @Test
    void mustReturnAtRiskStudents() throws SQLException {

        List<Student> risk = studentService.getAtRiskStudentsReport(2.0);

        assertFalse(risk.isEmpty());

        // each student must have gpa less than 2.0
        for (Student student : risk) {
            assertTrue(student.getGpa() < 2.0);
        }
    }


    @Test
    void mustReturnGpaDistribution() throws SQLException {

        List<GpaBandSummary> distribution = studentService.getGpaDistributionReport();

        assertNotNull(distribution);

        // 4 gpa bands expected 
        assertEquals(4, distribution.size()); 
    }


    @Test
    void mustReturnProgrammeSummary() throws SQLException {

        List<ProgrammeSummary> summary = studentService.getProgrammeSummaryReport();

        assertNotNull(summary);

        // 3 programmes in seed data so 3 summeries expected
        assertEquals(3, summary.size());  
    }
}