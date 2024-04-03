package com.team73.studyshare.service.Impl;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.DirectoryContent;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.Directory;
import com.team73.studyshare.model.data.File;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.repository.DirectoryRepository;
import com.team73.studyshare.repository.DocumentRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DirectoryServiceImpl implements DirectoryService {

    private final DirectoryRepository directoryRepository;
    private final ModuleRepository moduleRepository;
    private final DocumentRepository documentRepository;
    private final JwtService jwtService;

    @Autowired
    public DirectoryServiceImpl(DirectoryRepository directoryRepository, ModuleRepository moduleRepository, DocumentRepository documentRepository, JwtService jwtService) {
        this.directoryRepository = directoryRepository;
        this.moduleRepository = moduleRepository;
        this.documentRepository = documentRepository;
        this.jwtService = jwtService;
    }

    @Override
    public List<Directory> getAllDirectories() {
        Long requestingUserId = jwtService.extractIdFromToken();

        return directoryRepository.findAll().stream()
                .filter(directory -> {
                    if (directory.getVisibility() == Visibility.PUBLIC) {
                        return true;
                    }
                    return findModuleFromDirectory(directory)
                            .map(module -> module.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId)))
                            .orElse(false);
                })
                .toList();
    }

    @Override
    public Optional<Directory> getDirectoryById(Long directoryId) throws InvalidRequestException {
        Optional<Directory> directoryOptional = directoryRepository.findById(directoryId);

        if (directoryOptional.isEmpty()) {
            return Optional.empty();
        }

        Directory requestedDirectory = directoryOptional.get();
        Long requestingUserId = jwtService.extractIdFromToken();

        if (requestedDirectory.getVisibility().equals(Visibility.PUBLIC)) {
            return Optional.of(requestedDirectory);
        } else {
            Optional<Module> moduleOptional = findModuleFromDirectory(requestedDirectory);
            if (moduleOptional.isEmpty()) {
                throw new InvalidRequestException("No module in the database exists with this ID.");
            }
            if (moduleOptional.get().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
                return Optional.of(requestedDirectory);
            } else {
                throw new InvalidRequestException("You do not have permission to access this directory.");
            }
        }
    }

    @Override
    public DirectoryContent getDirectoryContent(Long directoryId) throws InvalidRequestException {
        Directory directory = directoryRepository.findById(directoryId)
                .orElseThrow(() -> new InvalidRequestException("Directory with ID " + directoryId + " does not exist."));

        Long requestingUserId = jwtService.extractIdFromToken();

        Optional<Module> moduleOptional = findModuleFromDirectory(directory);
        if (moduleOptional.isEmpty()) {
            throw new InvalidRequestException("No module in the database exists with this ID.");
        }

        if (moduleOptional.get().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            return new DirectoryContent(directory.getSubDirectories(), directory.getFiles());
        } else if(directory.getVisibility().equals(Visibility.PUBLIC)) {
            List<Directory> subDirectories = new ArrayList<>();
            List<File> files = new ArrayList<>();
            for (Directory subDirectory : directory.getSubDirectories()) {
                if (subDirectory.getVisibility().equals(Visibility.PUBLIC)) {
                    subDirectories.add(subDirectory);
                }
                for (File file : subDirectory.getFiles()) {
                    if (file.getVisibility().equals(Visibility.PUBLIC)) {
                        files.add(file);
                    }
                }
            }
            return new DirectoryContent(subDirectories, files);
        }
        throw new InvalidRequestException("You do not have permission to access this directory.");
    }

    @Override
    public Directory createDirectory(Directory directory) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();
        checkIfDirectoryIsInvalidForCreation(directory);

        Optional<Module> moduleOptional = findModuleFromDirectory(directory);
        if (moduleOptional.isEmpty()) {
            throw new InvalidRequestException("No module in the database exists with this ID.");
        }
        if (!moduleOptional.get().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to create this directory.");
        }
        directory.setCreatedAt(new Date());
        return directoryRepository.save(directory);
    }

    @Override
    public Optional<Directory> updateDirectory(Long directoryId, Directory updatedDirectory) throws InvalidRequestException {

        Long requestingUserId = jwtService.extractIdFromToken();

        if (updatedDirectory == null || updatedDirectory.getId() == null) {
            throw new InvalidRequestException("No 'id' is present in the Directory object or given directory is null.");
        }

        Optional<Module> moduleOptional = findModuleFromDirectory(updatedDirectory);
        if (moduleOptional.isEmpty()) {
            throw new InvalidRequestException("No module in the database exists with this ID.");
        }

        if (!moduleOptional.get().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to update this directory.");
        }

        if (!updatedDirectory.getId().equals(directoryId)) {
            throw new InvalidRequestException("The directoryId in the path variable does not match the id in the request body.");
        }

        checkIfDirectoryIsInvalidForCreation(updatedDirectory);

        return directoryRepository.findById(directoryId)
                .map(existingDirectory-> {
                    existingDirectory.setName(updatedDirectory.getName());
                    existingDirectory.setVisibility(updatedDirectory.getVisibility());

                    Directory savedDirectory = directoryRepository.save(existingDirectory);
                    return Optional.of(savedDirectory);
                })
                .orElseThrow(() -> new InvalidRequestException("Directory with ID " + directoryId + " does not exist."));
    }

    @Override
    public void deleteDirectory(Long directoryId) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();

        Optional<Directory> directoryOptional = directoryRepository.findById(directoryId);
        if (directoryOptional.isPresent()) {
            Directory directory = directoryOptional.get();

            Optional<Module> moduleOptional = findModuleFromDirectory(directory);
            if (moduleOptional.isEmpty()) {
                throw new InvalidRequestException("No module in the database exists with this ID.");
            }
            Module module = moduleOptional.get();
            if (!module.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
                throw new InvalidRequestException("You do not have permission to delete this directory.");
            }
            AtomicInteger deletions = new AtomicInteger();
            getAllSubDirectories(directory).forEach(dir -> {
                    dir.getFiles().forEach(file ->{
                        if(file.getDocumentId()!=null){
                            documentRepository.deleteById(file.getDocumentId());
                            deletions.getAndIncrement();
                        }
                });
            });

            directory.getFiles().forEach(file -> {
                if(file.getDocumentId()!=null){
                    documentRepository.deleteById(file.getDocumentId());
                    deletions.getAndIncrement();
                }
            });
            module.setFileCount(Math.max(module.getFileCount() - deletions.get(), 0));
            moduleRepository.save(module);
        }
        else {
            throw new InvalidRequestException("Directory with ID " + directoryId + " does not exist.");
        }
        directoryRepository.deleteById(directoryId);
    }

    private List<String> getInvalidFieldsForCreation(Directory directory) {
        List<String> invalidFields = new ArrayList<>();

        if (directory.getName() == null) {
            invalidFields.add("name");
        }

        if (directory.getVisibility() == null) {
            invalidFields.add("visibility");
        }

        if (directory.getCreator() == null) {
            invalidFields.add("creator");
        }

        return invalidFields;
    }

    private void checkIfDirectoryIsInvalidForCreation(Directory directory) throws InvalidRequestException {
        List<String> invalidFields = getInvalidFieldsForCreation(directory);

        if (!invalidFields.isEmpty()) {
            throw new InvalidRequestException("Invalid directory fields: " + String.join(", ", invalidFields));
        }
    }

    private Optional<Module> findModuleFromDirectory(Directory directory) {
        Directory mainDirectory = directory;
        while (mainDirectory.getMainDirectory() != null) {
            mainDirectory = mainDirectory.getMainDirectory();
        }
        return moduleRepository.findModuleByRootDirectoryId(mainDirectory.getId());
    }
    private List<Directory> getAllSubDirectories(Directory directory) {
        List<Directory> directoryList = new ArrayList<>();
        if(!directory.getSubDirectories().isEmpty()){
            directory.getSubDirectories().forEach(dir -> {
                directoryList.addAll(getAllSubDirectories(dir));
            });
        }
        directoryList.addAll(directory.getSubDirectories());
        return directoryList;
    }
}
