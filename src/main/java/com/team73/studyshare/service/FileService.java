package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.Document;
import com.team73.studyshare.model.data.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * This interface defines the operations that can be performed on File objects.
 * Implementations of this interface provide the business logic for file-related actions.
 */
public interface FileService {

    /**
     * Retrieves a list of all files.
     *
     * @return A list of File objects.
     */
    List<File> getAllFiles();

    /**
     * Retrieves a document by its unique file ID.
     *
     * @param fileId The unique file ID of the document to retrieve.
     * @return An Optional containing the found document if it exists, or Optional.empty() otherwise.
     * @throws InvalidRequestException if an invalid request or error occurs.
     */
    Optional<Document> getDocumentById(Long fileId) throws InvalidRequestException;

     /**
      * Deletes a file by its unique identifier.
      *
      * @param fileId The unique identifier of the file to be deleted.
      * @return `true` if the file was successfully deleted, or `false` if the file was not found.
      */
    boolean deleteFile(Long fileId) throws InvalidRequestException;

    /**
     * Creates a new file with the provided data.
     *
     * @param multipartFile The binary data of the file.
     * @param file          Represents a file in a directory.
     * @return The created File object.
     * @throws IOException             if there is an issue with file I/O.
     * @throws InvalidRequestException if the provided data is incomplete or invalid.
     */
    File createFile(MultipartFile multipartFile, File file) throws IOException, InvalidRequestException;


    /**
    * Updates a file with the provided data.
    *
    * @param fileId      The unique identifier of the file to be updated.
    * @param updatedFile The updated File object.
    * @return The updated File object.
    * @throws InvalidRequestException      if the provided data is incomplete or invalid.
    *
    */
    Optional<File> updateFile(Long fileId, File updatedFile) throws InvalidRequestException;
}


