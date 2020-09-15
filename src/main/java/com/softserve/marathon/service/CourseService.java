package com.softserve.marathon.service;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Course;

import java.util.List;

public interface CourseService {

    Course createOrUpdateCourse(Course course);

    List<Course> getAll();

    List<Course> getAllByStudentId(Long studentId);

    Course getCourseById(Long id) throws EntityNotFoundException;

    void deleteCourseById(Long id);

    boolean existsCourseByID(Long id);

    boolean existsCourseByName(String name);
}
