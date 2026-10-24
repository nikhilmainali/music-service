package com.musicservice.controller;

import com.musicservice.dto.MusicRequest;
import com.musicservice.model.Music;
import com.musicservice.model.User;
import com.musicservice.service.DiscoveryService;
import com.musicservice.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for the Music Catalog.
 *
 * Concepts for beginners:
 * - @RestController: Combines @Controller and @ResponseBody. Tells Spring that every method
 *   returns JSON directly to the HTTP response body.
 * - @RequestMapping("/music"): Base URL path for all endpoints in this controller.
 * - @GetMapping, @PostMapping, @PatchMapping, @DeleteMapping: Map HTTP methods to Java methods.
 * - @PathVariable: Extracts path parameters from the URL (e.g. /music/1 -> musicId = 1).
 * - @RequestBody: Automatically parses incoming JSON into Java objects.
 * - ResponseEntity: Allows customizing HTTP status codes (200 OK, 201 Created, 204 No Content).
 */
@RestController
@RequestMapping("/music")
@Tag(name = "Music", description = "Endpoints for managing the music catalog")
public class MusicController {

    private final MusicService musicService;
    private final DiscoveryService discoveryService;

    public MusicController(MusicService musicService, DiscoveryService discoveryService) {
        this.musicService = musicService;
        this.discoveryService = discoveryService;
    }

    /**
     * GET /music -> Retrieve the whole music catalog.
     * Optional query parameter: ?genre=electronic
     */
    @GetMapping
    @Operation(summary = "Get the whole music catalog", description = "Returns a list of all songs in the catalog")
    public ResponseEntity<List<Music>> getAllMusic(
            @RequestParam(value = "genre", required = false) String genre) {
        if (genre != null && !genre.isBlank()) {
            return ResponseEntity.ok(musicService.getMusicByGenre(genre.trim()));
        }
        return ResponseEntity.ok(musicService.getAllMusic());
    }

    /**
     * GET /music/{music-id} -> Retrieve a single song by ID.
     */
    @GetMapping("/{music-id}")
    @Operation(summary = "Get a song by ID", description = "Returns a single song matching the given music ID")
    public ResponseEntity<Music> getMusicById(@PathVariable("music-id") Long musicId) {
        Music music = musicService.getMusicById(musicId);
        return ResponseEntity.ok(music);
    }

    /**
     * POST /music -> Add a new song to the catalog.
     *
     * Example Body:
     * {
     *     "artist-name": "Daft Punk",
     *     "genre-name": "Electronic",
     *     "link": "https://open.spotify.com/track/4Cee00Dyq308ZgfSd9337b"
     * }
     */
    @PostMapping
    @Operation(summary = "Create a new song", description = "Adds a song to the music catalog")
    public ResponseEntity<Music> createMusic(@Valid @RequestBody MusicRequest request) {
        Music created = musicService.createMusic(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * PATCH /music/{music-id} -> Partially update a song.
     *
     * Example Body:
     * {
     *     "artist-name": "Updated Artist",
     *     "genre-name": "Synthwave",
     *     "link": "https://music.youtube.com"
     * }
     */
    @PatchMapping("/{music-id}")
    @Operation(summary = "Partially update a song", description = "Updates only the fields provided in the request body")
    public ResponseEntity<Music> updateMusic(
            @PathVariable("music-id") Long musicId,
            @RequestBody MusicRequest request) {
        Music updated = musicService.updateMusic(musicId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /music/{music-id} -> Remove a song from the catalog.
     */
    @DeleteMapping("/{music-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a song", description = "Removes a song from catalog and user playlists")
    public ResponseEntity<Void> deleteMusic(@PathVariable("music-id") Long musicId) {
        musicService.deleteMusic(musicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /music/{music-id}/fans -> Discovery: Find all users who added this song.
     */
    @GetMapping("/{music-id}/fans")
    @Operation(summary = "Discover fans of a song", description = "Returns all users who have this song in their library")
    public ResponseEntity<List<User>> getMusicFans(@PathVariable("music-id") Long musicId) {
        return ResponseEntity.ok(discoveryService.getMusicFans(musicId));
    }
}
