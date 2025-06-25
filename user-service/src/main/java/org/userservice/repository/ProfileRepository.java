package org.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.userservice.model.entity.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);

}
