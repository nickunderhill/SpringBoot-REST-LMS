package com.softserve.marathon.repository;

import com.softserve.marathon.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = "SELECT M.* FROM COURSE M\n" +
            "JOIN COURSE_USER MU ON MU.ID_COURSE  = M.ID \n" +
            "WHERE MU.ID_USER = ?1", nativeQuery = true)
    List<Course> findAllByUserId(Long id);

    Boolean existsByTitle(String name);
}
