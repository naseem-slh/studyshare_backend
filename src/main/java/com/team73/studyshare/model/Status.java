package com.team73.studyshare.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {

    UNDONE("Undone"),
    BAD("Bad"),
    OK("Ok"),
    GOOD("Good");

    private final String id;

    Status(String id) {
        this.id = id;
    }

    @JsonValue
    public String getId() {
        return id;
    }

    public static boolean isStatus(String id) {
        for (Status status : Status.values()) {
            if (status.id.equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }
}