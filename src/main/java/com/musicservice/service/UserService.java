package com.musicservice.service;

import com.musicservice.dto.UserRequest;
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
 * Service layer for User business logic and playlist management.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MusicRepository musicRepository;

    public UserService(UserRepository userRepository, MusicRepository musicRepository) {
        this.userRepository = userRepository;
        this.musicRepository = musicRepository;
    }

    /**
     * Retrieve the whole catalog of users.
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieve a single user by ID.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found"));
    }

    /**
     * Create a new user with an initial name and optional music list.
     */
    @Transactional
    public User createUser(UserRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("User 'name' is required");
        }

        User user = new User(request.getName().trim());

        // If initial music IDs were provided, validate and add them
        if (request.getMusicIds() != null) {
            for (Long musicId : request.getMusicIds()) {
                if (!musicRepository.existsById(musicId)) {
                    throw new ResourceNotFoundException("Music with ID " + musicId + " not found in music catalog");
                }
                user.addMusic(musicId);
            }
        }

        return userRepository.save(user);
    }

    /**
     * Partially update a user (PATCH).
     * Can update name, add music to their list, or both.
     *
     * Example PATCH payload:
     * {
     *     "name": "Jane",
     *     "music": "2"
     * }
     */
    @Transactional
    public User updateUser(Long id, UserRequest request) {
        User user = getUserById(id);

        if (request != null) {
            // Update name if provided
            if (request.getName() != null && !request.getName().isBlank()) {
                user.setName(request.getName().trim());
            }

            // Add music if provided
            if (request.getMusicIds() != null && !request.getMusicIds().isEmpty()) {
                for (Long musicId : request.getMusicIds()) {
                    if (!musicRepository.existsById(musicId)) {
                        throw new ResourceNotFoundException("Music with ID " + musicId + " not found in music catalog");
                    }
                    user.addMusic(musicId);
                }
            }
        }

        return userRepository.save(user);
    }

    /**
     * Delete a user by ID.
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with ID " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Remove a specific music ID from a user's music list.
     */
    @Transactional
    public void deleteUserMusic(Long userId, Long musicId) {
        User user = getUserById(userId);

        if (!user.getMusic().contains(musicId)) {
            throw new ResourceNotFoundException(
                    "Music with ID " + musicId + " not found in user " + userId + "'s music list");
        }

        user.removeMusic(musicId);
        userRepository.save(user);
    }

    /**
     * Retrieve the full Music objects for all songs in the user's music list.
     */
    @Transactional(readOnly = true)
    public List<Music> getUserMusicCatalog(Long userId) {
        User user = getUserById(userId);
        return musicRepository.findAllById(user.getMusic());
    }
}
