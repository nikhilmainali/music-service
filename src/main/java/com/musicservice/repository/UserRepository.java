package com.musicservice.repository;

import com.musicservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds all users who have a specific music ID in their music collection.
     * Uses a JPQL JOIN query on the user's music collection.
     */
    @Query("SELECT u FROM User u JOIN u.music m WHERE m = :musicId")
    List<User> findUsersByMusicId(@Param("musicId") Long musicId);
}
