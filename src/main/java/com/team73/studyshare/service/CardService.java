package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.Card;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/**
 * Service interface for managing {@link Card} entities.
 */
public interface CardService {

    /**
     * Creates a new card with optional question and answer images.
     *
     * @param card            The card to be created.
     * @param questionImage   The image associated with the question (optional).
     * @param answerImage     The image associated with the answer (optional).
     * @return The created card.
     * @throws InvalidRequestException If the card is invalid for creation.
     * @throws IOException            If an I/O exception occurs during image processing.
     */
    Card createCard(Card card, MultipartFile questionImage, MultipartFile answerImage) throws InvalidRequestException, IOException;

    /**
     * Retrieves a card by its ID.
     *
     * @param cardId The ID of the card to retrieve.
     * @return An {@link Optional} containing the card if found.
     */
    Optional<Card> getCardById(Long cardId) throws InvalidRequestException;


    /**
     * Updates an existing card with optional new question and answer images.
     *
     * @param cardId         The ID of the card to update.
     * @param updatedCard    The updated card.
     * @param questionImage  The new image associated with the question (optional).
     * @param answerImage    The new image associated with the answer (optional).
     * @return The updated card, wrapped in an  Optional.
     * @throws InvalidRequestException If the card with the specified ID does not exist
     *                                 or if the updated card is invalid for creation.
     * @throws IOException            If an I/O exception occurs during image processing.
     */
    Optional<Card> updateCard(Long cardId, Card updatedCard, MultipartFile questionImage, MultipartFile answerImage) throws InvalidRequestException, IOException;


    /**
     * Deletes a card by its ID.
     *
     * @param cardId The ID of the card to delete.
     * @throws InvalidRequestException If the card with the specified ID does not exist.
     */
    void deleteCard(Long cardId) throws InvalidRequestException;
}
