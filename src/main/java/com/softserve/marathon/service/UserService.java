package com.softserve.marathon.service;

import com.softserve.marathon.dto.UserRequest;
import com.softserve.marathon.dto.UserResponse;
import com.softserve.marathon.model.Marathon;
import com.softserve.marathon.model.Role;
import com.softserve.marathon.model.Task;
import com.softserve.marathon.model.User;

import java.util.List;

public interface UserService {
    void delete(Long id);

    List<User> getAll();

    User getUserById(Long id);

    User getUserByEmail(String email);

    boolean createOrUpdateUser(User user);

    // for MarathonRestController
    boolean saveUser(UserRequest userRequest);

    UserResponse findByLoginAndPassword(UserRequest userRequest);

    String getExpirationLocalDate();

    List<User> getAllByRole(Role role);

    List<User> getAllByMarathon(Long marathonId);

    boolean addUserToMarathon(User user, Marathon marathon);

    boolean deleteUserFromMarathon(Long user, Long marathon);

    boolean addUserToTask(User user, Task task);
}
