package com.musicservice.repository;

import com.musicservice.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Music entity.
 *
 * Concepts for beginners:
 * - Spring Data JPA automatically provides ready-to-use methods such as:
 *   save(), findById(), findAll(), deleteById(), existsById(), etc.
 * - By defining method signatures like 'findByGenreNameIgnoreCase', Spring Data JPA
 *   automatically derives the SQL query without having to write SQL!
 */
@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    /**
     * Find all songs matching a genre, ignoring case.
     */
    List<Music> findByGenreNameIgnoreCase(String genreName);

    /**
     * Find all songs matching an artist, ignoring case.
     */
    List<Music> findByArtistNameIgnoreCase(String artistName);
}
