package com.student.studentmanagementsystem.service;


import com.student.studentmanagementsystem.dto.StudentRequest;
import com.student.studentmanagementsystem.dto.StudentResponse;
import com.student.studentmanagementsystem.entity.Student;
import com.student.studentmanagementsystem.entity.Subject;
import com.student.studentmanagementsystem.exception.ResourceAlreadyExistsException;
import com.student.studentmanagementsystem.exception.ResourceNotFoundException;
import com.student.studentmanagementsystem.repository.StudentRepository;
import com.student.studentmanagementsystem.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private StudentRequest studentRequest;
    private Student student;
    private Subject subject;

    @BeforeEach
    void setUp() {
        studentRequest = new StudentRequest();
        studentRequest.setFirstName("John");
        studentRequest.setLastName("Doe");
        studentRequest.setEmail("john.doe@example.com");
        studentRequest.setPhoneNumber("1234567890");

        student = new Student();
        student.setId(1L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe@example.com");
        student.setPhoneNumber("1234567890");
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        subject = new Subject();
        subject.setId(1L);
        subject.setName("Mathematics");
        subject.setDescription("Basic Mathematics");
        subject.setCredits(3);
    }

    @Test
    void createStudent_Success() {
        // Given
        when(studentRepository.existsByEmail(studentRequest.getEmail())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentResponse result = studentService.createStudent(studentRequest);

        // Then
        assertNotNull(result);
        assertEquals(student.getId(), result.getId());
        assertEquals(student.getFirstName(), result.getFirstName());
        assertEquals(student.getEmail(), result.getEmail());

        verify(studentRepository).existsByEmail(studentRequest.getEmail());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void createStudent_EmailAlreadyExists_ThrowsException() {
        // Given
        when(studentRepository.existsByEmail(studentRequest.getEmail())).thenReturn(true);

        // When & Then
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> studentService.createStudent(studentRequest)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(studentRepository).existsByEmail(studentRequest.getEmail());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void updateStudent_Success() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentRepository.existsByEmail(studentRequest.getEmail())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentResponse result = studentService.updateStudent(studentId, studentRequest);

        // Then
        assertNotNull(result);
        assertEquals(student.getId(), result.getId());
        verify(studentRepository).findById(studentId);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void updateStudent_StudentNotFound_ThrowsException() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.updateStudent(studentId, studentRequest)
        );

        assertTrue(exception.getMessage().contains("Student not found"));
        verify(studentRepository).findById(studentId);
    }

    @Test
    void assignSubjectToStudent_Success() {
        // Given
        Long studentId = 1L;
        Long subjectId = 1L;

        when(studentRepository.findByIdWithSubjects(studentId)).thenReturn(Optional.of(student));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        // When
        StudentResponse result = studentService.assignSubjectToStudent(studentId, subjectId);

        // Then
        assertNotNull(result);
        verify(studentRepository).findByIdWithSubjects(studentId);
        verify(subjectRepository).findById(subjectId);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void assignSubjectToStudent_StudentNotFound_ThrowsException() {
        // Given
        Long studentId = 1L;
        Long subjectId = 1L;

        when(studentRepository.findByIdWithSubjects(studentId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> studentService.assignSubjectToStudent(studentId, subjectId)
        );

        assertTrue(exception.getMessage().contains("Student not found"));
    }

    @Test
    void getAllStudentsWithSubjects_Success() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(studentRepository.findAllWithSubjects()).thenReturn(students);

        // When
        List<StudentResponse> result = studentService.getAllStudentsWithSubjects();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(studentRepository).findAllWithSubjects();
    }

    @Test
    void deleteStudent_Success() {
        // Given
        Long studentId = 1L;
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        // When
        studentService.deleteStudent(studentId);

        // Then
        verify(studentRepository).findById(studentId);
        verify(studentRepository).delete(student);
    }
}