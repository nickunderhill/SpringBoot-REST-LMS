package com.softserve.marathon;

import com.softserve.marathon.model.Role;
import com.softserve.marathon.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class  SoftServeMarathonApplication implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;

    public static void main(String[] args) {
        SpringApplication.run(SoftServeMarathonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByRole("ROLE_MENTOR") == null) roleRepository.save(new Role("ROLE_MENTOR"));
        if (roleRepository.findByRole("ROLE_STUDENT") == null) roleRepository.save(new Role("ROLE_STUDENT"));
        if (roleRepository.findByRole("ROLE_ADMIN") == null) roleRepository.save(new Role("ROLE_ADMIN"));
    }
}
