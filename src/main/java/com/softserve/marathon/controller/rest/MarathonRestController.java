package com.softserve.marathon.controller.rest;

import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.MarathonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public MarathonRestController(MarathonService marathonService, UserRepository userRepository) {
        this.marathonService = marathonService;
        this.userRepository = userRepository;
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
}
