package com.softserve.marathon.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserve.marathon.model.User;
import com.softserve.marathon.repository.RoleRepository;
import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
@RestController
@RequestMapping("/api/students")
public class StudentsRestController {
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final UserService userService;
    private final MarathonService marathonService;
    private final RoleRepository roleRepository;
    private final ObjectMapper mapper;

    @Autowired
    public StudentsRestController(UserService userService, MarathonService marathonService, RoleRepository roleRepository, ObjectMapper mapper) {
        this.userService = userService;
        this.marathonService = marathonService;
        this.roleRepository = roleRepository;
        this.mapper = mapper;
    }

    @GetMapping
    public List<User> studentsList() {
        logger.info("** GET /api/students");
        return userService.getAllByRole(roleRepository.findByRole("ROLE_STUDENT"));
    }

    @GetMapping("/marathon/{marathonId}")
    public List<User> studentsListByMarathon(@PathVariable Long marathonId) {
        logger.info("** GET /api/students/marathon/" + marathonId);
        return marathonService.getMarathonById(marathonId).getUsers();
    }

    @PostMapping
    public ResponseEntity<String> createStudent(@RequestBody String studentAsJsonString) throws JsonProcessingException {
        logger.info("** POST /api/students");
        User student = mapper.readValue(studentAsJsonString, User.class);
        String email = student.getEmail();
        if (userService.userExistsEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(String.format("User with email %s already exists", email));
        }
        student.setRole(roleRepository.findByRole("ROLE_STUDENT"));
        userService.createOrUpdateUser(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.format("New student created"));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<String> editStudent(@PathVariable Long studentId,
                                              @RequestBody String studentAsJsonString) throws JsonProcessingException {
        logger.info("** PUT /api/students/" + studentId);
        if (!userService.userExists(studentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body((String.format("Student with id %s doesn't exist", studentId)));
        }
        User student = mapper.readValue(studentAsJsonString, User.class);
        student.setId(studentId);
        userService.createOrUpdateUser(student);
        return ResponseEntity.ok(String.format("Student id %s updated", studentId));
    }

    //TODO Move to Marathon
    @PatchMapping("/{marathonId}")
    public String removeStudentFromMarathon(@PathVariable Long marathonId, @RequestBody Long studentId) {
        logger.info("Deleting /api/ student id " + studentId + " from marathon id " + marathonId);
        User student = userService.getUserById(studentId);
        userService.deleteUserFromMarathon(student.getId(), marathonId);
        return "redirect:/students/{marathonId}";
    }

    //TODO Move to Marathon
    @GetMapping("/{marathonId}/add")
    public String addStudent(@RequestParam("studentId") long studentId,
                             @PathVariable long marathonId) {
        logger.info("Adding student id " + studentId + " to marathon " + marathonId);
        userService.addUserToMarathon(
                userService.getUserById(studentId),
                marathonService.getMarathonById(marathonId));
        return "redirect:/students/{marathonId}";
    }
}
