package com.student.studentmanagementsystem.service;

import com.student.studentmanagementsystem.dto.StudentRequest;
import com.student.studentmanagementsystem.dto.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(StudentRequest studentRequest);

    StudentResponse updateStudent(Long studentId, StudentRequest studentRequest);

    StudentResponse getStudentById(Long studentId);

    List<StudentResponse> getAllStudents();

    List<StudentResponse> getAllStudentsWithSubjects();

    StudentResponse assignSubjectToStudent(Long studentId, Long subjectId);

    void deleteStudent(Long studentId);
}
