package com.softserve.marathon.controller;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.Course;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {
    Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;
    private final UserRepository userRepository;

    public CourseController(CourseService courseService, UserRepository userRepository) {
        this.courseService = courseService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showCourses(Model model, HttpServletRequest request) {
        List<Course> courses;
        String userEmail = request.getUserPrincipal().getName();
        Long studentId = userRepository.getUserByEmail(userEmail).getId();
        if (request.isUserInRole("ROLE_STUDENT")) {
            courses = courseService.getAllByStudentId(studentId);
        } else {
            courses = courseService.getAll();
        }
        logger.info("Rendering course/list.html view");
        model.addAttribute("courses", courses);
        return "course/list";
    }

    @GetMapping("/add")
    public String createCourse(Model model) {
        logger.info("Rendering course/create.html view");
        model.addAttribute("course", new Course());
        return "course/create";
    }

    @PostMapping("/add")
    public String createCourse(@Valid @ModelAttribute Course course,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) creating course" + bindingResult.getAllErrors());
            return "course/create";
        }
        logger.info("Creating new course");
        courseService.createOrUpdateCourse(course);
        return "redirect:/courses";
    }


    @GetMapping("/edit/{id}")
    public String editCourse(Model model, @PathVariable Long id) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "course/edit";
    }

    @PostMapping("/edit/{id}")
    public String editCourse(@Valid @ModelAttribute Course course,
                               BindingResult bindingResult,
                               @PathVariable Long id) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) editing course Id " + id + bindingResult.getAllErrors());
            return "course/edit";
        }
        logger.info("Editing course Id " + id);
        courseService.createOrUpdateCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String close(@PathVariable Long id) {
        logger.info("Close course");
        try {
            Course courseFromDb = courseService.getCourseById(id);
            courseFromDb.setClosed(true);
            courseService.createOrUpdateCourse(courseFromDb);
        } catch (EntityNotFoundException e) {
            logger.error("Exception:" + e);
            e.printStackTrace();
        }
        return "redirect:/courses";
    }
}


