package com.frederickamakye.smsplus.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.utils.Database;
import com.frederickamakye.smsplus.utils.StudentIdGenerator;

class StudentRepositoryTests {

    private StudentRepository studentRepository;

    @BeforeEach
    void setup() throws SQLException {
        // Configure test database
        System.setProperty("db.url", "jdbc:sqlite:data/test.db");
        Database.init();
        studentRepository = new StudentRepository();

        // Reset database before each test begins
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM students");
        }
    }


    @Test
    void mustCreateStudent() throws SQLException {

        Student student = new Student(StudentIdGenerator.generate(), "John Doe", "B.Tech Electrical Engineering", 400, 3.5);

        studentRepository.create(student);

        Student found = studentRepository.getById(student.getStudentId());

        // check student was created
        assertNotNull(found);
        // check the student created is the same as the one provided
        assertEquals(student.getFullName(), found.getFullName());
    }


    @Test
    void mustUpdateStudent() throws SQLException {

        Student student = new Student(StudentIdGenerator.generate(), "John Doe", "B.Tech Electrical Engineering", 400, 3.5);

        studentRepository.create(student);

        student.setFullName("Micheal Jackson");
        student.setGpa(4.9);

        studentRepository.update(student);

        Student updated = studentRepository.getById(student.getStudentId());

        // check updated record exist
        assertNotNull(updated);

        // check updated data is same as what's in db
        assertEquals("Micheal Jackson", updated.getFullName());
        assertEquals(4.9, updated.getGpa());
    }


    @Test
    void mustDeleteStudent() throws SQLException {

        Student student = new Student(StudentIdGenerator.generate(), "Anonymous", "B.Tech Electrical Engineering", 400, 3.5);

        studentRepository.create(student);
        studentRepository.delete(student.getStudentId());

        Student found = studentRepository.getById(student.getStudentId());

        // check student record is not in db
        assertNull(found);
    }


    @Test
    void mustReturnAllStudents() throws SQLException {

        Student student1 = new Student(StudentIdGenerator.generate(), "Anonymous 1", "B.Tech Electrical Engineering", 300, 3.3);
        Student student2 = new Student(StudentIdGenerator.generate(), "Anonymous 2", "B.Tech Electrical Engineering", 200, 2.9);

        studentRepository.create(student1);
        studentRepository.create(student2);

        List<Student> students = studentRepository.getAll();

        // check 2 student records are in db which is the total records in the db
        assertEquals(2, students.size());
    }


    @Test
    void mustSearchStudentsByName() throws SQLException {

        Student student1 = new Student(StudentIdGenerator.generate(), "John Doe", "B.Tech Electrical Engineering", 100, 3.0);
        Student student2 = new Student(StudentIdGenerator.generate(), "Jane Smith", "B.Tech Electrical Engineering", 100, 3.5);

        studentRepository.create(student1);
        studentRepository.create(student2);

        List<Student> results = studentRepository.search("john");

        // 1 student has name john so check 1 record is returned
        assertEquals(1, results.size());
    }


    @Test
    void mustSortStudentsByGpaDescending() throws SQLException {

        Student low = new Student(StudentIdGenerator.generate(), "Student One", "B.Tech Electrical Engineering", 100, 2.0);
        Student high = new Student(StudentIdGenerator.generate(), "Student Two", "B.Tech Electrical Engineering", 100, 3.8);

        studentRepository.create(low);
        studentRepository.create(high);

        List<Student> students = studentRepository.sortBy("gpa", "desc");

        // since records were sorted by gpa in descending order check if the last record(high) is now the first record
        assertEquals(high.getStudentId(), students.get(0).getStudentId());
    }
}