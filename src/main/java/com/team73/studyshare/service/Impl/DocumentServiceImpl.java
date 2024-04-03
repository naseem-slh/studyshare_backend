package com.team73.studyshare.service.Impl;

import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.File;
import com.team73.studyshare.repository.DocumentRepository;
import com.team73.studyshare.repository.FileRepository;
import com.team73.studyshare.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final FileRepository fileRepository;
    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(FileRepository fileRepository, DocumentRepository documentRepository) {
        this.fileRepository = fileRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public void deleteAllDocumentsFromModul(Long moduleId) {
        List<File> files = fileRepository.findByModuleId(moduleId);
        for (File file : files) {
            documentRepository.deleteById(file.getDocumentId());
        }
    }

    @Override
    public void deleteAllDocumentsFromCardSets(List<CardSet> cardSets) {
        cardSets.forEach(cardSet ->
                cardSet.getCards().forEach(card -> {
                    deleteDocumentIfPresent(card.getQuestion().getDocumentId());
                    deleteDocumentIfPresent(card.getAnswer().getDocumentId());
                })
        );
    }

    private void deleteDocumentIfPresent(Long documentId) {
        Optional.ofNullable(documentId).ifPresent(id -> documentRepository.deleteById(documentId));
    }

}
