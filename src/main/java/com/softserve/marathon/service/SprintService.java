package com.softserve.marathon.service;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Course;
import com.softserve.marathon.model.Sprint;

import java.util.List;

public interface SprintService {
    void delete(Long id) throws EntityNotFoundException;

    List<Sprint> getSprintsByCourse(Long id);

    List<Sprint> getSprintByUserIdAndCourse(Long userId, Long courseId);

    boolean addSprintToCourse(Sprint sprint, Course course);

    boolean updateSprint(Sprint sprint);

    void createSprint(Sprint sprint);

    void finishSprint(Sprint sprint);

    Sprint getSprintById(Long id) throws EntityNotFoundException;

    boolean existSprint(Long id) throws EntityNotFoundException;
}
