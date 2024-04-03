package com.team73.studyshare.service.Impl;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.FileType;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.File;
import com.team73.studyshare.model.data.Document;
import com.team73.studyshare.repository.FileRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.repository.DocumentRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final ModuleRepository moduleRepository;
    private final DocumentRepository documentRepository;
    private final JwtService jwtService;

    public FileServiceImpl(FileRepository fileRepository, ModuleRepository moduleRepository, DocumentRepository documentRepository, JwtService jwtService) {
        this.fileRepository = fileRepository;
        this.moduleRepository = moduleRepository;
        this.documentRepository = documentRepository;
        this.jwtService = jwtService;
    }

    @Override
    public List<File> getAllFiles() {
        Long requestingUserId = jwtService.extractIdFromToken();

        return fileRepository.findAll()
                .stream()
                .filter(file -> file.getVisibility() == Visibility.PUBLIC
                        || file.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId)))
                .toList();
    }

    @Override
    public Optional<Document> getDocumentById(Long documentId) throws InvalidRequestException {
        return documentRepository.findById(documentId);
    }

    @Override
    public File createFile(MultipartFile multipartFile, File fileResource) throws InvalidRequestException, IOException {

        if (fileIsIncompleteForCreation(fileResource)) {
            throw new InvalidRequestException("One or more properties are not set in the request.");
        }
        Long requestingUserId = jwtService.extractIdFromToken();
        if(!fileResource.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))){
            throw new InvalidRequestException("You do not have permission to create the file.");
        }

        String name = fileResource.getName();
        Visibility visibility = fileResource.getVisibility();

        String fileName = (name == null || name.isEmpty())? multipartFile.getOriginalFilename() : name;

        Long moduleId = fileResource.getModule().getId();

        Module moduleDB = moduleRepository.findById(moduleId).orElseThrow(() ->
                new InvalidRequestException("ModuleID of Module does not exist."));

        moduleDB.incrementFileCount();
        moduleRepository.save(moduleDB);

        Document document = Document.builder()
                .data(multipartFile.getBytes())
                .type(FileType.getFileTypeById(multipartFile.getContentType()))
                .build();

        documentRepository.save(document);

         return fileRepository.save(
                File.builder()
                        .name(fileName)
                        .createdAt(new Date())
                        .visibility(visibility)
                        .documentId(document.getId())
                        .directory(fileResource.getDirectory())
                        .creator(fileResource.getCreator())
                        .module(moduleDB)
                        .type(FileType.getFileTypeById(multipartFile.getContentType()))
                        .build());
    }

    @Override
    public Optional<File> updateFile(Long fileId, File fileResource) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();

        if (fileResource == null || fileResource.getId() == null) {
            throw new InvalidRequestException("No 'id' is present in the File object or given fileResource is null.");
        }

        if (!fileResource.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to update this file.");
        }

        if (!fileResource.getId().equals(fileId)) {
            throw new InvalidRequestException("The File in the path variable does not match the id in the request body.");
        }
        if (fileIsIncompleteForUpdate(fileResource)) {
            throw new InvalidRequestException("One or more properties are not set in the request.");
        }

        return fileRepository.findById(fileId)
                .map(existingFile -> {
                    existingFile.setName(fileResource.getName());
                    existingFile.setVisibility(fileResource.getVisibility());
                    File savedFile = fileRepository.save(existingFile);
                    return Optional.of(savedFile);
                })
                .orElseThrow(() -> new InvalidRequestException("File with ID " + fileId + " does not exist."));
    }

    @Override
    public boolean deleteFile(Long fileId) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();

        File fileResource = fileRepository.findById(fileId).orElseThrow(() ->
                new InvalidRequestException("File with ID " + fileId + " not found."));

        if (!fileResource.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to delete this file.");
        }

        //for file count
        Long moduleId = fileResource.getModule().getId();

        Module moduleDB = moduleRepository.findById(moduleId).orElseThrow(() ->
                new InvalidRequestException("ModuleID of Module does not exist."));

        moduleDB.decrementFileCount();
        moduleRepository.save(moduleDB);
        //delete
        fileRepository.deleteById(fileId);
        documentRepository.deleteById(fileResource.getDocumentId());
        return true;
    }

    private boolean fileIsIncompleteForCreation(File fileResource) {
        return fileResource.getVisibility() == null ||
                fileResource.getDirectory() == null ||
                fileResource.getCreator() == null;
    }

    private boolean fileIsIncompleteForUpdate(File fileResource) {
        return fileIsIncompleteForCreation(fileResource) ||
                fileResource.getName() == null;
    }
}
