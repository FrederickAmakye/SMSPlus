package com.frederickamakye.smsplus.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.services.StudentService;

public class CsvHandler {

    private static final Logger logger = LoggerFactory.getLogger(CsvHandler.class);

    public static void exportStudents(List<Student> students, String filePath) throws IOException {
        
        //Create a new file with the provided path 
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("student_id", "full_name", "programme", "level", "gpa", "email", "phone", "status"))) {
            
            // write each student record as csv
            for (Student s : students) {

                csvPrinter.printRecord(
                        s.getStudentId(),
                        s.getFullName(),
                        s.getProgramme(),
                        s.getLevel(),
                        s.getGpa(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getStatus()
                );
            }

            logger.info("Exported {} students to CSV", students.size());

        } catch (IOException e) {

            logger.error("CSV export failed", e);
            throw e;
        }
    }

    public static void importStudents(String filePath, StudentService studentService)
            throws IOException, SQLException {
        
        // open provided csv file and parse content
        // TODO: refactor for bulk db insert using transactions (to limit IO overhead)
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord record : csvParser) {

                try {
                    // validate and create student record for each line in the csv file
                    Student student = new Student(
                            record.get("student_id"),
                            record.get("full_name"),
                            record.get("programme"),
                            Integer.parseInt(record.get("level")),
                            Double.parseDouble(record.get("gpa"))
                    );

                    student.setEmail(record.get("email"));
                    student.setPhone(record.get("phone"));
                    student.setStatus(record.get("status"));

                    studentService.createStudent(student);

                } catch (RuntimeException e) {
                    // if validation fails or another exception occures skip the line
                    logger.warn("Skipped invalid CSV row: {}", record);
                }
            }

            logger.info("CSV import completed");

        } catch (IOException e) {

            logger.error("CSV import failed", e);
            throw e;
        }
    }
}