package com.softserve.marathon.service.imp;

import com.softserve.marathon.config.CustomUserDetails;
import com.softserve.marathon.dto.UserRequest;
import com.softserve.marathon.dto.UserResponse;
import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.*;
import com.softserve.marathon.repository.MarathonRepository;
import com.softserve.marathon.repository.ProgressRepository;
import com.softserve.marathon.repository.RoleRepository;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MarathonRepository marathonRepository;
    private final ProgressRepository progressRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MarathonRepository marathonRepository, ProgressRepository progressRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.marathonRepository = marathonRepository;
        this.progressRepository = progressRepository;
        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User doesn't exist");
        }
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityNotFoundException("User with id " + id + " doesn't exist");
        }
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user.getId() != null) {
            return user;
        } else {
            throw new EntityNotFoundException("User with id " + email + " doesn't exist");
        }
    }

    @Override
    @Transactional
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    @Transactional
    public boolean userExistsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public boolean createOrUpdateUser(User entity) {
        if (entity.getId() != null) {
            Optional<User> user = userRepository.findById(entity.getId());
            if (user.isPresent()) {
                User updateUser = user.get();
                updateUser.setEmail(entity.getEmail());
                updateUser.setFirstName(entity.getFirstName());
                updateUser.setLastName(entity.getLastName());
                updateUser.setPassword(passwordEncoder.encode(entity.getPassword()));
                userRepository.save(updateUser);
                return false;
            }
        }
        entity.setActive(true);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setRole(roleRepository.findByRole("ROLE_STUDENT"));
        userRepository.save(entity);
        return true;
    }

    @Override
    public boolean saveUser(UserRequest userRequest) {
        User user = new User();
        user.setRole(roleRepository.findByRole("ROLE_STUDENT"));
        user.setFirstName("API");
        user.setLastName("User");
        user.setEmail(userRequest.getLogin());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return (userRepository.save(user) != null);
    }

    @Override
    public UserResponse findByLoginAndPassword(UserRequest userRequest) {
        UserResponse result = null;
        User user = userRepository.getUserByEmail(userRequest.getLogin());
        if ((user != null)
                && (passwordEncoder.matches(userRequest.getPassword(),
                user.getPassword()))) {
            result = new UserResponse();
            result.setLogin(userRequest.getLogin());
            result.setRoleName(user.getRole().getRole());
        }
        return result;
    }

    @Override
    public String getExpirationLocalDate() {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime localDate = customUserDetails.getExpirationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy 'at' hh:mm");
        return localDate.format(formatter);
    }

    @Override
    @Transactional
    public List<User> getAllByRole(Role role) {
        List<User> roles = userRepository.getAllByRole(role);
        return !roles.isEmpty() ? roles : new ArrayList<>();
    }

    @Override
    @Transactional
    public List<User> getAllByMarathon(Long marathonId) {
        List<User> users = userRepository.getAllByMarathon(marathonId);
        return !users.isEmpty() ? users : new ArrayList<>();
    }

    @Override
    @Transactional
    public boolean addUserToMarathon(User user, Marathon marathon) {
        User userInstance = userRepository.getOne(user.getId());
        Marathon marathonInstance = marathonRepository.getOne(marathon.getId());
        if (!marathonInstance.getUsers().contains(userInstance)) {
            marathonInstance.getUsers().add(userInstance);
            marathonRepository.save(marathonInstance);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteUserFromMarathon(Long userId, Long marathonId) {
        User userInstance = userRepository.getOne(userId);
        Marathon marathonInstance = marathonRepository.getOne(marathonId);
        if (marathonInstance.getUsers().contains(userInstance)) {
            marathonInstance.getUsers().remove(userInstance);
            marathonRepository.save(marathonInstance);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addUserToTask(User user, Task task) {
        if (!progressRepository.checkProgressExist(user.getId(), task.getId())) {
            Progress progress = new Progress();
            progress.setStarted(LocalDate.now());
            progress.setStatus("start");
            progress.setTask(task);
            progress.setUser(user);
            progressRepository.save(progress);
            return true;
        }
        return false;
    }
}

