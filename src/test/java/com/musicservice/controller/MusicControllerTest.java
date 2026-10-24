package com.musicservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicservice.dto.MusicRequest;
import com.musicservice.model.Music;
import com.musicservice.model.User;
import com.musicservice.repository.MusicRepository;
import com.musicservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Music REST endpoints.
 *
 * Demonstrates:
 * - Setting up deterministic test state in @BeforeEach
 * - MockMvc testing for GET, POST, PATCH, and DELETE operations
 * - Asserting JSON responses and HTTP status codes
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MusicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private UserRepository userRepository;

    private Long testMusicId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        musicRepository.deleteAll();

        Music song1 = musicRepository.save(new Music("Daft Punk", "Electronic", "https://open.spotify.com/track/4Cee00Dyq308ZgfSd9337b"));
        Music song2 = musicRepository.save(new Music("Miles Davis", "Jazz", "https://open.spotify.com/track/4vfpfdUgvnLgUm0R204XkW"));
        Music song3 = musicRepository.save(new Music("Taylor Swift", "Pop", "https://open.spotify.com/track/0V3wPSX9Ir6xc75YJh92cf"));

        User alice = new User("Alice");
        alice.setMusic(Set.of(song1.getId(), song3.getId()));
        User bob = new User("Bob");
        bob.setMusic(Set.of(song1.getId()));
        userRepository.saveAll(List.of(alice, bob));

        testMusicId = song1.getId();
    }

    @Test
    @DisplayName("GET /music should return the full music catalog")
    void testGetAllMusic() throws Exception {
        mockMvc.perform(get("/music"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].artist-name").exists())
                .andExpect(jsonPath("$[0].genre-name").exists())
                .andExpect(jsonPath("$[0].link").exists());
    }

    @Test
    @DisplayName("GET /music/{music-id} should return single song by ID")
    void testGetMusicById() throws Exception {
        mockMvc.perform(get("/music/" + testMusicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testMusicId.intValue())))
                .andExpect(jsonPath("$.artist-name", is("Daft Punk")))
                .andExpect(jsonPath("$.genre-name", is("Electronic")));
    }

    @Test
    @DisplayName("GET /music/{music-id} with non-existent ID should return 404")
    void testGetMusicByIdNotFound() throws Exception {
        mockMvc.perform(get("/music/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("Music with ID 99999 not found")));
    }

    @Test
    @DisplayName("POST /music should create a new song and return 201 Created")
    void testCreateMusic() throws Exception {
        MusicRequest request = new MusicRequest("Queen", "Rock", "https://open.spotify.com/track/queen");

        mockMvc.perform(post("/music")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.artist-name", is("Queen")))
                .andExpect(jsonPath("$.genre-name", is("Rock")))
                .andExpect(jsonPath("$.link", is("https://open.spotify.com/track/queen")));
    }

    @Test
    @DisplayName("PATCH /music/{music-id} should partially update a song")
    void testPatchMusic() throws Exception {
        MusicRequest patchRequest = new MusicRequest();
        patchRequest.setGenreName("French House");

        mockMvc.perform(patch("/music/" + testMusicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testMusicId.intValue())))
                .andExpect(jsonPath("$.artist-name", is("Daft Punk"))) // Unchanged
                .andExpect(jsonPath("$.genre-name", is("French House"))); // Updated
    }

    @Test
    @DisplayName("DELETE /music/{music-id} should remove the song and return 204")
    void testDeleteMusic() throws Exception {
        mockMvc.perform(delete("/music/" + testMusicId))
                .andExpect(status().isNoContent());

        // Verify song is gone
        mockMvc.perform(get("/music/" + testMusicId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /music/{music-id}/fans should return users who like the song")
    void testGetMusicFans() throws Exception {
        // Both Alice and Bob like song1
        mockMvc.perform(get("/music/" + testMusicId + "/fans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice", "Bob")));
    }
}
