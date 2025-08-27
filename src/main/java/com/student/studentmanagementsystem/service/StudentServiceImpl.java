package com.student.studentmanagementsystem.service;


import com.student.studentmanagementsystem.dto.StudentRequest;
import com.student.studentmanagementsystem.dto.StudentResponse;
import com.student.studentmanagementsystem.dto.SubjectResponse;
import com.student.studentmanagementsystem.entity.Student;
import com.student.studentmanagementsystem.entity.Subject;
import com.student.studentmanagementsystem.exception.BusinessLogicException;
import com.student.studentmanagementsystem.exception.ResourceAlreadyExistsException;
import com.student.studentmanagementsystem.exception.ResourceNotFoundException;
import com.student.studentmanagementsystem.repository.StudentRepository;
import com.student.studentmanagementsystem.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository, SubjectRepository subjectRepository) {
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public StudentResponse createStudent(StudentRequest studentRequest) {
        // Business validation - check if email already exists
        if (studentRepository.existsByEmail(studentRequest.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Student with email " + studentRequest.getEmail() + " already exists"
            );
        }

        // Create new student entity
        Student student = new Student();
        student.setFirstName(studentRequest.getFirstName());
        student.setLastName(studentRequest.getLastName());
        student.setEmail(studentRequest.getEmail());
        student.setPhoneNumber(studentRequest.getPhoneNumber());

        // Save student
        Student savedStudent = studentRepository.save(student);

        return convertToStudentResponse(savedStudent);
    }

    @Override
    public StudentResponse updateStudent(Long studentId, StudentRequest studentRequest) {
        // Find existing student
        Student existingStudent = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Business validation - check if email is being changed to existing email
        if (!existingStudent.getEmail().equals(studentRequest.getEmail()) &&
                studentRepository.existsByEmail(studentRequest.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    "Student with email " + studentRequest.getEmail() + " already exists"
            );
        }

        // Update student fields
        existingStudent.setFirstName(studentRequest.getFirstName());
        existingStudent.setLastName(studentRequest.getLastName());
        existingStudent.setEmail(studentRequest.getEmail());
        existingStudent.setPhoneNumber(studentRequest.getPhoneNumber());

        // Save updated student
        Student updatedStudent = studentRepository.save(existingStudent);

        return convertToStudentResponse(updatedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long studentId) {
        Student student = studentRepository.findByIdWithSubjects(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        return convertToStudentResponseWithSubjects(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(this::convertToStudentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudentsWithSubjects() {
        List<Student> students = studentRepository.findAllWithSubjects();
        return students.stream()
                .map(this::convertToStudentResponseWithSubjects)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponse assignSubjectToStudent(Long studentId, Long subjectId) {
        // Find student
        Student student = studentRepository.findByIdWithSubjects(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Find subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        // Business validation - check if subject is already assigned to student
        if (student.getSubjects().contains(subject)) {
            throw new BusinessLogicException(
                    "Subject '" + subject.getName() + "' is already assigned to student"
            );
        }

        // Assign subject to student
        student.addSubject(subject);
        Student updatedStudent = studentRepository.save(student);

        return convertToStudentResponseWithSubjects(updatedStudent);
    }

    @Override
    public void deleteStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        studentRepository.delete(student);
    }

    // Helper methods for entity to DTO conversion
    private StudentResponse convertToStudentResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setEmail(student.getEmail());
        response.setPhoneNumber(student.getPhoneNumber());
        response.setCreatedAt(student.getCreatedAt());
        response.setUpdatedAt(student.getUpdatedAt());
        return response;
    }

    private StudentResponse convertToStudentResponseWithSubjects(Student student) {
        StudentResponse response = convertToStudentResponse(student);

        Set<SubjectResponse> subjectResponses = student.getSubjects().stream()
                .map(this::convertToSubjectResponse)
                .collect(Collectors.toSet());

        response.setSubjects(subjectResponses);
        return response;
    }

    private SubjectResponse convertToSubjectResponse(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());
        response.setName(subject.getName());
        response.setDescription(subject.getDescription());
        response.setCredits(subject.getCredits());
        response.setCreatedAt(subject.getCreatedAt());
        response.setUpdatedAt(subject.getUpdatedAt());
        return response;
    }
}