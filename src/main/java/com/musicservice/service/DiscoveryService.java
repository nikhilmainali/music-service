package com.musicservice.service;

import com.musicservice.dto.TasteMatch;
import com.musicservice.dto.TasteRecommendation;
import com.musicservice.exception.ResourceNotFoundException;
import com.musicservice.model.Music;
import com.musicservice.model.User;
import com.musicservice.repository.MusicRepository;
import com.musicservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service providing core music and people discovery based on taste similarity.
 *
 * Implements:
 * 1. People Discovery: Find other users with overlapping music taste.
 * 2. Music Discovery: Recommend songs loved by people with similar taste.
 * 3. Song Fans: Find all users who love a specific song.
 */
@Service
public class DiscoveryService {

    private final UserRepository userRepository;
    private final MusicRepository musicRepository;

    public DiscoveryService(UserRepository userRepository, MusicRepository musicRepository) {
        this.userRepository = userRepository;
        this.musicRepository = musicRepository;
    }

    /**
     * Discover other users who share similar music taste.
     * Uses Jaccard Similarity: |Intersection| / |Union| * 100%
     */
    @Transactional(readOnly = true)
    public List<TasteMatch> findSimilarUsers(Long userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        Set<Long> targetMusic = targetUser.getMusic();
        if (targetMusic.isEmpty()) {
            return Collections.emptyList();
        }

        List<User> allUsers = userRepository.findAll();
        List<TasteMatch> matches = new ArrayList<>();

        for (User otherUser : allUsers) {
            if (otherUser.getId().equals(userId)) {
                continue; // Skip the user themselves
            }

            Set<Long> otherMusic = otherUser.getMusic();
            if (otherMusic.isEmpty()) {
                continue;
            }

            // Find overlapping songs (Intersection)
            Set<Long> sharedIds = new HashSet<>(targetMusic);
            sharedIds.retainAll(otherMusic);

            if (!sharedIds.isEmpty()) {
                // Find all unique songs between both users (Union)
                Set<Long> unionIds = new HashSet<>(targetMusic);
                unionIds.addAll(otherMusic);

                double rawSimilarity = ((double) sharedIds.size() / unionIds.size()) * 100.0;
                double similarity = BigDecimal.valueOf(rawSimilarity)
                        .setScale(1, RoundingMode.HALF_UP)
                        .doubleValue();

                List<Music> sharedSongs = musicRepository.findAllById(sharedIds);

                matches.add(new TasteMatch(
                        otherUser.getId(),
                        otherUser.getName(),
                        sharedIds.size(),
                        similarity,
                        sharedSongs
                ));
            }
        }

        // Sort by similarity percentage descending, then by shared tracks count descending
        matches.sort((a, b) -> {
            int comp = Double.compare(b.getSimilarityPercentage(), a.getSimilarityPercentage());
            if (comp != 0) return comp;
            return Integer.compare(b.getSharedMusicCount(), a.getSharedMusicCount());
        });

        return matches;
    }

    /**
     * Discover new music recommended by users with similar taste.
     * Looks at tracks owned by users who share at least one track with the target user,
     * but which the target user has not added yet.
     */
    @Transactional(readOnly = true)
    public List<TasteRecommendation> recommendMusic(Long userId) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        Set<Long> targetMusic = targetUser.getMusic();

        List<TasteMatch> similarUsers = findSimilarUsers(userId);
        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // Map musicId -> list of user names who have it
        Map<Long, List<String>> candidateTracks = new HashMap<>();

        for (TasteMatch match : similarUsers) {
            User otherUser = userRepository.findById(match.getUserId()).orElse(null);
            if (otherUser == null) continue;

            for (Long candidateMusicId : otherUser.getMusic()) {
                // Only consider tracks the current user does not already have
                if (!targetMusic.contains(candidateMusicId)) {
                    candidateTracks.computeIfAbsent(candidateMusicId, k -> new ArrayList<>())
                            .add(otherUser.getName());
                }
            }
        }

        List<TasteRecommendation> recommendations = new ArrayList<>();
        for (Map.Entry<Long, List<String>> entry : candidateTracks.entrySet()) {
            Music music = musicRepository.findById(entry.getKey()).orElse(null);
            if (music != null) {
                recommendations.add(new TasteRecommendation(
                        music,
                        entry.getValue().size(),
                        entry.getValue()
                ));
            }
        }

        // Sort by recommendation score descending (most common among similar friends first)
        recommendations.sort((a, b) -> Integer.compare(b.getRecommendationScore(), a.getRecommendationScore()));

        return recommendations;
    }

    /**
     * Discover all users who have a specific song in their music list.
     */
    @Transactional(readOnly = true)
    public List<User> getMusicFans(Long musicId) {
        if (!musicRepository.existsById(musicId)) {
            throw new ResourceNotFoundException("Music with ID " + musicId + " not found");
        }
        return userRepository.findUsersByMusicId(musicId);
    }
}
