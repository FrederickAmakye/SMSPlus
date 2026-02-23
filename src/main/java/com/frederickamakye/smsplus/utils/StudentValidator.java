package com.frederickamakye.smsplus.utils;

import com.frederickamakye.smsplus.exceptions.ValidationException;
import com.frederickamakye.smsplus.models.Student;


public class StudentValidator {

    public static void validate(Student student) {

        if (student == null){
            throw new ValidationException("Student cannot be null");
        }

        if (isBlank(student.getStudentId())) {
            throw new ValidationException("Student ID is required");
        }
            

        if (isBlank(student.getFullName())){
            throw new ValidationException("Full name is required");
        }
            

        if (isBlank(student.getProgramme())) {
            throw new ValidationException("Programme is required");
        }
            

        if (student.getLevel() <= 0) {
            throw new ValidationException("Invalid level");
        }
            

        if (student.getGpa() < 0.0 || student.getGpa() > 4.0) {
            throw new ValidationException("GPA must be between 0.0 and 4.0");
        }
            

        if (!isBlank(student.getEmail()) && (!student.getEmail().contains("@") || !student.getEmail().contains("."))) {
            throw new ValidationException("Invalid email format");
        }
            
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}