package com.frederickamakye.smsplus.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

import com.frederickamakye.smsplus.exceptions.ValidationException;
import com.frederickamakye.smsplus.models.Student;

class StudentValidatorTests {

    @Test
    void mustPassValidationForValidStudent() {

        Student student = new Student(StudentIdGenerator.generate(), "John Doe", "B.Tech Electrical Engineering", 400, 3.5);
        student.setEmail("valid@gmail.com");

        try {
            StudentValidator.validate(student);

        } catch (ValidationException e) {
            // If you reach here something went wrong since the student record is valid. So test is failed manually.
            fail("Check the test: Validation should not have failed");
        }
    }


    @Test
    void mustRejectInvalidGpa() {

        Student student = new Student(StudentIdGenerator.generate(), "John Doe", "B.Tech Electrical Engineering", 400, 5.5);

        try {
            StudentValidator.validate(student);

            // validation is expected to fail. You should not reach here.
            fail("Check the test: Validation should have failed");

        } catch (ValidationException e) {
            assertEquals("GPA must be between 0.0 and 4.0", e.getMessage());
        }
    }


    @Test
    void mustRejectMissingStudentId() {

        Student student = new Student(null, "John Doe", "B.Tech Electrical Engineering", 200, 3.5);

        try {
            StudentValidator.validate(student);
            fail("Check the test: Validation should have failed");

        } catch (ValidationException e) {
            assertEquals("Student ID is required", e.getMessage());
        }
    }


    @Test
    void mustRejectBlankName() {

        Student student = new Student(StudentIdGenerator.generate(), "   ", "B.Tech Electrical Engineering", 100, 3.7);

        try {
            StudentValidator.validate(student);
            fail("Check the test: Validation should have failed");

        } catch (ValidationException e) {
            assertEquals("Full name is required", e.getMessage());
        }
    }


    @Test
    void mustRejectInvalidEmail() {

        Student student = new Student(StudentIdGenerator.generate(), "Email Test", "B.Tech Electrical Engineering", 100, 3.0);
        student.setEmail("invalid@gmail");

        try {
            StudentValidator.validate(student);
            fail("Check the test: Validation should have failed");

        } catch (ValidationException e) {
            assertEquals("Invalid email format", e.getMessage());
        }
    }
}