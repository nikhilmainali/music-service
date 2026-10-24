package com.musicservice.controller;

import com.musicservice.dto.TasteMatch;
import com.musicservice.dto.TasteRecommendation;
import com.musicservice.dto.UserRequest;
import com.musicservice.model.Music;
import com.musicservice.model.User;
import com.musicservice.service.DiscoveryService;
import com.musicservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User Management and Taste Discovery.
 *
 * Endpoints:
 * - GET /user -> the whole catalog of users
 * - GET /user/{user-id} -> single user details
 * - POST /user -> create a new user
 * - PATCH /user/{user-id} -> update user name and/or add music to library
 * - DELETE /user/{user-id} -> delete user
 * - DELETE /user/{user-id}/music/{music-id} -> remove a song from user's library
 *
 * Discovery endpoints:
 * - GET /user/{user-id}/similar-users -> discover people with similar music taste
 * - GET /user/{user-id}/recommendations -> discover music recommended by similar users
 * - GET /user/{user-id}/music -> view full song details for this user's music list
 */
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Endpoints for user management and taste discovery")
public class UserController {

    private final UserService userService;
    private final DiscoveryService discoveryService;

    public UserController(UserService userService, DiscoveryService discoveryService) {
        this.userService = userService;
        this.discoveryService = discoveryService;
    }

    /**
     * GET /user -> Retrieve the whole user catalog.
     */
    @GetMapping
    @Operation(summary = "Get the whole user catalog", description = "Returns a list of all users and their music list")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * GET /user/{user-id} -> Retrieve a single user by ID.
     */
    @GetMapping("/{user-id}")
    @Operation(summary = "Get a user by ID", description = "Returns a single user matching the given user ID")
    public ResponseEntity<User> getUserById(@PathVariable("user-id") Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * POST /user -> Create a new user.
     *
     * Example Body:
     * {
     *     "name": "Alice"
     * }
     */
    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user profile")
    public ResponseEntity<User> createUser(@Valid @RequestBody UserRequest request) {
        User created = userService.createUser(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PATCH /user/{user-id} -> Partially update a user.
     * Can update user's name, add a music track to their library, or both.
     *
     * Example Body:
     * {
     *     "name": "Alice Cooper",
     *     "music": "2"
     * }
     */
    @PatchMapping("/{user-id}")
    @Operation(summary = "Partially update a user", description = "Updates name and/or adds music to user's library")
    public ResponseEntity<User> updateUser(
            @PathVariable("user-id") Long userId,
            @RequestBody UserRequest request) {
        User updated = userService.updateUser(userId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /user/{user-id} -> Delete a user.
     */
    @DeleteMapping("/{user-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user", description = "Deletes a user by ID")
    public ResponseEntity<Void> deleteUser(@PathVariable("user-id") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /user/{user-id}/music/{music-id} -> Remove a specific song from a user's library.
     */
    @DeleteMapping("/{user-id}/music/{music-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a song from user's library", description = "Removes music-id from user's music list")
    public ResponseEntity<Void> deleteUserMusic(
            @PathVariable("user-id") Long userId,
            @PathVariable("music-id") Long musicId) {
        userService.deleteUserMusic(userId, musicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /user/{user-id}/music -> Retrieve full song details for this user's music list.
     */
    @GetMapping("/{user-id}/music")
    @Operation(summary = "Get full music details for user", description = "Returns the complete Music objects for user's songs")
    public ResponseEntity<List<Music>> getUserMusicList(@PathVariable("user-id") Long userId) {
        return ResponseEntity.ok(userService.getUserMusicCatalog(userId));
    }

    /**
     * GET /user/{user-id}/similar-users -> Discovery: Find people with similar music taste.
     */
    @GetMapping("/{user-id}/similar-users")
    @Operation(summary = "Discover people with similar taste", description = "Finds other users ranked by taste similarity")
    public ResponseEntity<List<TasteMatch>> getSimilarUsers(@PathVariable("user-id") Long userId) {
        return ResponseEntity.ok(discoveryService.findSimilarUsers(userId));
    }

    /**
     * GET /user/{user-id}/recommendations -> Discovery: Recommend music based on similar users.
     */
    @GetMapping("/{user-id}/recommendations")
    @Operation(summary = "Discover new music", description = "Recommends tracks listened to by similar users")
    public ResponseEntity<List<TasteRecommendation>> getRecommendations(@PathVariable("user-id") Long userId) {
        return ResponseEntity.ok(discoveryService.recommendMusic(userId));
    }
}
