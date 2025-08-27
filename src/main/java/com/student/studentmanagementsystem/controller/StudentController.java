package com.student.studentmanagementsystem.controller;


import com.student.studentmanagementsystem.dto.StudentRequest;
import com.student.studentmanagementsystem.dto.StudentResponse;
import com.student.studentmanagementsystem.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // create Student
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse createdStudent = studentService.createStudent(studentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }
    // update Student
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse updatedStudent = studentService.updateStudent(id, studentRequest);
        return ResponseEntity.ok(updatedStudent);
    }
    // get Student by id
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        StudentResponse student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }
    // get all Students with optional subjects
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents(
            @RequestParam(defaultValue = "false") boolean includeSubjects) {
        List<StudentResponse> students;

        if (includeSubjects) {
            students = studentService.getAllStudentsWithSubjects();
        } else {
            students = studentService.getAllStudents();
        }

        return ResponseEntity.ok(students);
    }
    // assign subject to student
    @PostMapping("/{studentId}/subjects/{subjectId}")
    public ResponseEntity<StudentResponse> assignSubjectToStudent(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        StudentResponse updatedStudent = studentService.assignSubjectToStudent(studentId, subjectId);
        return ResponseEntity.ok(updatedStudent);
    }
    // delete Student
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}