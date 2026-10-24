package com.musicservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicservice.model.Music;

import java.util.List;

/**
 * DTO representing another user with similar music taste.
 * Used for "discovery of people".
 */
public class TasteMatch {

    @JsonProperty("user-id")
    private Long userId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("shared-tracks-count")
    private int sharedMusicCount;

    @JsonProperty("similarity-percentage")
    private double similarityPercentage;

    @JsonProperty("shared-music")
    private List<Music> sharedMusic;

    public TasteMatch() {
    }

    public TasteMatch(Long userId, String name, int sharedMusicCount, double similarityPercentage, List<Music> sharedMusic) {
        this.userId = userId;
        this.name = name;
        this.sharedMusicCount = sharedMusicCount;
        this.similarityPercentage = similarityPercentage;
        this.sharedMusic = sharedMusic;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSharedMusicCount() {
        return sharedMusicCount;
    }

    public void setSharedMusicCount(int sharedMusicCount) {
        this.sharedMusicCount = sharedMusicCount;
    }

    public double getSimilarityPercentage() {
        return similarityPercentage;
    }

    public void setSimilarityPercentage(double similarityPercentage) {
        this.similarityPercentage = similarityPercentage;
    }

    public List<Music> getSharedMusic() {
        return sharedMusic;
    }

    public void setSharedMusic(List<Music> sharedMusic) {
        this.sharedMusic = sharedMusic;
    }
}
