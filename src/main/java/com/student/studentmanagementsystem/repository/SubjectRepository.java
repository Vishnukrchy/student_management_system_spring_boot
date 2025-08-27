package com.student.studentmanagementsystem.repository;

import com.student.studentmanagementsystem.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByName(String name);

    @Query("SELECT COUNT(s.students) > 0 FROM Subject s WHERE s.id = :subjectId")
    boolean hasStudentsAssigned(Long subjectId);

    boolean existsByName(String name);
}