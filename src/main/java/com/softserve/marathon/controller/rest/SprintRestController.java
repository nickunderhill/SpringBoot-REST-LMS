package com.softserve.marathon.controller.rest;

    import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserve.marathon.model.Sprint;
    import com.softserve.marathon.service.SprintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

@RestController
@RequestMapping("/api/sprints")
public class SprintRestController {
    Logger logger = LoggerFactory.getLogger(CourseRestController.class);

    private final SprintService sprintService;
    private final ObjectMapper mapper;

    @Autowired
    public SprintRestController(SprintService sprintService, ObjectMapper mapper) {
        this.sprintService = sprintService;
        this.mapper = mapper;
    }

    @GetMapping("/course/{courseId}")
    public List<Sprint> SprintListByCourse(@PathVariable Long courseId) {
        logger.info("** GET /api/sprints/course/" + courseId);
        List<Sprint> sprints = sprintService.getSprintsByCourse(courseId);
        return sprints;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @PostMapping
    public ResponseEntity<String> createSprint(@RequestBody String sprintAsJsonString) throws JsonProcessingException {
        logger.info("** POST /api/sprints/");
        Sprint sprint = mapper.readValue(sprintAsJsonString, Sprint.class);
        sprintService.createSprint(sprint);
        return ResponseEntity.ok(String.format("New sprint \"%s\" created", sprint.getTitle()));
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @PutMapping("/{sprintId}")
    public ResponseEntity<String> updateSprint(@RequestBody String sprintAsJsonString, @PathVariable Long sprintId) throws JsonProcessingException {
        logger.info("** PUT /api/sprints/");
        Sprint sprint = mapper.readValue(sprintAsJsonString, Sprint.class);
        sprint.setId(sprintId);
        if (sprintService.updateSprint(sprint)) {
            return ResponseEntity.ok(String.format("Sprint id %s  updated", sprintId));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(String.format("Sprint with id %s not found", sprintId));
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @DeleteMapping("/{sprintId}")
    public ResponseEntity<String> deleteSprint(@PathVariable Long sprintId) {
        logger.info("** DELETE /api/sprints/" + sprintId);
        if (!sprintService.existSprint(sprintId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Sprint with ID %s does not exist", sprintId));
        }
        sprintService.delete(sprintId);
        return ResponseEntity.ok(String.format("Sprint ID %s deleted", sprintId));
    }
}
