package com.musicservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musicservice.model.Music;

import java.util.List;

/**
 * DTO representing music recommendations discovered through people with similar taste.
 * Used for "discovery of music".
 */
public class TasteRecommendation {

    @JsonProperty("music")
    private Music music;

    @JsonProperty("recommendation-score")
    private int recommendationScore;

    @JsonProperty("recommended-by")
    private List<String> recommendedByUsers;

    public TasteRecommendation() {
    }

    public TasteRecommendation(Music music, int recommendationScore, List<String> recommendedByUsers) {
        this.music = music;
        this.recommendationScore = recommendationScore;
        this.recommendedByUsers = recommendedByUsers;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public int getRecommendationScore() {
        return recommendationScore;
    }

    public void setRecommendationScore(int recommendationScore) {
        this.recommendationScore = recommendationScore;
    }

    public List<String> getRecommendedByUsers() {
        return recommendedByUsers;
    }

    public void setRecommendedByUsers(List<String> recommendedByUsers) {
        this.recommendedByUsers = recommendedByUsers;
    }
}
