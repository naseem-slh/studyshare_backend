package com.team73.studyshare.controller;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.DirectoryContent;
import com.team73.studyshare.model.data.Directory;
import com.team73.studyshare.service.DirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/directories")
public class DirectoryController {
    private final DirectoryService directoryService;

    @Autowired
    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping
    public ResponseEntity<List<Directory>> getAllDirectories() {
        List<Directory> directories = directoryService.getAllDirectories();
        return ResponseEntity.ok().body(directories);
    }

    @GetMapping("/{directoryId}")
    public ResponseEntity<Directory> getDirectoryById(@PathVariable long directoryId) {
        Optional<Directory> directoryOptional;
        try {
            directoryOptional = directoryService.getDirectoryById(directoryId);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return directoryOptional
                .map(directory -> ResponseEntity.ok().body(directory))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{directoryId}/directory-content")
    public ResponseEntity<DirectoryContent> getDirectoryContent(@PathVariable long directoryId) {
        try {
            DirectoryContent result = directoryService.getDirectoryContent(directoryId);
            return ResponseEntity.ok().body(result);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Directory> createDirectory(@RequestBody Directory newDirectory) {
        try {
            Directory createdDirectory = directoryService.createDirectory(newDirectory);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDirectory);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{directoryId}")
    public ResponseEntity<Directory> updateDirectory(@PathVariable long directoryId, @RequestBody Directory updatedDirectory) {
        try {
            Optional<Directory> updated = directoryService.updateDirectory(directoryId, updatedDirectory);
            return updated.map(directory -> ResponseEntity.ok().body(directory)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{directoryId}")
    public ResponseEntity<Void> deleteDirectory(@PathVariable long directoryId) {
        try {
            directoryService.deleteDirectory(directoryId);
            return ResponseEntity.noContent().build();
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
}

