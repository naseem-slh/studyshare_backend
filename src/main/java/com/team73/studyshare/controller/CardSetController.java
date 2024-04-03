package com.team73.studyshare.controller;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.data.Card;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.service.Impl.CardSetServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/card-sets")
public class CardSetController {
    private final CardSetServiceImpl cardSetService;

    public CardSetController(CardSetServiceImpl cardSetService) {
        this.cardSetService = cardSetService;
    }

    @PostMapping
    public ResponseEntity<CardSet> createCardSet(@RequestBody CardSet cardSet) {
        try {
            CardSet createdCardSet = cardSetService.createCardSet(cardSet);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCardSet);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/copy/{moduleId}")
    public ResponseEntity<CardSet> copyCardSet(@RequestBody CardSet cardSet, @PathVariable Long moduleId) {
        try {
            CardSet copiedCardSet = cardSetService.copyCardSet(cardSet, moduleId);
            return ResponseEntity.status(HttpStatus.CREATED).body(copiedCardSet);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{cardSetId}")
    public ResponseEntity<CardSet> getCardSetById(@PathVariable Long cardSetId) {
        Optional<CardSet> cardSetOptional;
        try {
            cardSetOptional = cardSetService.getCardSetById(cardSetId);
        } catch (InvalidRequestException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return cardSetOptional
                .map(cardSet -> ResponseEntity.ok().body(cardSet))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{cardSetId}/cards")
    public ResponseEntity<List<Card>> getCardsOfCardSet(@PathVariable Long cardSetId) {
        try {
            List<Card> cards = cardSetService.getCardsFromCardSet(cardSetId);
            return ResponseEntity.ok(cards);
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CardSet>> getAllCardSets() {
        List<CardSet> cardSets = cardSetService.getAllCardSets();
        return ResponseEntity.ok().body(cardSets);
    }

    @PutMapping("/{cardSetId}")
    public ResponseEntity<CardSet> updateCardSet(@PathVariable Long cardSetId, @RequestBody CardSet updatedCardSet) {
        try {
            Optional<CardSet> cardSet = cardSetService.updateCardSet(cardSetId, updatedCardSet);
            return cardSet.map(set -> ResponseEntity.ok().body(set)).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{cardSetId}")
    public ResponseEntity<Void> deleteCardSet(@PathVariable Long cardSetId) {
        try {
            cardSetService.deleteCardSet(cardSetId);
            return ResponseEntity.noContent().build();
        } catch (InvalidRequestException e) {
            if (e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if(e.getMessage().contains("could not be deleted")){
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }
    }
}
