package com.musicservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for creating or updating a song.
 *
 * Using DTOs is a best practice because it separates the API contract
 * from the internal database entity model.
 */
public class MusicRequest {

    @JsonProperty("artist-name")
    @Size(max = 255, message = "Artist name must be at most 255 characters")
    private String artistName;

    @JsonProperty("genre-name")
    @Size(max = 100, message = "Genre name must be at most 100 characters")
    private String genreName;

    @JsonProperty("link")
    @Size(max = 1024, message = "Link must be at most 1024 characters")
    private String link;

    public MusicRequest() {
    }

    public MusicRequest(String artistName, String genreName, String link) {
        this.artistName = artistName;
        this.genreName = genreName;
        this.link = link;
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
}
