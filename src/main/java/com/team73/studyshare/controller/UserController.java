package com.team73.studyshare.controller;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.model.requestEntity.ChangePasswordRequest;
import com.team73.studyshare.model.responseEntity.UserResponse;
import com.team73.studyshare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        Optional<User> userOptional;
        try {
            userOptional = userService.getUserById(userId);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return userOptional
                .map(user -> ResponseEntity.ok().body(user))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{userId}/modules")
    public ResponseEntity<List<Module>> getModulesForUser(@PathVariable Long userId) {
        try {
            List<Module> modules = userService.getModulesForUser(userId);
            return ResponseEntity.ok(modules);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/last-quizzed")
    public ResponseEntity<List<CardSet>> getLastQuizzedCardSets() {
        try {
            List<CardSet> cardSets = userService.getLastQuizzedCardSets();
            return ResponseEntity.ok(cardSets);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody User user) {
        try {
            Optional<User> updatedUser = userService.updateUser(userId, user);
            if (updatedUser.isPresent()) {
                UserResponse userResponse = new UserResponse(updatedUser.get(), null);
                return ResponseEntity.status(HttpStatus.OK).body(userResponse);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            UserResponse userResponse = new UserResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userResponse);
        }
    }

    @PutMapping("/{cardSetId}/last-quizzed")
    public ResponseEntity<UserResponse> updateQuizzedCardSetToUser(@PathVariable Long cardSetId) {
        try {
            Optional<User> updatedUser = userService.addQuizzedCardSetToUser(cardSetId);
            if (updatedUser.isPresent()) {
                UserResponse userResponse = new UserResponse(updatedUser.get(), null);
                return ResponseEntity.status(HttpStatus.OK).body(userResponse);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        try {
            return userService.deleteUser(userId) ?
                    ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PatchMapping("change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
