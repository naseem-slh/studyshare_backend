package com.team73.studyshare.service;

import com.team73.studyshare.model.data.CardSet;

import java.util.List;

/**
 * Interface for document management services in the StudyShare application.
 * Provides methods for operations related to documents, such as deleting all
 * documents associated with a specific module.
 */
public interface DocumentService {

    /**
     * Deletes all documents belonging to a specified module.
     *
     * @param moduleId The ID of the module whose documents are to be deleted.
     */
    void deleteAllDocumentsFromModul(Long moduleId);

    /**
     * Deletes all documents associated with specified card sets.
     * This method is used to remove all documents linked to each card set in the provided list,
     * identified by their respective IDs.
     *
     * @param cardSets A list of CardSet objects whose associated documents are to be deleted.
     */
    void deleteAllDocumentsFromCardSets(List<CardSet> cardSets);
}
