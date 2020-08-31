package com.softserve.marathon.service.imp;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Course;
import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.repository.CourseRepository;
import com.softserve.marathon.service.CourseService;
import com.softserve.marathon.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {
    Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final SprintService sprintService;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, SprintService sprintService) {
        this.courseRepository = courseRepository;
        this.sprintService = sprintService;
    }

    //For admin, mentor roles
    @Override
//    @PreAuthorize("hasAnyRole('ADMIN','MENTOR')")
    public List<Course> getAll() {
        logger.info("show getAll() method");
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .filter(course -> !course.isClosed())
                .collect(Collectors.toList());
    }

    //For student role
    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public List<Course> getAllByStudentId(Long studentId) {
        logger.info("show getAllByStudentId() method");
        List<Course> courses = courseRepository.findAllByUserId(studentId);
        return courses.stream()
                .filter(course -> !course.isClosed())
                .collect(Collectors.toList());
    }

    @Override
    public Course getCourseById(Long id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            logger.info("get course from BD by ID");
            return course.get();
        } else {
            throw new EntityNotFoundException("Course with id " + id + " doesn't exist");
        }
    }

    @Override
    public Course createOrUpdateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourseById(Long id) {
        try {
            Optional<Course> course = courseRepository.findById(id);
            if (course.isPresent()) {
                Course courseFromDb = course.get();
                for (Sprint sprint : courseFromDb.getSprints()) {
                    try {
                        logger.info("delete course");
                        sprintService.delete(sprint.getId());
                    } catch (EntityNotFoundException e) {
                        logger.error("Exception:" + e);
                        e.printStackTrace();
                    }
                }
                courseRepository.deleteById(id);
            } else {
                throw new EntityNotFoundException("Course with id " + id + " doesn't exist");
            }
        } catch (EntityNotFoundException e) {
            logger.error("Exception:" + e);
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsCourseByID(Long id) {
        return courseRepository.existsById(id);
    }

    @Override
    public boolean existsCourseByName(String name) {
        return courseRepository.existsByTitle(name);
    }
}
