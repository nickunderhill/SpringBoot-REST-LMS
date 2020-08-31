package com.softserve.marathon.service.imp;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Course;
import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.model.Task;
import com.softserve.marathon.repository.SprintRepository;
import com.softserve.marathon.service.SprintService;
import com.softserve.marathon.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SprintServiceImpl implements SprintService {
    private final SprintRepository sprintRepository;
    private final TaskService taskService;

    @Autowired
    public SprintServiceImpl(SprintRepository sprintRepository, TaskService taskService) {
        this.sprintRepository = sprintRepository;
        this.taskService = taskService;
    }

    @Override
    public void delete(Long id) throws EntityNotFoundException {
        Optional<Sprint> sprint = sprintRepository.findById(id);
        if (sprint.isPresent()) {
            Sprint sprintFromDb = sprint.get();
            for (Task task : sprintFromDb.getTasks()) {
                taskService.delete(task.getId());
            }
            sprintRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Sprint doesn't exist");
        }
    }

    @Override
    public List<Sprint> getSprintsByCourse(Long id) {
        return sprintRepository.getSprintsByCourseId(id);
    }

    @Override
    public List<Sprint> getSprintByUserIdAndCourse(Long userId, Long marathonId) {
        return sprintRepository.getSprintByUserIdAndCourse(userId, marathonId);
    }

    @Override
    public boolean addSprintToCourse(Sprint sprint, Course course) {
        sprint.setCourse(course);
        Sprint sprintFromDb = sprintRepository.getOne(sprint.getId());
        if (!sprintFromDb.equals(sprint)) {
            sprintRepository.save(sprint);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSprint(Sprint sprint) {
        if (!sprintRepository.existsById(sprint.getId())) {
            return false;
        }
        sprintRepository.save(sprint);
        return true;
    }

    @Override
    public void createSprint(Sprint sprint) {
        sprint.setStartDay(LocalDate.now());
        sprintRepository.save(sprint);
    }

    @Override
    public void finishSprint(Sprint sprint) {
        sprint.setFinish(LocalDate.now());
        sprintRepository.save(sprint);
    }

    @Override
    public Sprint getSprintById(Long id) throws EntityNotFoundException {
        Optional<Sprint> sprint = sprintRepository.findById(id);
        if (sprint.isPresent()) {
            return sprint.get();
        } else {
            throw new EntityNotFoundException("Sprint doesn't exist");
        }
    }

    @Override
    public boolean existSprint(Long id) throws EntityNotFoundException {
        return sprintRepository.existsById(id);
    }
}
