package com.team73.studyshare.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {

    USER("User"),
    ADMIN("Admin");

    private final String id;

    Role(String id) {
        this.id = id;
    }

    @JsonValue
    public String getId() {
        return id;
    }
}
