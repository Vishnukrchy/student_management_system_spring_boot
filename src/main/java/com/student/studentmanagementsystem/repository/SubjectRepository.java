package com.student.studentmanagementsystem.repository;

import com.student.studentmanagementsystem.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByName(String name);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subject s JOIN s.students st WHERE s.id = :subjectId")
    boolean hasStudentsAssigned(@Param("subjectId") Long subjectId);

    boolean existsByName(String name);
}