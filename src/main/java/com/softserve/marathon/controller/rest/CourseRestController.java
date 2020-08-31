package com.softserve.marathon.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserve.marathon.model.Course;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseRestController {
    Logger logger = LoggerFactory.getLogger(CourseRestController.class);

    private final CourseService courseService;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public CourseRestController(CourseService courseService, UserRepository userRepository, ObjectMapper mapper) {
        this.courseService = courseService;
        this.userRepository = userRepository;
        this.mapper = mapper;

    }

    @GetMapping
    public List<Course> showCourses(HttpServletRequest request) {
        logger.info("** GET /api/courses");
        List<Course> courses;
        String login = request.getUserPrincipal().getName();
        Long studentId = userRepository.getUserByEmail(login).getId();
        if (request.isUserInRole("ROLE_STUDENT")) {
            courses = courseService.getAllByStudentId(studentId);
        } else {
            courses = courseService.getAll();
        }
        return courses;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @PostMapping
    public ResponseEntity<String> createCourse(@RequestBody String courseAsJsonString) throws JsonProcessingException {
        logger.info("** POST /api/courses");
        Course newCourse = mapper.readValue(courseAsJsonString, Course.class);
        if (courseService.existsCourseByName(newCourse.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(String.format("Course with name \"%s\" already exists.", newCourse.getTitle()));
        }
        courseService.createOrUpdateCourse(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.format("New marathon \"%s\" created.", newCourse.getTitle()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<String> getCourse(@PathVariable("id") Long id) throws JsonProcessingException {
        logger.info("** GET /api/marathons/" + id);
        if (!courseService.existsCourseByID(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Course with ID %s does not exist", id));
        }
        return ResponseEntity.ok(mapper.writeValueAsString(courseService.getCourseById
                (id)));
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable("id") Long id,
                                                 @RequestBody String marathonAsJsonString) throws JsonProcessingException {
        logger.info("** PUT /api/marathons/" + id);
        if (!courseService.existsCourseByID(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Course with ID %s does not exist", id));
        }
        Course course = mapper.readValue(marathonAsJsonString, Course.class);
        course.setId(id);
        courseService.createOrUpdateCourse(course);
        return ResponseEntity.ok(String.format("Course with ID %s updated", id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        logger.info("** DELETE /api/marathons/" + id);
        if (!courseService.existsCourseByID(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Course with ID %s does not exist", id));
        }
        courseService.deleteCourseById(id);
        return ResponseEntity.ok(String.format("Course with ID %s deleted", id));
    }
}