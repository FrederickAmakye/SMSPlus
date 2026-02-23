/*
 * StudentService takes care of activities related to student business logic. 
*/


package com.frederickamakye.smsplus.services;

import java.sql.SQLException;
import java.util.List;

import com.frederickamakye.smsplus.exceptions.ValidationException;
import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.models.GpaBandSummary;
import com.frederickamakye.smsplus.models.ProgrammeSummary;
import com.frederickamakye.smsplus.repository.StudentRepository;
import com.frederickamakye.smsplus.utils.StudentIdGenerator;
import com.frederickamakye.smsplus.utils.StudentValidator;

public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService() {
        this.studentRepository = new StudentRepository();
    }

    // Create new student 
    public void createStudent(Student student) throws SQLException {

        // Generate new ID if missing
        if (student.getStudentId() == null) {
            student.setStudentId(StudentIdGenerator.generate());
        }

        StudentValidator.validate(student);

        studentRepository.create(student);
    }

    public void updateStudent(Student student) throws SQLException {

        StudentValidator.validate(student);
        studentRepository.update(student);
    }

    public void deleteStudent(String id) throws SQLException {

        studentRepository.delete(id);
    }

    public Student getStudentById(String id) throws SQLException {

        return studentRepository.getById(id);
    }

    public List<Student> getAllStudents() throws SQLException {

        return studentRepository.getAll();
    }   



    // Searching and sorting logic
    public List<Student> searchStudents(String query) throws SQLException {
        if (query == null || query.isBlank()) {
            throw new ValidationException("Search query cannot be empty");
        }

        return studentRepository.search(query);
    }

    public List<Student> sortByGpa(String direction) throws SQLException {
        return studentRepository.sortBy("gpa", direction);
    }

    public List<Student> sortByFullName(String direction) throws SQLException {
        return studentRepository.sortBy("fullname", direction);
    }



    // Reporting logic
    public List<GpaBandSummary> getGpaDistributionReport() throws SQLException {
        return studentRepository.getGpaDistribution();
    }

    public List<Student> getTopPerformersReport(String programme, Integer level) throws SQLException {
        return studentRepository.getTopPerformers(programme, level);
    }

    public List<Student> getAtRiskStudentsReport(double threshold) throws SQLException {
        return studentRepository.getAtRiskStudents(threshold);
    }

    public List<ProgrammeSummary> getProgrammeSummaryReport() throws SQLException {
        return studentRepository.getProgrammeSummary();
    }
}