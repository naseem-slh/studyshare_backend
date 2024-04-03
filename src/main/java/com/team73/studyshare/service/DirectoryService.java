package com.team73.studyshare.service;

import com.team73.studyshare.model.DirectoryContent;
import com.team73.studyshare.model.data.Directory;
import com.team73.studyshare.exception.InvalidRequestException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing directories.
 */
public interface DirectoryService {

    /**
     * Creates a new directory.
     *
     * @param directory The directory to be created.
     * @return The created directory.
     * @throws InvalidRequestException if the request is invalid.
     */
    Directory createDirectory(Directory directory) throws InvalidRequestException;

    /**
     * Retrieves a directory by its ID.
     *
     * @param directoryId The ID of the directory to retrieve.
     * @return An Optional containing the retrieved directory, or empty if not found.
     * @throws InvalidRequestException if the request is invalid.
     */
    Optional<Directory> getDirectoryById(Long directoryId) throws InvalidRequestException;

    /**
     * Retrieves all directories.
     *
     * @return A list of all directories.
     */
    List<Directory> getAllDirectories();

    /**
     * Updates an existing directory.
     *
     * @param directoryId      The ID of the directory to be updated.
     * @param updatedDirectory The updated directory object.
     * @return The updated directory.
     * @throws InvalidRequestException if the request is invalid.
     */
    Optional<Directory> updateDirectory(Long directoryId, Directory updatedDirectory) throws InvalidRequestException;

    /**
     * Deletes a directory by its ID.
     *
     * @param directoryId The ID of the directory to be deleted.
     * @throws InvalidRequestException if the request is invalid.
     */
    void deleteDirectory(Long directoryId) throws InvalidRequestException;

    /**
     * Retrieves the content of a directory, including its subdirectories and files.
     *
     * @param directoryId The unique identifier of the directory.
     * @return A {@link DirectoryContent} object representing the content of the directory.
     * @throws InvalidRequestException If the directory with the specified ID does not exist.
     */
    DirectoryContent getDirectoryContent(Long directoryId) throws InvalidRequestException;
}
