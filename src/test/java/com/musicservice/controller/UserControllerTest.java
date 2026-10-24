package com.musicservice.controller;

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
 * Integration tests for User endpoints and taste discovery.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MusicRepository musicRepository;

    @Autowired
    private UserRepository userRepository;

    private Long aliceId;
    private Long bobId;
    private Long song1Id;
    private Long song2Id;
    private Long song3Id;
    private Long song4Id;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        musicRepository.deleteAll();

        Music song1 = musicRepository.save(new Music("Daft Punk", "Electronic", "https://open.spotify.com/track/1"));
        Music song2 = musicRepository.save(new Music("Miles Davis", "Jazz", "https://open.spotify.com/track/2"));
        Music song3 = musicRepository.save(new Music("Taylor Swift", "Pop", "https://open.spotify.com/track/3"));
        Music song4 = musicRepository.save(new Music("Led Zeppelin", "Rock", "https://open.spotify.com/track/4"));

        song1Id = song1.getId();
        song2Id = song2.getId();
        song3Id = song3.getId();
        song4Id = song4.getId();

        // Alice: loves song 1 and song 3
        User alice = new User("Alice");
        alice.setMusic(Set.of(song1Id, song3Id));
        alice = userRepository.save(alice);
        aliceId = alice.getId();

        // Bob: loves song 1 and song 4 (Shares song 1 with Alice!)
        User bob = new User("Bob");
        bob.setMusic(Set.of(song1Id, song4Id));
        bob = userRepository.save(bob);
        bobId = bob.getId();
    }

    @Test
    @DisplayName("GET /user should return the whole catalog of users")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Alice", "Bob")));
    }

    @Test
    @DisplayName("GET /user/{user-id} should return single user with their music list")
    void testGetUserById() throws Exception {
        mockMvc.perform(get("/user/" + aliceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(aliceId.intValue())))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.music", hasItems(song1Id.intValue(), song3Id.intValue())));
    }

    @Test
    @DisplayName("GET /user/{user-id} with invalid ID should return 404")
    void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/user/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("User with ID 99999 not found")));
    }

    @Test
    @DisplayName("POST /user should create a new user")
    void testCreateUser() throws Exception {
        String json = """
                {
                    "name": "Eve"
                }
                """;

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Eve")))
                .andExpect(jsonPath("$.music", hasSize(0)));
    }

    @Test
    @DisplayName("PATCH /user/{user-id} should update user name and add music")
    void testPatchUser() throws Exception {
        String json = String.format("""
                {
                    "name": "Alice Wonderland",
                    "music": "%d"
                }
                """, song2Id);

        mockMvc.perform(patch("/user/" + aliceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(aliceId.intValue())))
                .andExpect(jsonPath("$.name", is("Alice Wonderland")))
                .andExpect(jsonPath("$.music", hasItem(song2Id.intValue())));
    }

    @Test
    @DisplayName("DELETE /user/{user-id}/music/{music-id} should remove song from user library")
    void testDeleteUserMusic() throws Exception {
        mockMvc.perform(delete("/user/" + aliceId + "/music/" + song1Id))
                .andExpect(status().isNoContent());

        // Verify song1 is no longer in Alice's library
        mockMvc.perform(get("/user/" + aliceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.music", not(hasItem(song1Id.intValue()))));
    }

    @Test
    @DisplayName("DELETE /user/{user-id} should delete user")
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/user/" + aliceId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/user/" + aliceId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /user/{user-id}/similar-users should find people with similar taste")
    void testGetSimilarUsers() throws Exception {
        mockMvc.perform(get("/user/" + aliceId + "/similar-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Bob")))
                .andExpect(jsonPath("$[0].shared-tracks-count", is(1)))
                .andExpect(jsonPath("$[0].shared-music", hasSize(1)))
                .andExpect(jsonPath("$[0].shared-music[0].id", is(song1Id.intValue())));
    }

    @Test
    @DisplayName("GET /user/{user-id}/recommendations should recommend songs based on similar users")
    void testGetRecommendations() throws Exception {
        // Bob has song 4 (Led Zeppelin) which Alice does not have
        mockMvc.perform(get("/user/" + aliceId + "/recommendations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].music.id", is(song4Id.intValue())))
                .andExpect(jsonPath("$[0].music.artist-name", is("Led Zeppelin")))
                .andExpect(jsonPath("$[0].recommendation-score", is(1)))
                .andExpect(jsonPath("$[0].recommended-by", contains("Bob")));
    }
}
