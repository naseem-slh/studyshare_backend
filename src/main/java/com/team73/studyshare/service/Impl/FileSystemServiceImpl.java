package com.team73.studyshare.service.Impl;

import com.team73.studyshare.exception.StorageFileNotFoundException;
import com.team73.studyshare.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemServiceImpl implements FileSystemService {

    private static final String FILE_TYPE_PNG = "png";
    private static final String FILE_TYPE_PDF = "pdf";
    private static final String FILE_TYPE_JPG = "jpg";
    private final Path rootPath;

    @Autowired
    public FileSystemServiceImpl(@Value("${storage.path}") String path) {
        this.rootPath = Paths.get(path);
    }

    public Resource getFileFromPNGDirectory(String filename) throws StorageFileNotFoundException {
        Path path = rootPath.resolve(FILE_TYPE_PNG).resolve(filename);
        return loadAsResource(path, filename);
    }

    public Resource getFileFromPDFDirectory(String filename) throws StorageFileNotFoundException {
        Path path = rootPath.resolve(FILE_TYPE_PDF).resolve(filename);
        return loadAsResource(path, filename);
    }

    public Resource getFileFromJPGDirectory(String filename) throws StorageFileNotFoundException {
        Path path = rootPath.resolve(FILE_TYPE_JPG).resolve(filename);
        return loadAsResource(path, filename);
    }

    private Resource loadAsResource(Path pathToResource, String fileName) throws StorageFileNotFoundException {

        try {
            Resource resource = new UrlResource(pathToResource.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Malformed URL for file: " + fileName);
        }
    }
}
