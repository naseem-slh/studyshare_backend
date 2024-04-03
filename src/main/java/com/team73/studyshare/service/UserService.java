package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.model.requestEntity.ChangePasswordRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * This interface defines the operations that can be performed on User objects.
 * Implementations of this interface provide the business logic for user-related actions.
 */
public interface UserService {

    /**
     * Retrieves a list of all users.
     *
     * @return A list of User objects.
     */
    List<User> getAllUsers();

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param userId The unique identifier of the user.
     * @return An Optional containing the User object if found, or an empty Optional if not found.
     * @throws InvalidRequestException if the request is invalid.
     */
    Optional<User> getUserById(Long userId) throws InvalidRequestException;

    /**
     * Deletes a user by their unique identifier.
     *
     * @param userId The unique identifier of the user to be deleted.
     * @return `true` if the user was successfully deleted, or `false` if the user was not found.
     * @throws InvalidRequestException if the requesting user does not have the permission to delete the module.
     */
    boolean deleteUser(Long userId) throws InvalidRequestException;

    /**
     * Updates user information with the provided User object.
     *
     * @param userId The unique identifier of the user to be updated.
     * @param user   The updated User object.
     * @return An Optional containing the updated User object if the user was found and updated successfully,
     * or an empty Optional if the user was not found.
     * @throws InvalidRequestException if the provided User object is incomplete or invalid.
     */
    Optional<User> updateUser(Long userId, User user) throws InvalidRequestException;

    /**
     * Saves a user in the system.
     *
     * @param user The user to be saved.
     * @return The saved user.
     * @throws InvalidRequestException if the requesting user does not have the permission to delete the module.
     */
    User createUser(User user) throws InvalidRequestException;

    /**
     * Changes the password for a user based on the provided request and the connected user's Principal.
     *
     * @param request       The request containing the new password and user information.
     * @param connectedUser The Principal representing the currently connected user.
     */
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    /**
     * Retrieves a list of modules associated with a user based on the user's unique identifier.
     *
     * @param userId The unique identifier of the user for whom to retrieve the modules.
     * @return A list of modules associated with the specified user.
     * @throws InvalidRequestException if the provided user ID is invalid or if the user is not found.
     */
    List<Module> getModulesForUser(Long userId) throws InvalidRequestException;

    List<CardSet> getLastQuizzedCardSets() throws InvalidRequestException;

    Optional<User> addQuizzedCardSetToUser(Long cardSetId) throws InvalidRequestException;
}

