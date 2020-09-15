package com.softserve.marathon.service;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Progress;
import com.softserve.marathon.model.Task;
import com.softserve.marathon.model.User;

import java.util.List;

public interface ProgressService {
    void delete(Long id) throws EntityNotFoundException;

    Progress getProgressById(Long id) throws EntityNotFoundException;

    Progress addTaskForStudent(Task task, User user);

    boolean setStatus(String taskStatus, Progress progress);

    List<Progress> allProgressByUserIdAndCourseId(Long userId, Long courseId);

    List<Progress> allProgressByUserIdAndSprintId(Long userId, Long sprintId);

}
