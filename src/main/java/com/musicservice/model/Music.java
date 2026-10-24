package com.musicservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Music Entity represents a song in the music catalog.
 *
 * Annotations explained for beginners:
 * - @Entity: Marks this Java class as a JPA entity that maps to a database
 * table.
 * - @Table: Specifies the name of the database table ("musics").
 * - @Id: Marks the primary key field.
 * - @GeneratedValue: Tells the database to automatically generate and increment
 * the ID.
 * - @JsonProperty: Maps Java camelCase ("artistName") to JSON kebab-case
 * ("artist-name")
 * so the API JSON matches the exact specification.
 */
@Entity
@Table(name = "musics")
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("artist-name")
    @Column(name = "artist_name", nullable = false)
    private String artistName;

    @JsonProperty("genre-name")
    @Column(name = "genre_name", nullable = false)
    private String genreName;

    @JsonProperty("link")
    @Column(name = "link", nullable = false)
    private String link;

    // Default constructor required by JPA
    public Music() {
    }

    public Music(String artistName, String genreName, String link) {
        this.artistName = artistName;
        this.genreName = genreName;
        this.link = link;
    }

    public Music(Long id, String artistName, String genreName, String link) {
        this.id = id;
        this.artistName = artistName;
        this.genreName = genreName;
        this.link = link;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Music music = (Music) o;
        return Objects.equals(id, music.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Music{" +
                "id=" + id +
                ", artist'sName='" + artistName + '\'' +
                ", genre's Name='" + genreName + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
