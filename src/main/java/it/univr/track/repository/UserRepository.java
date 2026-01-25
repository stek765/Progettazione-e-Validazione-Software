package it.univr.track.repository;

import it.univr.track.entity.UserRegistered;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserRegistered, Long> {
    Optional<UserRegistered> findByUsername(String username);

    boolean existsByEmailIgnoreCase(String email);

    Optional<UserRegistered> findByEmailIgnoreCase(String email);

}
