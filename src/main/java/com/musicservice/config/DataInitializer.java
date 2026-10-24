package com.musicservice.config;

import com.musicservice.model.Music;
import com.musicservice.model.User;
import com.musicservice.repository.MusicRepository;
import com.musicservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

/**
 * Pre-populates the in-memory H2 database with initial sample songs and users.
 *
 * Concepts for beginners:
 * - @Configuration: Declares this class as a source of bean definitions and setup code.
 * - CommandLineRunner: A Spring Boot interface with a run() method executed once
 *   the application context has loaded, right before the server starts accepting requests.
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;

    public DataInitializer(MusicRepository musicRepository, UserRepository userRepository) {
        this.musicRepository = musicRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (musicRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data load.");
            return;
        }

        log.info("Initializing sample music catalog and users...");

        // Seed sample music catalog
        Music song1 = new Music("Daft Punk", "Electronic", "https://open.spotify.com/track/4Cee00Dyq308ZgfSd9337b");
        Music song2 = new Music("Miles Davis", "Jazz", "https://open.spotify.com/track/4vfpfdUgvnLgUm0R204XkW");
        Music song3 = new Music("Taylor Swift", "Pop", "https://open.spotify.com/track/0V3wPSX9Ir6xc75YJh92cf");
        Music song4 = new Music("Led Zeppelin", "Rock", "https://open.spotify.com/track/5CQ30WqJwcep0pYcV4AMNc");
        Music song5 = new Music("Daft Punk", "Electronic", "https://open.spotify.com/track/0DiWol3AO6WpXZgp0gGlOX");
        Music song6 = new Music("Dave Brubeck", "Jazz", "https://open.spotify.com/track/1YQWosTIljIvxAgHWTpDlR");

        musicRepository.saveAll(List.of(song1, song2, song3, song4, song5, song6));

        // Seed sample users with initial music preferences
        // Alice loves electronic music and pop
        User alice = new User("Alice");
        alice.setMusic(Set.of(song1.getId(), song5.getId(), song3.getId()));

        // Bob also loves electronic music and classic rock (High taste overlap with Alice!)
        User bob = new User("Bob");
        bob.setMusic(Set.of(song1.getId(), song5.getId(), song4.getId()));

        // Charlie is a jazz enthusiast
        User charlie = new User("Charlie");
        charlie.setMusic(Set.of(song2.getId(), song6.getId()));

        // Dana enjoys pop and rock
        User dana = new User("Dana");
        dana.setMusic(Set.of(song3.getId(), song4.getId()));

        userRepository.saveAll(List.of(alice, bob, charlie, dana));

        log.info("Sample data initialized! {} songs and {} users loaded.",
                musicRepository.count(), userRepository.count());
    }
}
