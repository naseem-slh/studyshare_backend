package com.team73.studyshare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.Document;
import com.team73.studyshare.model.data.File;
import com.team73.studyshare.service.Impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/files")
public class FileController {

    private final FileServiceImpl fileService;
    private final ObjectMapper objectMapper;

    @Autowired
    public FileController(FileServiceImpl fileService, ObjectMapper objectMapper) {
        this.fileService = fileService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<File>> getAllFiles() {
        try {
            List<File> allFiles = fileService.getAllFiles();
            if (!allFiles.isEmpty()) {
                return ResponseEntity.ok(allFiles);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<?> getDocument(@PathVariable Long documentId) {
        try {
            Optional<Document> documentOptional = fileService.getDocumentById(documentId);
            return documentOptional.map(document -> ResponseEntity.ok()
                            .contentType(MediaType.valueOf(document.getType().getId()))
                            .body(document.getData()))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("fileResource") String fileMetadata
    ) {
        try {
            File fileResource = objectMapper.readValue(fileMetadata, File.class);
            File createdFile = fileService.createFile(file, fileResource);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFile);
        } catch (IOException | InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<File> updateFile(@PathVariable long fileId, @RequestBody File file) {
        try {
            Optional<File> updated = fileService.updateFile(fileId, file);
            return updated.map(updatedFile -> ResponseEntity.ok().body(file)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        try {
            fileService.deleteFile(fileId);
            return ResponseEntity.noContent().build();
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
}