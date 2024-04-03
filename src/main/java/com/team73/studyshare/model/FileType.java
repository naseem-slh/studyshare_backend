package com.team73.studyshare.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FileType {

    PDF("application/pdf"),
    PNG("image/png"),
    JPG("image/jpeg"),
    UNKNOWN("Unknown");

    private final String id;

    FileType(String id) {
        this.id = id;
    }

    @JsonValue
    public String getId() {
        return id;
    }

    public static FileType getFileTypeById(String id) {
        for (FileType fileType : values()) {
            if (fileType.id.equals(id)) {
                return fileType;
            }
        }
        return UNKNOWN;
    }

    public static boolean isImage(String id) {
        for (FileType fileType : values()) {
            if (fileType.id.equals(PNG.getId()) || (fileType.id.equals(JPG.getId()))) {
                return true;
            }
        }
        return false;
    }
}