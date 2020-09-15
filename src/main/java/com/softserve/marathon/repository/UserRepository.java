package com.softserve.marathon.repository;

import com.softserve.marathon.model.Role;
import com.softserve.marathon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User getUserByEmail(String email);

    List<User> getAllByRole(Role role);

    @Query(value = "SELECT U.* FROM USERS U\n" +
            "JOIN COURSE_USER MU ON MU.ID_USER  = U.ID \n" +
            "WHERE MU.ID_COURSE = ?1", nativeQuery = true)
    List<User> getAllByCourse(Long id);

    Boolean existsByEmail(String email);


}
