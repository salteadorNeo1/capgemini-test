package com.capgemini.test.code.repository;

import com.capgemini.test.code.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByIdAndRoomId(Long id, Integer roomId);

    boolean existsByDni(String dni);

    boolean existsByPhone(String phone);
}
