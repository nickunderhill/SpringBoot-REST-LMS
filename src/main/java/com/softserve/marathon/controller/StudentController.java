package com.softserve.marathon.controller;

import com.softserve.marathon.model.Course;
import com.softserve.marathon.model.User;
import com.softserve.marathon.repository.RoleRepository;
import com.softserve.marathon.service.CourseService;
import com.softserve.marathon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {
    Logger logger = LoggerFactory.getLogger(StudentController.class);
    private final UserService userService;
    private final CourseService courseService;
    private final RoleRepository roleRepository;

    public StudentController(UserService userService, CourseService courseService, RoleRepository roleRepository) {
        this.userService = userService;
        this.courseService = courseService;
        this.roleRepository = roleRepository;
    }

    //All students list
    @Secured({"ROLE_ADMIN","ROLE_MENTOR"})
    @GetMapping("")
    public String studentsList(Model model) {
        logger.info("Rendering student/list.html view");
        model.addAttribute("students", userService.getAllByRole(roleRepository.findByRole("ROLE_STUDENT")));
        return "student/list";
    }

    //Students from specified course
    @GetMapping("/{courseId}")
    public String studentsListByCourse(Model model, @PathVariable Long courseId) {
        logger.info("Rendering student/listByCourse.html view");
        List<User> students = courseService.getCourseById(courseId).getUsers();
        List<User> allStudents = userService.getAll();
        model.addAttribute("students", students);
        model.addAttribute("allStudents", allStudents);
        return "student/listByCourse";
    }

    //Delete user from course
    @GetMapping("/{courseId}/delete/{studentId}")
    public String deleteStudentFromCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        User student = userService.getUserById(studentId);
        userService.deleteUserFromCourse(student.getId(), courseId);
        logger.info("Deleting student id " + studentId + " from course id " + courseId);
        return "redirect:/students/{courseId}";
    }

    //Edit user
    @GetMapping("/{courseId}/edit/{studentId}")
    public String editStudentForm(@PathVariable Long courseId,
                                  @PathVariable Long studentId,
                                  Model model) {
        logger.info("Rendering student/edit.html view");
        model.addAttribute("student", userService.getUserById(studentId));
        return "student/edit";
    }

    //Edit user
    @PostMapping("/{courseId}/edit/{studentId}")
    public String editStudentFormSubmit(@Valid @ModelAttribute("student") User student,
                                        BindingResult bindingResult,
                                        @PathVariable Long studentId,
                                        @PathVariable Long courseId) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) updating student id " + studentId + ": " + bindingResult.getAllErrors());
            return "student/edit";
        }
        userService.createOrUpdateUser(student);
        logger.info("Updating student id: " + studentId);
        return "redirect:/students/{courseId}";
    }

    //Create user
    @GetMapping("/{courseId}/create")
    public String createStudentForm(@PathVariable Long courseId, Model model,
                                    @ModelAttribute("errorMessage") String errorMessage,
                                    @ModelAttribute("student") User student) {
        logger.info("Rendering student/create.html view");
        model.addAttribute(courseId);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("student", student != null ? student : new User());
        return "student/create";
    }

    //Create user
    @PostMapping("/{courseId}/create")
    public String createStudentFormSubmit(@Valid @ModelAttribute("student") User student,
                                          BindingResult bindingResult,
                                          @PathVariable Long courseId,
                                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logger.error("Error(s) creating student" + bindingResult.getAllErrors());
            return "student/create";
        }
        logger.info("Creating new student for course " + courseId);
        student.setRole(roleRepository.findByRole("ROLE_STUDENT"));
        try {
            /*User newStudent = */userService.createOrUpdateUser(student);
            Course course = courseService.getCourseById(courseId);
            userService.addUserToCourse(student, course);
        } catch (DataIntegrityViolationException e) {
            logger.info("DataIntegrityViolationException occurred::" + "Error=" + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "This email is already in use.");
            redirectAttributes.addFlashAttribute("student", student);
            return "redirect:/students/{courseId}/create";
        }
        return "redirect:/students/{courseId}";
    }

    //Add user to course
    @GetMapping("/{courseId}/add")
    public String addStudent(@RequestParam("studentId") long studentId,
                             @PathVariable long courseId) {
        logger.info("Adding student id " + studentId + " to course " + courseId);
        userService.addUserToCourse(
                userService.getUserById(studentId),
                courseService.getCourseById(courseId));
        return "redirect:/students/{courseId}";
    }
}
