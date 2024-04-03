package com.team73.studyshare.model.responseEntity;

import com.team73.studyshare.model.data.User;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A data transfer object representing a user response.
 * This class is used to encapsulate user information and an optional error message.
 */
@Data
@AllArgsConstructor
public class UserResponse {
    /**
     * The user object containing user information.
     */
    private User user;

    /**
     * An error message, if applicable. This message can provide additional details
     * when an operation results in an error.
     */
    private String errorMessage;
}
