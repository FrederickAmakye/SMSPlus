/*
 * StudentRepository takes care of activities related to student data. Such as:
 * - Execution of CRUD operations
 * - Mapping student database rows to Student objects
*/

package com.frederickamakye.smsplus.repository;

import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.models.GpaBandSummary;
import com.frederickamakye.smsplus.models.ProgrammeSummary;
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


    // Search for students using id or full name
    public List<Student> search(String query) throws SQLException {
        String sql = """
            SELECT * FROM students
            WHERE student_id LIKE ?
            OR LOWER(full_name) LIKE LOWER(?)
        """;

        List<Student> students = new ArrayList<>();

        try (Connection conn = Database.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + query + "%";

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet resultset = stmt.executeQuery()) {

                while (resultset.next()) {
                    students.add(toObject(resultset));
                }
            }

        } catch (SQLException e) {

            logger.error("Search query failed", e);
            throw new RepositoryException("Search failed", e);
        }

        return students;
    }
    

    // Sort records by specific field (gpa, fullname, or level)
    public List<Student> sortBy(String field, String direction) throws SQLException {

        String column;

        switch (field.toLowerCase()) {
            case "gpa" -> column = "gpa";
            case "fullname" -> column = "full_name";
            case "level" -> column = "level";
            default -> throw new IllegalArgumentException("Invalid sorting field");
        }

        String order = "ASC";
        if ("desc".equalsIgnoreCase(direction)) {
            order = "DESC";
        }

        String sql = "SELECT * FROM students ORDER BY " + column + " " + order;

        List<Student> students = new ArrayList<>();

        try (Connection conn = Database.connect();
            Statement stmt = conn.createStatement();
            ResultSet resultset = stmt.executeQuery(sql)) {

            while (resultset.next()) {
                students.add(toObject(resultset));
            }

        } catch (SQLException e) {

            logger.error("Sorting failed", e);
            throw new RepositoryException("Sorting failed", e);
        }

        return students;
    }


    // Get list of 10 top performers (students with highest gpa). Optional filtering by programme or level
    public List<Student> getTopPerformers(String programme, Integer level) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT * FROM students
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (programme != null) {
            sql.append(" AND LOWER(programme) = LOWER(?)");
            params.add(programme);
        }

        if (level != null) {
            sql.append(" AND level = ?");
            params.add(level);
        }

        sql.append(" ORDER BY gpa DESC LIMIT 10");

        List<Student> students = new ArrayList<>();

        try (Connection conn = Database.connect();
            PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++)
                stmt.setObject(i + 1, params.get(i));

            try (ResultSet resultset = stmt.executeQuery()) {
                while (resultset.next())
                    students.add(toObject(resultset));
            }

        } catch (SQLException e) {

            logger.error("Top performers report failed", e);
            throw new RepositoryException("Report failed", e);
        }

        return students;
    }


    // Get at risk students (students with gpa below specific threshold)
    public List<Student> getAtRiskStudents(double threshold) throws SQLException {

        String sql = "SELECT * FROM students WHERE gpa < ? ORDER BY gpa ASC";

        List<Student> students = new ArrayList<>();

        try (Connection conn = Database.connect();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, threshold);

            try (ResultSet resultset = stmt.executeQuery()) {
                while (resultset.next()) {
                    students.add(toObject(resultset));
                }
            }

        } catch (SQLException e) {

            logger.error("Failed to generate at risk students report", e);
            throw new RepositoryException("Report generation failed", e);
        }

        return students;
    }


    // Get gpa band distribution (how many students fall into each GPA band)
    public List<GpaBandSummary> getGpaDistribution() throws SQLException {
        String sql = """
            SELECT
                CASE
                    WHEN gpa < 2.0 THEN 'Below 2.0'
                    WHEN gpa < 3.0 THEN '2.0 - 2.99'
                    WHEN gpa < 3.7 THEN '3.0 - 3.69'
                    ELSE '3.7 - 4.0'
                END AS band,
                COUNT(*) AS total
            FROM students
            GROUP BY band
        """;

        List<GpaBandSummary> results = new ArrayList<>();

        try (Connection conn = Database.connect();
            Statement stmt = conn.createStatement();
            ResultSet resultset = stmt.executeQuery(sql)) {

            while (resultset.next())
                results.add(new GpaBandSummary(
                    resultset.getString("band"),
                    resultset.getInt("total")
                ));

        } catch (SQLException e) {

            logger.error("GPA distribution failed", e);
            throw new RepositoryException("Report failed", e);
        }

        return results;
    }


    // Get programme summary (total students per programme and average GPA per programme)
    public List<ProgrammeSummary> getProgrammeSummary() throws SQLException {
        String sql = """
            SELECT programme,
                COUNT(*) as total,
                AVG(gpa) as averageGpa
            FROM students
            GROUP BY programme
        """;

        List<ProgrammeSummary> results = new ArrayList<>();

        try (Connection conn = Database.connect();
            Statement stmt = conn.createStatement();
            ResultSet resultset = stmt.executeQuery(sql)) {

            while (resultset.next())
                results.add(new ProgrammeSummary(
                    resultset.getString("programme"),
                    resultset.getInt("total"),
                    resultset.getDouble("averageGpa")
                ));

        } catch (SQLException e) {

            logger.error("Programme summary failed", e);
            throw new RepositoryException("Report failed", e);
        }

        return results;
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