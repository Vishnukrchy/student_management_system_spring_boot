package com.student.studentmanagementsystem.service;

import com.student.studentmanagementsystem.dto.SubjectRequest;
import com.student.studentmanagementsystem.dto.SubjectResponse;

import java.util.List;

public interface SubjectService {

    SubjectResponse createSubject(SubjectRequest subjectRequest);

    SubjectResponse updateSubject(Long subjectId, SubjectRequest subjectRequest);

    SubjectResponse getSubjectById(Long subjectId);

    List<SubjectResponse> getAllSubjects();

    void deleteSubject(Long subjectId);
}