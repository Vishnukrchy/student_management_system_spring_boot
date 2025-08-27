package com.student.studentmanagementsystem.service;




import com.student.studentmanagementsystem.dto.SubjectRequest;
import com.student.studentmanagementsystem.dto.SubjectResponse;
import com.student.studentmanagementsystem.entity.Subject;
import com.student.studentmanagementsystem.exception.BusinessLogicException;
import com.student.studentmanagementsystem.exception.ResourceAlreadyExistsException;
import com.student.studentmanagementsystem.exception.ResourceNotFoundException;
import com.student.studentmanagementsystem.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private SubjectRequest subjectRequest;
    private Subject subject;

    @BeforeEach
    void setUp() {
        subjectRequest = new SubjectRequest();
        subjectRequest.setName("Mathematics");
        subjectRequest.setDescription("Basic Mathematics");
        subjectRequest.setCredits(3);

        subject = new Subject();
        subject.setId(1L);
        subject.setName("Mathematics");
        subject.setDescription("Basic Mathematics");
        subject.setCredits(3);
        subject.setCreatedAt(LocalDateTime.now());
        subject.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void createSubject_Success() {
        // Given
        when(subjectRepository.existsByName(subjectRequest.getName())).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        // When
        SubjectResponse result = subjectService.createSubject(subjectRequest);

        // Then
        assertNotNull(result);
        assertEquals(subject.getId(), result.getId());
        assertEquals(subject.getName(), result.getName());
        assertEquals(subject.getCredits(), result.getCredits());

        verify(subjectRepository).existsByName(subjectRequest.getName());
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void createSubject_NameAlreadyExists_ThrowsException() {
        // Given
        when(subjectRepository.existsByName(subjectRequest.getName())).thenReturn(true);

        // When & Then
        ResourceAlreadyExistsException exception = assertThrows(
                ResourceAlreadyExistsException.class,
                () -> subjectService.createSubject(subjectRequest)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(subjectRepository).existsByName(subjectRequest.getName());
        verify(subjectRepository, never()).save(any(Subject.class));
    }

    @Test
    void updateSubject_Success() {
        // Given
        Long subjectId = 1L;
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        // When
        SubjectResponse result = subjectService.updateSubject(subjectId, subjectRequest);

        // Then
        assertNotNull(result);
        assertEquals(subject.getId(), result.getId());
        verify(subjectRepository).findById(subjectId);
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    void updateSubject_SubjectNotFound_ThrowsException() {
        // Given
        Long subjectId = 1L;
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> subjectService.updateSubject(subjectId, subjectRequest)
        );

        assertTrue(exception.getMessage().contains("Subject not found"));
        verify(subjectRepository).findById(subjectId);
    }

    @Test
    void deleteSubject_Success() {
        // Given
        Long subjectId = 1L;
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(subjectRepository.hasStudentsAssigned(subjectId)).thenReturn(false);

        // When
        subjectService.deleteSubject(subjectId);

        // Then
        verify(subjectRepository).findById(subjectId);
        verify(subjectRepository).hasStudentsAssigned(subjectId);
        verify(subjectRepository).delete(subject);
    }

    @Test
    void deleteSubject_HasAssignedStudents_ThrowsException() {
        // Given
        Long subjectId = 1L;
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(subjectRepository.hasStudentsAssigned(subjectId)).thenReturn(true);

        // When &
        BusinessLogicException exception = assertThrows(
                BusinessLogicException.class,
                () -> subjectService.deleteSubject(subjectId)
        );
        assertTrue(exception.getMessage().contains("Cannot delete subject"));
        verify(subjectRepository).findById(subjectId);
        verify(subjectRepository).hasStudentsAssigned(subjectId);
        verify(subjectRepository, never()).delete(any(Subject.class));
    }

    @Test
    void getSubjectById_Success() {
        // Given
        Long subjectId = 1L;
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        // When
        SubjectResponse result = subjectService.getSubjectById(subjectId);
        // Then
        assertNotNull(result);
        assertEquals(subject.getId(), result.getId());
        verify(subjectRepository).findById(subjectId);
    }

    @Test
    void getSubjectById_SubjectNotFound_ThrowsException() {
        // Given
        Long subjectId = 1L;
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());
        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> subjectService.getSubjectById(subjectId)
        );
        assertTrue(exception.getMessage().contains("Subject not found"));
        verify(subjectRepository).findById(subjectId);
    }
}
