package com.musicservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) for creating or updating a user.
 *
 * Handles:
 * - POST /user: {"name": "name"}
 * - PATCH /user/{user-id}: {"name": "name", "music": "music-id"}
 *
 * Designed flexibly to accept "music" as:
 * - A numeric ID: {"music": 1}
 * - A string ID: {"music": "1"}
 * - An array of IDs: {"music": [1, 2]}
 */
public class UserRequest {

    @JsonProperty("name")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    private List<Long> musicIds;

    public UserRequest() {
    }

    public UserRequest(String name) {
        this.name = name;
    }

    public UserRequest(String name, List<Long> musicIds) {
        this.name = name;
        this.musicIds = musicIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("music")
    public List<Long> getMusicIds() {
        return musicIds;
    }

    @JsonSetter("music")
    public void setMusic(JsonNode node) {
        if (node == null || node.isNull()) {
            this.musicIds = null;
            return;
        }

        this.musicIds = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode item : node) {
                Long id = parseId(item);
                if (id != null) {
                    this.musicIds.add(id);
                }
            }
        } else {
            Long id = parseId(node);
            if (id != null) {
                this.musicIds.add(id);
            }
        }
    }

    private Long parseId(JsonNode node) {
        if (node.isNumber()) {
            return node.asLong();
        } else if (node.isTextual() && !node.asText().isBlank()) {
            try {
                return Long.parseLong(node.asText().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid music ID format: '" + node.asText() + "'. ID must be a valid number.");
            }
        }
        return null;
    }
}
