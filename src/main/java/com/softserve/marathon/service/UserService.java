package com.softserve.marathon.service;

import com.softserve.marathon.dto.UserRequest;
import com.softserve.marathon.dto.UserResponse;
import com.softserve.marathon.model.Course;
import com.softserve.marathon.model.Role;
import com.softserve.marathon.model.Task;
import com.softserve.marathon.model.User;

import javax.transaction.Transactional;
import java.util.List;

public interface UserService {
    void delete(Long id);

    List<User> getAll();

    User getUserById(Long id);

    User getUserByEmail(String email);

    @Transactional
    boolean userExists(Long id);

    @Transactional
    boolean userExistsEmail(String email);

    boolean createOrUpdateUser(User user);

    // for CourseRestController
    boolean saveUser(UserRequest userRequest);

    UserResponse findByLoginAndPassword(UserRequest userRequest);

    String getExpirationLocalDate();

    List<User> getAllByRole(Role role);

    List<User> getAllByCourse(Long courseId);

    boolean addUserToCourse(User user, Course course);

    boolean deleteUserFromCourse(Long user, Long course);

    boolean addUserToTask(User user, Task task);
}
