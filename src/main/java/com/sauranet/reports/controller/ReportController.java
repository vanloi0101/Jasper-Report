package com.sauranet.reports.controller;

import com.sauranet.reports.entities.Student;
import com.sauranet.reports.services.StudentService;
import com.sauranet.reports.utils.ReportUtil;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ReportController {

    private final StudentService studentService;

    @GetMapping("/student/{rollNumber}")
    public ResponseEntity<?> generateStudentReport(
            @PathVariable(name = "rollNumber") Integer rollNumber,
            @RequestParam(name = "format", defaultValue = "pdf") String format
    ) throws IOException, JRException {
        try {
            // Fetch student data
            Student student = studentService.getStudentById(rollNumber);
            List<Student> students = List.of(student);
            String jasperPath = new File("src/main/resources/reports/StudentScoreCard.jasper").getAbsolutePath();

            // Parameters and data source
            HashMap<String, Object> parameters = new HashMap<>();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(students.get(0).getScoreCards());
            parameters.put("TABLE_DATA_SOURCE", dataSource);

            // Generate JasperPrint
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, parameters, new JRBeanCollectionDataSource(students));

            // Export based on format
            byte[] reportData;
            String fileName;
            String contentType;

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                if ("xlsx".equalsIgnoreCase(format)) {
                    JRXlsExporter exporter = new JRXlsExporter();
                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
                    exporter.exportReport();
                    reportData = outputStream.toByteArray();
                    fileName = "student_report.xlsx";
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                } else { // Default to PDF
                    JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                    reportData = outputStream.toByteArray();
                    fileName = "student_report.pdf";
                    contentType = "application/pdf";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(reportData);
            }
        } catch (RuntimeException e) {
            // Xử lý khi không tìm thấy student
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Student not found with ID: " + rollNumber);
        } catch (Exception e) {
            // Xử lý các lỗi khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating report: " + e.getMessage());
        }
    }
}