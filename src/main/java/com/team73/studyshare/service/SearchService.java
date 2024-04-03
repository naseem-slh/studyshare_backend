package com.team73.studyshare.service;


import com.team73.studyshare.model.ItemType;

import java.util.List;

/**
 * Service interface for searching different types of items.
 */
public interface SearchService {

    /**
     * Search for items based on the provided query and item type.
     *
     * @param query The search query.
     * @param type  The type of item to search for (e.g., User, CardSet, Module, File).
     * @return A list of items matching the search criteria.
     * @throws IllegalArgumentException If an invalid item type is provided.
     */
    List<?> searchItems(String query, ItemType type);
}
