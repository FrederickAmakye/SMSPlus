/*
 * StudentRepository takes care of all activities related to student records. Such as:
 * - Execution of CRUD operations
 * - Mapping student database rows to Student objects
*/

package com.frederickamakye.smsplus.repository;

import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.utils.Database;
import com.frederickamakye.smsplus.exceptions.RepositoryException;
import com.frederickamakye.smsplus.exceptions.DuplicateStudentException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudentRepository {

    private static final Logger logger = LoggerFactory.getLogger(StudentRepository.class);

    // Save a new student record into the database.
    public void create(Student student) throws SQLException {
        String sql = """
            INSERT INTO students
            (student_id, full_name, programme, level, gpa, email, phone, date_added, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getStudentId());
            stmt.setString(2, student.getFullName());
            stmt.setString(3, student.getProgramme());
            stmt.setInt(4, student.getLevel());
            stmt.setDouble(5, student.getGpa());
            stmt.setString(6, student.getEmail());
            stmt.setString(7, student.getPhone());
            stmt.setString(8, student.getDateAdded());
            stmt.setString(9, student.getStatus());

            stmt.executeUpdate();

            logger.info("Created new student: {}", student.getStudentId());

        } catch (SQLException e) {

            logger.error("Failed to create new student", e);

            if (e.getMessage().contains("PRIMARY KEY"))
                throw new DuplicateStudentException("Student ID already exists", e);

            throw new RepositoryException("Failed to create new student", e);
        }
    }

    // Update the records of an existing student in the database
    public void update(Student student) throws SQLException {
        String sql = """
            UPDATE students
            SET full_name = ?, programme = ?, level = ?, gpa = ?,
                email = ?, phone = ?, date_added = ?, status = ?
            WHERE student_id = ?
        """;

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getFullName());
            stmt.setString(2, student.getProgramme());
            stmt.setInt(3, student.getLevel());
            stmt.setDouble(4, student.getGpa());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getPhone());
            stmt.setString(7, student.getDateAdded());
            stmt.setString(8, student.getStatus());
            stmt.setString(9, student.getStudentId());

            stmt.executeUpdate();

            logger.info("Updated student: {}", student.getStudentId());

        } catch (SQLException e) {

            logger.error("Failed to update student: %s".formatted(student.getStudentId()), e);
            throw new RepositoryException("Failed to update student: %s".formatted(student.getStudentId()), e);
        }
    }

    // Get all student records from the database.
    // Returns array of student objects
    public List<Student> getAll() throws SQLException {

        String sql = "SELECT * FROM students";

        List<Student> students = new ArrayList<>();

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet resultset = stmt.executeQuery(sql)) {

            while (resultset.next()) {
                students.add(toObject(resultset));
            }

            logger.info("Found students: {}", students.size());

        } catch (SQLException e) {

            logger.error("Failed to get all students", e);
            throw new RepositoryException("Failed to get all students", e);
        }

        return students;
    }

    // Get a specific student record from db with student's id
    // Returns student object or null
    public Student getById(String id) throws SQLException {

        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);

            try (ResultSet resultset = stmt.executeQuery()) {
                if (resultset.next()) {
                    return toObject(resultset);
                }
            }

        } catch (SQLException e) {

            logger.error("Failed to get student with id %s".formatted(id), e);
            throw new RepositoryException("Failed to get student with id %s".formatted(id), e);
        }

        return null;
    }

    // Delete a delete a student record from db with student's id
    public void delete(String id) throws SQLException {

        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            ps.executeUpdate();

            logger.info("Deleted student: {}", id);

        } catch (SQLException e) {

            logger.error("Failed to delete student with id %s".formatted(id), e);
            throw new RepositoryException("Failed to delete student with id %s".formatted(id), e);
        }
    }

    // Map database student records to Student objects
    private Student toObject(ResultSet resultset) throws SQLException {

        Student student = new Student();

        student.setStudentId(resultset.getString("student_id"));
        student.setFullName(resultset.getString("full_name"));
        student.setProgramme(resultset.getString("programme"));
        student.setLevel(resultset.getInt("level"));
        student.setGpa(resultset.getDouble("gpa"));
        student.setEmail(resultset.getString("email"));
        student.setPhone(resultset.getString("phone"));
        student.setDateAdded(resultset.getString("date_added"));
        student.setStatus(resultset.getString("status"));

        return student;
    }
}