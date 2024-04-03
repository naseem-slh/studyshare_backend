package com.team73.studyshare.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.Card;
import com.team73.studyshare.service.Impl.CardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardServiceImpl cardService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CardController(CardServiceImpl cardService, ObjectMapper objectMapper) {
        this.cardService = cardService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<?> createCard(@RequestPart("cardResource") String cardMetadata,
                                        @RequestPart(value = "questionImage", required = false) MultipartFile questionImage,
                                        @RequestPart(value = "answerImage", required = false) MultipartFile answerImage) {
        try {
            Card cardResource = objectMapper.readValue(cardMetadata, Card.class);
            Card createdCard = cardService.createCard(cardResource, questionImage, answerImage);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<Card> getCardById(@PathVariable Long cardId) {
        Optional<Card> cardOptional;// = cardService.getCardById(cardId);
        try {
            cardOptional = cardService.getCardById(cardId);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return cardOptional
                .map(card -> ResponseEntity.ok().body(card))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Card> updateCard(@PathVariable Long cardId,
                                           @RequestPart("cardResource") String cardMetadata,
                                           @RequestPart(value = "questionImage", required = false) MultipartFile questionImage,
                                           @RequestPart(value = "answerImage", required = false) MultipartFile answerImage) {
        try {
            Card cardResource = objectMapper.readValue(cardMetadata, Card.class);
            Optional<Card> updatedCard = cardService.updateCard(cardId, cardResource, questionImage, answerImage);
            return updatedCard.map(value -> ResponseEntity.ok().body(value)).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        try {
            cardService.deleteCard(cardId);
            return ResponseEntity.noContent().build();
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }
}
