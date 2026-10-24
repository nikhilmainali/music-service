package com.musicservice.service;

import com.musicservice.dto.MusicRequest;
import com.musicservice.exception.BadRequestException;
import com.musicservice.exception.ResourceNotFoundException;
import com.musicservice.model.Music;
import com.musicservice.model.User;
import com.musicservice.repository.MusicRepository;
import com.musicservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Music business logic.
 *
 * Concepts for beginners:
 * - @Service: Tells Spring this is a service component where business logic lives.
 * - @Transactional: Ensures database operations inside a method succeed together
 *   or rollback completely if an error occurs.
 */
@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;

    public MusicService(MusicRepository musicRepository, UserRepository userRepository) {
        this.musicRepository = musicRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieve the whole catalog of music.
     */
    @Transactional(readOnly = true)
    public List<Music> getAllMusic() {
        return musicRepository.findAll();
    }

    /**
     * Retrieve a single song by its ID.
     */
    @Transactional(readOnly = true)
    public Music getMusicById(Long id) {
        return musicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Music with ID " + id + " not found"));
    }

    /**
     * Create a new song.
     */
    @Transactional
    public Music createMusic(MusicRequest request) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be empty");
        }
        if (request.getArtistName() == null || request.getArtistName().isBlank()) {
            throw new BadRequestException("artist-name is required");
        }
        if (request.getGenreName() == null || request.getGenreName().isBlank()) {
            throw new BadRequestException("genre-name is required");
        }
        if (request.getLink() == null || request.getLink().isBlank()) {
            throw new BadRequestException("link is required");
        }

        Music music = new Music(
                request.getArtistName().trim(),
                request.getGenreName().trim(),
                request.getLink().trim()
        );

        return musicRepository.save(music);
    }

    /**
     * Partially update an existing song (PATCH).
     * Only fields provided in the request body are modified.
     */
    @Transactional
    public Music updateMusic(Long id, MusicRequest request) {
        Music music = getMusicById(id);

        if (request != null) {
            if (request.getArtistName() != null && !request.getArtistName().isBlank()) {
                music.setArtistName(request.getArtistName().trim());
            }
            if (request.getGenreName() != null && !request.getGenreName().isBlank()) {
                music.setGenreName(request.getGenreName().trim());
            }
            if (request.getLink() != null && !request.getLink().isBlank()) {
                music.setLink(request.getLink().trim());
            }
        }

        return musicRepository.save(music);
    }

    /**
     * Delete a song by ID.
     * Also cleans up any user playlists that contain this song.
     */
    @Transactional
    public void deleteMusic(Long id) {
        if (!musicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Music with ID " + id + " not found");
        }

        // Clean up references to this music across all users
        List<User> affectedUsers = userRepository.findUsersByMusicId(id);
        for (User user : affectedUsers) {
            user.removeMusic(id);
            userRepository.save(user);
        }

        musicRepository.deleteById(id);
    }

    /**
     * Search music by genre.
     */
    @Transactional(readOnly = true)
    public List<Music> getMusicByGenre(String genreName) {
        return musicRepository.findByGenreNameIgnoreCase(genreName);
    }
}
