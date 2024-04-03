package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.Card;
import com.team73.studyshare.model.data.CardSet;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing {@link CardSet} entities.
 */
public interface CardSetService {

    /**
     * Creates a new card set.
     *
     * @param cardSet The card set to be created.
     * @return The created card set.
     * @throws InvalidRequestException If the card set is invalid for creation.
     */
    CardSet createCardSet(CardSet cardSet) throws InvalidRequestException;

    /**
     * Retrieves a card set by its ID.
     *
     * @param cardSetId The ID of the card set to retrieve.
     * @return An {@link Optional} containing the card set if found.
     * @throws InvalidRequestException if the request is invalid.
     */
    Optional<CardSet> getCardSetById(Long cardSetId) throws InvalidRequestException;

    /**
     * Updates an existing card set.
     *
     * @param cardSetId      The ID of the card set to update.
     * @param updatedCardSet The updated card set.
     * @return The updated card set.
     * @throws InvalidRequestException If the card set with the specified ID does not exist
     *                                 or if the updated card set is invalid for creation.
     */
    Optional<CardSet> updateCardSet(Long cardSetId, CardSet updatedCardSet) throws InvalidRequestException;


    /**
     * Calculates the score of a given card set based on its content or state.
     *
     * @param cardSet The card set for which the score is to be calculated.
     * @return An integer representing the calculated score of the card set.
     */
    Integer calculateScoreOfCardSet(CardSet cardSet);

    /**
     * Deletes a card set by its ID.
     *
     * @param cardSetId The ID of the card set to delete.
     * @throws InvalidRequestException If the card set with the specified ID does not exist.
     */
    void deleteCardSet(Long cardSetId) throws InvalidRequestException;

    /**
     * Retrieves the cards from a card set based on its ID.
     *
     * @param cardSetId The ID of the card set from which to retrieve cards.
     * @return A list of {@link Card} objects belonging to the card set.
     * @throws InvalidRequestException If the card set with the specified ID does not exist.
     */
    List<Card> getCardsFromCardSet(Long cardSetId) throws InvalidRequestException;

    CardSet copyCardSet(CardSet cardSet, Long moduleId) throws InvalidRequestException;
}
