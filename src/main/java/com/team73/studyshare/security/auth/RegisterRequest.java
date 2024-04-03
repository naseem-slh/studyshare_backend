package com.team73.studyshare.security.auth;

import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private Visibility visibility;
    private Role role;
    private String description;
}
