package com.student.studentmanagementsystem.service;


import com.student.studentmanagementsystem.dto.SubjectRequest;
import com.student.studentmanagementsystem.dto.SubjectResponse;
import com.student.studentmanagementsystem.entity.Subject;
import com.student.studentmanagementsystem.exception.BusinessLogicException;
import com.student.studentmanagementsystem.exception.ResourceAlreadyExistsException;
import com.student.studentmanagementsystem.exception.ResourceNotFoundException;
import com.student.studentmanagementsystem.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    @Autowired
    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public SubjectResponse createSubject(SubjectRequest subjectRequest) {
        // Business validation - check if subject name already exists
        if (subjectRepository.existsByName(subjectRequest.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Subject with name '" + subjectRequest.getName() + "' already exists"
            );
        }

        // Create new subject entity
        Subject subject = new Subject();
        subject.setName(subjectRequest.getName());
        subject.setDescription(subjectRequest.getDescription());
        subject.setCredits(subjectRequest.getCredits());

        // Save subject
        Subject savedSubject = subjectRepository.save(subject);

        return convertToSubjectResponse(savedSubject);
    }

    @Override
    public SubjectResponse updateSubject(Long subjectId, SubjectRequest subjectRequest) {
        // Find existing subject
        Subject existingSubject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        // Business validation - check if name is being changed to existing name
        if (!existingSubject.getName().equals(subjectRequest.getName()) &&
                subjectRepository.existsByName(subjectRequest.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Subject with name '" + subjectRequest.getName() + "' already exists"
            );
        }

        // Update subject fields
        existingSubject.setName(subjectRequest.getName());
        existingSubject.setDescription(subjectRequest.getDescription());
        existingSubject.setCredits(subjectRequest.getCredits());

        // Save updated subject
        Subject updatedSubject = subjectRepository.save(existingSubject);

        return convertToSubjectResponse(updatedSubject);
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectResponse getSubjectById(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        return convertToSubjectResponse(subject);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        return subjects.stream()
                .map(this::convertToSubjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSubject(Long subjectId) {
        // Find subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + subjectId));

        // Business validation - check if subject is assigned to any students
        if (subjectRepository.hasStudentsAssigned(subjectId)) {
            throw new BusinessLogicException(
                    "Cannot delete subject '" + subject.getName() + "' as it is assigned to one or more students"
            );
        }

        // Delete subject
        subjectRepository.delete(subject);
    }

    // Helper method for entity to DTO conversion
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