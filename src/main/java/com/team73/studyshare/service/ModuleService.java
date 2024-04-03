package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Module;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing modules.
 */
public interface ModuleService {

    /**
     * Creates a new module.
     *
     * @param module The module to be created.
     * @return The created module.
     * @throws InvalidRequestException if the request to create the module is invalid.
     */
    Module createModule(Module module) throws InvalidRequestException;

    /**
     * Retrieves a module by its ID.
     *
     * @param moduleId The ID of the module to retrieve.
     * @return An Optional containing the retrieved module, or empty if not found.
     * @throws InvalidRequestException if the request is invalid.
     */
    Optional<Module> getModuleById(Long moduleId) throws InvalidRequestException;

    /**
     * Retrieves all modules.
     *
     * @return A list of all modules.
     */
    List<Module> getAllModules();

    /**
     * Updates an existing module.
     *
     * @param moduleId      The ID of the module to be updated.
     * @param updatedModule The updated module object.
     * @return The updated module.
     * @throws InvalidRequestException if the request to update the module is invalid.
     */
    Optional<Module> updateModule(Long moduleId, Module updatedModule) throws InvalidRequestException;

    /**
     * Updates the score of an existing module based on the scores of its associated card sets.
     *
     * @param moduleId The ID of the module to update the score.
     * @return An Optional containing the updated module if found.
     * @throws InvalidRequestException If the module with the specified ID does not exist.
     */
    Optional<Module> updateScoreOfModule(Long moduleId) throws InvalidRequestException;

    /**
     * Deletes a module by its ID.
     *
     * @param moduleId The ID of the module to be deleted.
     * @throws InvalidRequestException if the requesting user does not have the permission to delete the module.
     */
    void deleteModule(Long moduleId) throws InvalidRequestException;

    /**
     * Retrieves a list of card sets associated with a specific module.
     *
     * @param moduleId The unique identifier of the module.
     * @return A {@link List} of {@link CardSet} objects representing the card sets within the specified module.
     * @throws InvalidRequestException If an error occurs during the retrieval of card sets or if the module with
     *                                 the specified ID does not exist.
     */
    List<CardSet> getCardSetsFromModule(Long moduleId) throws InvalidRequestException;

}

//TODO: Nochmal überall durchgehen und Permission in InvalidRequestException hinzufügen