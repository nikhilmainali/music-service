package com.musicservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User Entity represents a user in the music service.
 *
 * Each user has:
 * - id: unique numeric ID (auto-generated)
 * - name: user's display name
 * - music: list of music IDs that the user has added to their library
 *
 * Annotations explained for beginners:
 * - @ElementCollection: Maps a collection of simple types (Long music IDs) to a
 *   child table ("user_music") without needing a full separate entity class.
 * - @CollectionTable: Configures the join table name ("user_music") and foreign key ("user_id").
 * - @Column: Maps each element to the "music_id" column.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @JsonProperty("name")
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_music", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "music_id")
    @JsonProperty("music")
    private Set<Long> music = new LinkedHashSet<>();

    // Default constructor required by JPA
    public User() {
    }

    public User(String name) {
        this.name = name;
    }

    public User(Long id, String name, Set<Long> music) {
        this.id = id;
        this.name = name;
        if (music != null) {
            this.music = new LinkedHashSet<>(music);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getMusic() {
        return music;
    }

    public void setMusic(Set<Long> music) {
        this.music = music != null ? new LinkedHashSet<>(music) : new LinkedHashSet<>();
    }

    public void addMusic(Long musicId) {
        if (this.music == null) {
            this.music = new LinkedHashSet<>();
        }
        this.music.add(musicId);
    }

    public boolean removeMusic(Long musicId) {
        if (this.music != null) {
            return this.music.remove(musicId);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", music=" + music +
                '}';
    }
}
