package com.softserve.marathon.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserve.marathon.model.Sprint;
import com.softserve.marathon.model.User;
import com.softserve.marathon.service.MarathonService;
import com.softserve.marathon.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/sprints")
public class SprintRestController {
    Logger logger = LoggerFactory.getLogger(MarathonRestController.class);

    private final SprintService sprintService;
    private final ObjectMapper mapper;

    @Autowired
    public SprintRestController(SprintService sprintService, ObjectMapper mapper) {
        this.sprintService = sprintService;
        this.mapper = mapper;
    }

    @GetMapping("/marathon/{marathonId}")
    public List<Sprint> SprintListByMarathon(@PathVariable Long marathonId) {
        logger.info("** GET /api/sprints/marathon/" + marathonId);
        List<Sprint> sprints = sprintService.getSprintsByMarathon(marathonId);
        return sprints;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @PostMapping
    public ResponseEntity<String> createSprint(@RequestBody String sprintAsJsonString) throws JsonProcessingException {
        logger.info("** POST /api/sprints/");
        Sprint sprint = mapper.readValue(sprintAsJsonString, Sprint.class);
        sprintService.createSprint(sprint);
        return ResponseEntity.ok(String.format("New sprint %s created", sprint.getTitle()));
    }
}
