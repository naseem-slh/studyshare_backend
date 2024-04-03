package com.team73.studyshare.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Visibility {

    PRIVATE("Private"),
    PUBLIC("Public");

    private final String id;

    Visibility(String id) {
        this.id = id;
    }

    @JsonValue
    public String getId() {
        return id;
    }

    public static Visibility getVisibilityTypeById(String id) {
        if (PUBLIC.getId().equalsIgnoreCase(id)) {
            return Visibility.PUBLIC;
        }
        return Visibility.PRIVATE;
    }

}

