package com.softserve.marathon.controller;

import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/sprints")
public class SprintController {
    Logger logger = LoggerFactory.getLogger(SprintController.class);

    SprintService sprintService;
    UserRepository userRepository;

    public SprintController(SprintService sprintService, UserRepository userRepository) {
        this.sprintService = sprintService;
        this.userRepository = userRepository;
    }

    @GetMapping("/marathon/{marathonId}")
    public String SprintListByMarathon(@PathVariable Long marathonId,
                                       Model model,
                                       HttpServletRequest request) {
        String userEmail = request.getUserPrincipal().getName();
        Long studentId = userRepository.getUserByEmail(userEmail).getId();
        List<Sprint> sprints;
        if (request.isUserInRole("ROLE_STUDENT")) {
            sprints = sprintService.getSprintByUserIdAndMarathon(studentId, marathonId);
        } else {
            sprints = sprintService.getSprintsByMarathon(marathonId);
        }
        model.addAttribute("sprints", sprints);
        logger.info("Rendering sprint/list.html view");
        return "sprint/list";
    }
}