package com.team73.studyshare.service.Impl;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.Status;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.*;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.repository.CardRepository;
import com.team73.studyshare.repository.CardSetRepository;
import com.team73.studyshare.repository.DocumentRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.CardSetService;
import com.team73.studyshare.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CardSetServiceImpl implements CardSetService {
    private final CardSetRepository cardSetRepository;
    private final CardRepository cardRepository;
    private final ModuleRepository moduleRepository;
    private final DocumentRepository documentRepository;
    private final ModuleService moduleService;
    private final JwtService jwtService;

    private final String cardSetCreationErrMsg = "CardSet must not be null and have name, visibility, creationdate, module and score in a correct state before it can be created.";

    @Autowired
    public CardSetServiceImpl(
            CardRepository cardRepository,
            CardSetRepository cardSetRepository,
            ModuleRepository moduleRepository,
            DocumentRepository documentRepository, ModuleService moduleService,
            JwtService jwtService) {
        this.cardSetRepository = cardSetRepository;
        this.cardRepository = cardRepository;
        this.moduleRepository = moduleRepository;
        this.documentRepository = documentRepository;
        this.moduleService = moduleService;
        this.jwtService = jwtService;
    }

    public List<CardSet> getAllCardSets() {
        Long requestingUserId = jwtService.extractIdFromToken();

        return cardSetRepository.findAll().stream()
                .filter(cardSet -> cardSet.getVisibility() == Visibility.PUBLIC ||
                        cardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId)))
                .toList();
    }

    public Optional<CardSet> getCardSetById(Long cardSetId) throws InvalidRequestException {
        Optional<CardSet> cardSetOptional = cardSetRepository.findById(cardSetId);

        if (cardSetOptional.isEmpty()) {
            return Optional.empty();
        }

        CardSet requestedCardSet = cardSetOptional.get();

        Long requestingUserId = jwtService.extractIdFromToken();

        if (requestedCardSet.getVisibility().equals(Visibility.PUBLIC) ||
                requestedCardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            return Optional.of(requestedCardSet);
        } else {
            throw new InvalidRequestException("You do not have permission to access this cardset.");
        }
    }

    @Override
    public List<Card> getCardsFromCardSet(Long cardSetId) throws InvalidRequestException {
        CardSet requestedCardSet = cardSetRepository.findById(cardSetId)
                .orElseThrow(() -> new InvalidRequestException("CardSet with ID " + cardSetId + " does not exist."));

        Long requestingUserId = jwtService.extractIdFromToken();

        if (requestedCardSet.getVisibility().equals(Visibility.PUBLIC) ||
                requestedCardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            return requestedCardSet.getCards();
        } else {
            throw new InvalidRequestException("You do not have permission to access cardsets for this module.");
        }
    }

    public CardSet createCardSet(CardSet cardSet) throws InvalidRequestException {
        if (cardSetIsInvalidForCreation(cardSet)) {
            throw new InvalidRequestException(cardSetCreationErrMsg);
        }
        if(cardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(jwtService.extractIdFromToken()))){
            cardSet.setCreatedAt(new Date());
            CardSet savedCardSet = cardSetRepository.save(cardSet);
            Optional<Module> moduleOpt = moduleService.getModuleById(savedCardSet.getModule().getId());
            if(moduleOpt.isEmpty()){
                throw new InvalidRequestException("module of the created cardset does not exist");
            }
            Module module = moduleOpt.get();
            module.setCardSetCount(module.getCardSetCount()+1);
            moduleRepository.save(module);
            return savedCardSet;
        } else {
            throw new InvalidRequestException("You do not have permission to create the cardset for this module.");
        }
    }

    @Override
    public CardSet copyCardSet(CardSet cardSet, Long moduleId) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();

        if(!cardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId)) &&
            cardSet.getVisibility() == Visibility.PRIVATE){
            throw new InvalidRequestException("No permission due to no ownership and private visibility.");
        }

        Optional<Module> opt = moduleService.getModuleById(moduleId);
        if(opt.isPresent()){
            Module moduleToCopyInto = opt.get();
            if(!moduleToCopyInto.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))){
                throw new InvalidRequestException("No permission due to no ownership.");
            }
            User newCreator = moduleToCopyInto.getCreator();
            List<Card> cards = cardRepository.findCardSetCards(cardSet.getId());
            CardSet copyCardSet = CardSet.builder()
                    .id(null)
                    .name("(COPY) "+ cardSet.getName())
                    .cards(List.of())
                    .cardCount(cards.size())
                    .originCardSetId(cardSet.getId())
                    .score(0)
                    .createdAt(new Date())
                    .creator(newCreator)
                    .visibility(Visibility.PRIVATE)
                    .module(moduleToCopyInto)
                    .build();

            CardSet savedCardSet = createCardSet(copyCardSet);

            cards = cards.stream().map(originalCard -> {
                Long questionDocId = originalCard.getQuestion().getDocumentId();
                Long answerDocId = originalCard.getAnswer().getDocumentId();
                Long copiedQuestionDocId = null;
                Long copiedAnswerDocId = null;
                if(questionDocId != null){
                    Optional<Document> questionDocOpt = documentRepository.findById(questionDocId);
                    if(questionDocOpt.isPresent()){
                        Document oldQuestionDoc = questionDocOpt.get();
                        Document newQuestionDoc = Document.builder()
                                .type(oldQuestionDoc.getType())
                                .data(oldQuestionDoc.getData())
                                .id(null)
                                .build();
                        copiedQuestionDocId = documentRepository.save(newQuestionDoc).getId();
                    }

                }

                if(answerDocId != null){
                    Optional<Document> answerDocOpt = documentRepository.findById(answerDocId);
                    if(answerDocOpt.isPresent()){
                        Document oldAnswerDoc = answerDocOpt.get();
                        Document newAnswerDoc = Document.builder()
                                .type(oldAnswerDoc.getType())
                                .data(oldAnswerDoc.getData())
                                .id(null)
                                .build();
                        copiedAnswerDocId = documentRepository.save(newAnswerDoc).getId();
                    }

                }
                QuizField questionCopy = QuizField.builder()
                        .text(originalCard.getQuestion().getText())
                        .documentId(copiedQuestionDocId)
                        .build();

                QuizField answerCopy = QuizField.builder()
                        .text(originalCard.getAnswer().getText())
                        .documentId(copiedAnswerDocId)
                        .build();

                return Card.builder()
                        .question(questionCopy)
                        .answer(answerCopy)
                        .cardSet(savedCardSet)
                        .status(Status.UNDONE)
                        .flagged(false)
                        .creator(newCreator)
                        .build();
            }).collect(Collectors.toList());
            cardRepository.saveAll(cards);
            moduleToCopyInto.addCardSet(savedCardSet);
            moduleRepository.save(moduleToCopyInto);
            return savedCardSet;
        } else {
            throw new InvalidRequestException("The Module that was selected to hold the copied Cardset does not exist.");
        }
    }

    public Optional<CardSet> updateCardSet(Long cardSetId, CardSet cardSet) throws InvalidRequestException {
        if (cardSetIsInvalidForCreation(cardSet)) {
            throw new InvalidRequestException(cardSetCreationErrMsg);
        }
        if (cardSet.getId() == null) {
            throw new InvalidRequestException("No 'id' is present in the CardSet object.");
        }
        if (!cardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(jwtService.extractIdFromToken()))) {
            throw new InvalidRequestException("You do not have permission to update this cardset.");
        }
        if (!cardSet.getId().equals(cardSetId)) {
            throw new InvalidRequestException("The cardSetId in the path variable does not match the id in the request body.");
        }

        return cardSetRepository.findById(cardSetId)
                .map(existingCardSet -> {
                    existingCardSet.setName(cardSet.getName());
                    existingCardSet.setVisibility(cardSet.getVisibility());
                    CardSet savedCardSet = cardSetRepository.save(existingCardSet);
                    return Optional.of(savedCardSet);
                })
                .orElseThrow(() -> new InvalidRequestException("CardSet with ID " + cardSetId + " does not exist."));
    }

    public void deleteCardSet(Long cardSetId) throws InvalidRequestException {

        Long requestingUserId = jwtService.extractIdFromToken();

        if (!cardSetRepository.existsById(cardSetId)) {
            throw new InvalidRequestException("CardSet with ID " + cardSetId + " does not exist.");
        }
        Optional<CardSet> cs = cardSetRepository.findById(cardSetId);
        if (cs.isPresent()) {
            CardSet cardSet = cs.get();
            Module cardSetModule = cardSet.getModule();
            if (!cardSet.getModule().getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
                throw new InvalidRequestException("You do not have permission to delete this cardset.");
            }
            cardSet.getCards().forEach(card -> {
                Long questionDocId = card.getQuestion().getDocumentId();
                Long answerDocId = card.getAnswer().getDocumentId();
                if(questionDocId!=null){
                    documentRepository.deleteById(questionDocId);
                }
                if(answerDocId != null){
                    documentRepository.deleteById(answerDocId);
                }
            });
            cardSetModule.removeCardSet(cardSet);

            //modul setScore wird in updateScoreOfModule aufgerufen
            moduleService.updateScoreOfModule(cardSetModule.getId());
            moduleRepository.save(cardSetModule);
        }
        cardSetRepository.deleteById(cardSetId);
        Optional<CardSet> shouldBeEmpty = cardSetRepository.findById(cardSetId);
        if(shouldBeEmpty.isPresent()){
            throw new InvalidRequestException("CardSet could not be deleted from the database.");
        }
    }


    private boolean cardSetIsInvalidForCreation(CardSet cardSet) {
        return cardSet == null ||
                cardSet.getName() == null ||
                cardSet.getVisibility() == null ||
                cardSet.getScore() == null ||
                cardSet.getCardCount() == null ||
                cardSet.getModule() == null ||
                cardSet.getCreator() == null;
    }

    public Integer calculateScoreOfCardSet(CardSet cardSet) {
        int numberOfCards = cardSet.getCardCount();

        if (numberOfCards == 0) {
            return 0;
        }

        int totalPoints = 0;

        for (Card card : cardSet.getCards()) {
            totalPoints += getPoints(card.getStatus());
        }

        int rawPercentage = (int) Math.round(((double) totalPoints / (numberOfCards * 4)) * 100);
        return Math.min(100, Math.max(0, rawPercentage));
    }

    private Integer getPoints(Status status) {
        switch (status) {
            case UNDONE:
                return 0;
            case BAD:
                return 1;
            case OK:
                return 2;
            case GOOD:
                return 4;
            default:
                throw new IllegalArgumentException("Unsupported status: " + status); //TODO Runtime Exception sinnvoll?
        }
    }

}
