package com.frederickamakye.smsplus.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.io.File;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.frederickamakye.smsplus.models.Student;
import com.frederickamakye.smsplus.models.GpaBandSummary;
import com.frederickamakye.smsplus.models.ProgrammeSummary;
import com.frederickamakye.smsplus.models.ImportResult;
import com.frederickamakye.smsplus.services.StudentService;

public class CsvHandler {

    private static final Logger logger = LoggerFactory.getLogger(CsvHandler.class);

    public static void exportStudents(List<Student> students, String filePath) throws IOException {
        
        // Create parent dir if it does not exist
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
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

    public static ImportResult importStudents(String filePath, StudentService studentService) throws IOException {
        ImportResult result = new ImportResult();
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

                    result.incrementSuccess();

                } catch (Exception e) {

                    result.incrementError(record.toString());
                    // if validation fails or another exception occures skip the line
                    logger.warn("Skipped invalid CSV line: {}", record);
                }
            }

        } catch (IOException e) {

            logger.error("CSV import failed", e);
            throw e;
        }

        return result;
    }

    public static void exportGpaDistribution(List<GpaBandSummary> data, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("band", "total_students"))) {

            for (GpaBandSummary row : data) {
                csvPrinter.printRecord(
                        row.getBand(),
                        row.getTotalStudents()
                );
            }
        }
    }

    public static void exportProgrammeSummary(List<ProgrammeSummary> data, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("programme", "total_students", "average_gpa"))) {

            for (ProgrammeSummary row : data) {
                csvPrinter.printRecord(
                        row.getProgramme(),
                        row.getTotalStudents(),
                        row.getAverageGpa()
                );
            }
        }
    }


    public static void exportImportErrors(List<String> errors, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withHeader("error_row"))) {

            for (String row : errors) {
                csvPrinter.printRecord(row);
            }
        }
    }
}