package com.softserve.marathon.controller;

import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.repository.CourseRepository;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.SprintService;
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
@RequestMapping("/sprints")
public class SprintController {
    Logger logger = LoggerFactory.getLogger(SprintController.class);
    SprintService sprintService;
    UserRepository userRepository;
    CourseRepository courseRepository;

    public SprintController(SprintService sprintService, UserRepository userRepository, CourseRepository courseRepository) {
        this.sprintService = sprintService;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/course/{courseId}")
    public String sprintListByCourse(@PathVariable Long courseId,
                                     Model model,
                                     HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        Long studentId = userRepository.getUserByEmail(userEmail).getId();
        List<Sprint> sprints;
        if (request.isUserInRole("ROLE_STUDENT")) {
            sprints = sprintService.getSprintByUserIdAndCourse(studentId, courseId);
        } else {
            sprints = sprintService.getSprintsByCourse(courseId);
        }
        model.addAttribute("sprints", sprints);
        logger.info("Rendering sprint/list.html view");
        return "sprint/list";
    }

    @GetMapping("/course/{courseId}/add")
    public String createSprint(@PathVariable Long courseId, Model model) {
        logger.info("Rendering sprint/create.html view");
        model.addAttribute("sprint", new Sprint());
        return "sprint/create";
    }

    @PostMapping("/course/{courseId}/add")
    public String createSprint(@PathVariable Long courseId,
                               @Valid @ModelAttribute Sprint sprint,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) creating sprint" + bindingResult.getAllErrors());
            return "sprint/create";
        }
        logger.info("Creating new sprint");
        sprint.setCourse(courseRepository.getOne(courseId));
        sprintService.createSprint(sprint);
        return "redirect:/sprints/course/" + courseId;
    }

}