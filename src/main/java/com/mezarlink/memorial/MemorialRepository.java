package com.mezarlink.memorial;

import com.mezarlink.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemorialRepository extends JpaRepository<Memorial, UUID> {

    Optional<Memorial> findBySlug(String slug);

    boolean existsBySlug(String slug);

    List<Memorial> findByOwner(User owner);
}
