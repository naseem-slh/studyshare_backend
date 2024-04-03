package com.team73.studyshare.service.Impl;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.FileType;
import com.team73.studyshare.model.Status;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.Card;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Document;
import com.team73.studyshare.model.data.QuizField;
import com.team73.studyshare.repository.CardRepository;
import com.team73.studyshare.repository.CardSetRepository;
import com.team73.studyshare.repository.DocumentRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.CardService;
import com.team73.studyshare.service.CardSetService;
import com.team73.studyshare.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final CardSetRepository cardSetRepository;
    private final CardSetService cardSetService;
    private final ModuleService moduleService;
    private final DocumentRepository documentRepository;

    private final JwtService jwtService;
    private final String cardCreationErrMsg = "Card must not be null and have cardSet, question, answer, wronganswers, creator, and flagged in a correct state before it can be created.";

    @Autowired
    public CardServiceImpl(
            CardRepository cardRepository,
            CardSetRepository cardSetRepository,
            CardSetService cardSetService,
            ModuleService moduleService,
            DocumentRepository documentRepository, JwtService jwtService) {
        this.cardRepository = cardRepository;
        this.cardSetRepository = cardSetRepository;
        this.cardSetService = cardSetService;
        this.moduleService = moduleService;
        this.documentRepository = documentRepository;
        this.jwtService = jwtService;
    }

    public Optional<Card> getCardById(Long cardId) throws InvalidRequestException {
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if(cardOptional.isEmpty()){
            return Optional.empty();
        }

        Card requestedCard = cardOptional.get();
        CardSet cardSet = requestedCard.getCardSet();

        Long requestingUserId = jwtService.extractIdFromToken();
        if(cardSet.getVisibility().equals(Visibility.PUBLIC) || cardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            return Optional.of(requestedCard);
        } else {
            throw new InvalidRequestException("You do not have permission to access this card.");
        }
    }


    public Card createCard(Card card, MultipartFile questionImage, MultipartFile answerImage) throws InvalidRequestException, IOException {
        if (cardIsInvalidForCreation(card)) {
            throw new InvalidRequestException(cardCreationErrMsg);
        }
        Long requestingUserId = jwtService.extractIdFromToken();
        if(card.getCardSet().getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            Long cardSetId = card.getCardSet().getId();
            Optional<CardSet> cardSetOpt = cardSetRepository.findById(cardSetId);
            if (cardSetOpt.isPresent()) {
                if (questionImage != null) {
                    Document document = Document.builder()
                            .data(questionImage.getBytes())
                            .type(FileType.getFileTypeById(questionImage.getContentType()))
                            .build();
                    documentRepository.save(document);
                    card.getQuestion().setDocumentId(document.getId());
                }
                if (answerImage != null) {
                    Document document = Document.builder()
                            .data(answerImage.getBytes())
                            .type(FileType.getFileTypeById(answerImage.getContentType()))
                            .build();
                    documentRepository.save(document);
                    card.getAnswer().setDocumentId(document.getId());
                }
                CardSet cardSet = cardSetOpt.get();
                cardSet.addCard(card);
                cardSet.setScore(cardSetService.calculateScoreOfCardSet(cardSet));
                cardSetRepository.save(cardSet);
                moduleService.updateScoreOfModule(cardSet.getModule().getId());
            } else {
                throw new InvalidRequestException("CardSetID of Card does not exist.");
            }
        } else {
            throw new InvalidRequestException("You do not have permission to create this card.");
        }

        return cardRepository.save(card);
    }

    public Optional<Card> updateCard(Long cardId, Card card, MultipartFile questionImage, MultipartFile answerImage) throws InvalidRequestException, IOException {
        if (cardIsInvalidForCreation(card)) {
            throw new InvalidRequestException(cardCreationErrMsg);
        }
        Long requestingUserId = jwtService.extractIdFromToken();
        if (!card.getCardSet().getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to update this card.");
        }
        Optional<CardSet> cardSetOpt = cardSetRepository.findById(card.getCardSet().getId());
        if(cardSetOpt.isEmpty()){
            throw new InvalidRequestException("CardSetID of Card does not exist.");
        }
        CardSet cardSet = cardSetOpt.get();

        if (questionImage != null) {
            Long questionDocumentId = card.getQuestion().getDocumentId();
            if (questionDocumentId != null) {
                Optional<Document> documentOptional = documentRepository.findById(questionDocumentId);
                if (documentOptional.isPresent()) {
                    Document document = documentOptional.get();
                    document.setType(FileType.getFileTypeById(questionImage.getContentType()));
                    document.setData(questionImage.getBytes());
                    documentRepository.save(document);
                }
            } else {
                Document document = Document.builder()
                        .data(questionImage.getBytes())
                        .type(FileType.getFileTypeById(questionImage.getContentType()))
                        .build();
                documentRepository.save(document);
                card.getQuestion().setDocumentId(document.getId());
            }
        }

        if (answerImage != null) {
            Long answerDocumentId = card.getAnswer().getDocumentId();
            if (answerDocumentId != null) {
                Optional<Document> documentOptional = documentRepository.findById(answerDocumentId);
                if (documentOptional.isPresent()) {
                    Document document = documentOptional.get();
                    document.setType(FileType.getFileTypeById(answerImage.getContentType()));
                    document.setData(answerImage.getBytes());
                    documentRepository.save(document);
                }
            } else {
                Document document = Document.builder()
                        .data(answerImage.getBytes())
                        .type(FileType.getFileTypeById(answerImage.getContentType()))
                        .build();
                documentRepository.save(document);
                card.getAnswer().setDocumentId(document.getId());
            }
        }


        Optional<Card> existingCardOpt = cardRepository.findById(cardId);
        if(existingCardOpt.isPresent()){
            Card existingCard = existingCardOpt.get();
            existingCard.setQuestion(card.getQuestion());
            existingCard.setAnswer(card.getAnswer());
            existingCard.setWrongAnswers(card.getWrongAnswers());
            existingCard.setFlagged(card.getFlagged());

            if (existingCard.getStatus() != card.getStatus()) {
                if (!Status.isStatus(card.getStatus().getId())) {
                    throw new InvalidRequestException("Status is not valid");
                }
                existingCard.setStatus(card.getStatus());
                return setScoreForCardSet(cardSet, existingCard);
            }

            Card savedCard = cardRepository.save(existingCard);
            return Optional.of(savedCard);
        } else {
           throw new InvalidRequestException("Card with ID " + cardId + " does not exist.");
        }
    }


    public void deleteCard(Long cardId) throws InvalidRequestException {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("Card with ID " + cardId + " does not exist."));

        Long requestingUserId = jwtService.extractIdFromToken();
        if (!card.getCardSet().getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to delete this card.");
        }
        CardSet cardSet = card.getCardSet();
        if (cardSet != null) {
            cardSet.setCardCount(cardSet.getCardCount()-1);
            cardSet.getCards().remove(card);
            cardSet.setScore(cardSetService.calculateScoreOfCardSet(cardSet));
            cardSetRepository.save(cardSet);
            moduleService.updateScoreOfModule(cardSet.getModule().getId());
        }
        cardRepository.delete(card);
        QuizField question = card.getQuestion();
        QuizField answer = card.getAnswer();

        if (question.getDocumentId() != null) {
            documentRepository.deleteById(card.getQuestion().getDocumentId());
        }
        if (answer.getDocumentId() != null) {
            documentRepository.deleteById(card.getAnswer().getDocumentId());
        }
    }


    private Optional<Card> setScoreForCardSet(CardSet cardSet, Card existingCard) {
        Integer newScore = cardSetService.calculateScoreOfCardSet(cardSet);
        existingCard.getCardSet().setScore(newScore);
        Card savedCard = cardRepository.save(existingCard);
        try {
            moduleService.updateScoreOfModule(cardSet.getModule().getId());
        } catch (InvalidRequestException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(savedCard);
    }

    private boolean cardIsInvalidForCreation(Card c) {
        return c == null ||
                c.getQuestion() == null ||
                c.getAnswer() == null ||
                c.getStatus() == null ||
                c.getFlagged() == null ||
                c.getCardSet() == null ||
                c.getCreator() == null ||
                c.getWrongAnswers() == null ||
                c.getWrongAnswers().size()>4;
    }

}

//TODO: Bei Update und Delete auch Document löschen!