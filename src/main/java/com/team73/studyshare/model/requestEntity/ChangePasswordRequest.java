package com.team73.studyshare.model.requestEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request for changing a user's password.
 */
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    /**
     * The user's current password.
     */
    private String currentPassword;

    /**
     * The new password to be set.
     */
    private String newPassword;
}
