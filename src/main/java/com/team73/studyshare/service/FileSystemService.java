package com.team73.studyshare.service;

import com.team73.studyshare.exception.StorageFileNotFoundException;
import org.springframework.core.io.Resource;

/**
 * The interface defines the contract for services
 * related to file operations within the application.
 */
public interface FileSystemService {

    /**
     * Retrieves a PNG file resource from the designated directory.
     *
     * @param filename The name of the PNG file to be retrieved.
     * @return The PNG file as a resource.
     * @throws StorageFileNotFoundException If the specified PNG file is not found.
     */
    Resource getFileFromPNGDirectory(String filename) throws StorageFileNotFoundException;

    /**
     * Retrieves a PDF file resource from the designated directory.
     *
     * @param filename The name of the PDF file to be retrieved.
     * @return The PDF file as a resource.
     * @throws StorageFileNotFoundException If the specified PDF file is not found.
     */
    Resource getFileFromPDFDirectory(String filename) throws StorageFileNotFoundException;

    /**
     * Retrieves a JPG file resource from the designated directory.
     *
     * @param filename The name of the JPG file to be retrieved.
     * @return The JPG file as a resource.
     * @throws StorageFileNotFoundException If the specified JPG file is not found.
     */
    Resource getFileFromJPGDirectory(String filename) throws StorageFileNotFoundException;
}
