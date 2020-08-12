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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MarathonRestController {
    Logger logger = LoggerFactory.getLogger(MarathonRestController.class);

    private final MarathonService marathonService;
    private final UserRepository userRepository;
    private final ObjectMapper mapper;

    public MarathonRestController(MarathonService marathonService, UserRepository userRepository, ObjectMapper mapper) {
        this.marathonService = marathonService;
        this.userRepository = userRepository;
        this.mapper = mapper;

    }

    //Marathon List
    @GetMapping("/marathons")
    public List<Marathon> showMarathons(Model model, HttpServletRequest request) {
        List<Marathon> marathons = marathonService.getAll();
        model.addAttribute("marathons", marathons);
        return marathons;
    }

    //Create Marathon
    @PostMapping("/marathons/add")
    public Marathon createMarathon(String name) {
        logger.info("creating new marathon " + name);
        Marathon newMarathon = new Marathon();
        newMarathon.setTitle(name);
        return marathonService.createOrUpdateMarathon(newMarathon);
    }

    @GetMapping(path = "/marathons/{id}")
    public ResponseEntity<String> getMarathon(@PathVariable("id") Long id) throws JsonProcessingException {
        if (!marathonService.existsMarathonByID(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Marathon with ID %s does not exist", id));
        }
        return ResponseEntity.ok(mapper.writeValueAsString(marathonService.getMarathonById
                (id)));
    }

    @PutMapping(path = "/marathons/{id}")
    public ResponseEntity<String> updateMarathon(@PathVariable("id") Long id,
                                                 @RequestBody String marathonAsJsonString) throws JsonProcessingException {
        if (!marathonService.existsMarathonByID(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(String.format("Marathon with ID %s does not exist", id));
        }

        Marathon marathon = mapper.readValue(marathonAsJsonString, Marathon.class);
        marathonService.createOrUpdateMarathon(marathon);
        return ResponseEntity.ok(String.format("Marathon with ID %s updated", id));
    }

    @PostMapping(path = "/marathons")
    public ResponseEntity<String> updateMarathon(@RequestBody String marathonAsJsonString)
            throws JsonProcessingException {
        Marathon marathon = mapper.readValue(marathonAsJsonString, Marathon.class);
        marathonService.createOrUpdateMarathon(marathon);
        return ResponseEntity.ok("New marathon created");
    }

    @DeleteMapping(path = "/marathons/{id}")
    public ResponseEntity<String> deleteMarathon(@PathVariable("id") Long id) {
        marathonService.deleteMarathonById(id);
        return ResponseEntity.ok(String.format("Marathon with ID %s deleted", id));
    }
}