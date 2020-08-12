package com.softserve.marathon.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.MarathonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/marathons")
public class MarathonRestController {
    Logger logger = LoggerFactory.getLogger(MarathonRestController.class);

    private final MarathonService marathonService;
    private final ObjectMapper mapper;

    public MarathonRestController(MarathonService marathonService, UserRepository userRepository, ObjectMapper mapper) {
        this.marathonService = marathonService;
        this.mapper = mapper;

    }

    @GetMapping
    public List<Marathon> showMarathons(Model model) {
        logger.info("** GET /api/marathons");
        List<Marathon> marathons = marathonService.getAll();
        model.addAttribute("marathons", marathons);
        return marathons;
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @PostMapping
    public ResponseEntity<String> createMarathon(@RequestBody String marathonAsJsonString) throws JsonProcessingException {
        logger.info("** POST /api/marathons");
        Marathon newMarathon = mapper.readValue(marathonAsJsonString, Marathon.class);
        if (marathonService.existsMarathonByName(newMarathon.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(String.format("Marathon with name \"%s\" already exists.", newMarathon.getTitle()));
        }
        marathonService.createOrUpdateMarathon(newMarathon);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.format("New marathon \"%s\" created.", newMarathon.getTitle()));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<String> getMarathon(@PathVariable("id") Long id) throws JsonProcessingException {
        logger.info("** GET /api/marathons/" + id);
        if (!marathonService.existsMarathonByID(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Marathon with ID %s does not exist", id));
        }
        return ResponseEntity.ok(mapper.writeValueAsString(marathonService.getMarathonById
                (id)));
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @PutMapping(path = "/{id}")
    public ResponseEntity<String> updateMarathon(@PathVariable("id") Long id,
                                                 @RequestBody String marathonAsJsonString) throws JsonProcessingException {
        logger.info("** PUT /api/marathons/" + id);
        if (!marathonService.existsMarathonByID(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Marathon with ID %s does not exist", id));
        }
        Marathon marathon = mapper.readValue(marathonAsJsonString, Marathon.class);
        marathon.setId(id);
        marathonService.createOrUpdateMarathon(marathon);
        return ResponseEntity.ok(String.format("Marathon with ID %s updated", id));
    }

    @Secured({"ROLE_ADMIN", "ROLE_MENTOR"})
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<String> deleteMarathon(@PathVariable Long id) {
        logger.info("** DELETE /api/marathons/" + id);
        if (!marathonService.existsMarathonByID(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Marathon with ID %s does not exist", id));
        }
        marathonService.deleteMarathonById(id);
        return ResponseEntity.ok(String.format("Marathon with ID %s deleted", id));
    }
}