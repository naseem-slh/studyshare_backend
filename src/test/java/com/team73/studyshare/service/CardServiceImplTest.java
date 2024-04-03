package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.Role;
import com.team73.studyshare.model.Status;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.*;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.repository.CardRepository;
import com.team73.studyshare.repository.CardSetRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.Impl.CardServiceImpl;
import com.team73.studyshare.service.Impl.ModuleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CardServiceImplTest {

    @Mock
    private CardRepository cardRepositoryMock;

    @Mock
    private CardSetRepository cardSetRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private CardSetService cardSetService;

    @Mock
    private ModuleServiceImpl moduleService;

    @InjectMocks
    private CardServiceImpl cardService;

    private Module module;

    @Test
    public void deleteCard_ExistingCardId_DeletesCard() throws InvalidRequestException {
        // Arrange
        Card c = createValidCard();
        long existingCardId = 1L;
        when(cardRepositoryMock.findById(existingCardId)).thenReturn(Optional.ofNullable(c));
        when(jwtService.extractIdFromToken()).thenReturn(existingCardId);
        when(cardSetService.calculateScoreOfCardSet(c.getCardSet())).thenReturn(1);
        when(moduleService.updateScoreOfModule(module.getId())).thenReturn(Optional.ofNullable(module));

        //Assertion
        assertDoesNotThrow(() -> cardService.deleteCard(existingCardId));

        // Verify
        verify(cardRepositoryMock, times(1)).findById(existingCardId);

        verify(cardRepositoryMock, times(1)).delete(c);
    }


    @Test
    public void deleteCard_NonExistingCardId_ThrowsException() {
        // Arrange
        long nonExistingCardId = 999L;
        when(cardRepositoryMock.findById(nonExistingCardId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(InvalidRequestException.class, () -> cardService.deleteCard(nonExistingCardId));

        verify(cardRepositoryMock, times(1)).findById(nonExistingCardId);
        verify(cardRepositoryMock, times(0)).delete(any(Card.class));
    }


    @Test
    public void createCard_ValidCard_ReturnsSavedCard() throws InvalidRequestException, IOException {
        // Arrange
        Card validCard = createValidCard();
        when(cardRepositoryMock.save(any(Card.class))).thenReturn(validCard);
        when(jwtService.extractIdFromToken()).thenReturn(1L);
        when(cardSetRepository.findById(1L)).thenReturn(Optional.ofNullable(validCard.getCardSet()));


        // Act
        Card savedCard = cardService.createCard(validCard, null, null);

        // Assert
        assertNotNull(savedCard);
        assertEquals(validCard, savedCard);
        verify(cardRepositoryMock, times(1)).save(any(Card.class));
    }

    @Test
    public void createCard_InvalidCard_ThrowsException() {
        // Arrange
        Card invalidCard = createInvalidCard();

        // Act and Assert
        assertThrows(InvalidRequestException.class, () -> cardService.createCard(invalidCard, null, null));
        verify(cardRepositoryMock, times(0)).save(any(Card.class));
    }

    private Card createInvalidCard() {
        return new Card();
    }

    private Card createValidCard() {

        User creator = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("password")
                .visibility(Visibility.PUBLIC)
                .role(Role.USER)
                .description("Some description")
                .modules(new ArrayList<>())
                .build();

        Directory rootDirectory = Directory.builder()
                .name("Web Engineering II Directory")
                .visibility(Visibility.PUBLIC)
                .creator(creator)
                .mainDirectory(null)
                .build();


        List<User> ownersList = new ArrayList<>();
        ownersList.add(creator);

        module = Module.builder()
                .id(1L)
                .name("Valid Module")
                .description("Module Description")
                .visibility(Visibility.PUBLIC)
                .createdAt(new Date())
                .creator(creator)
                .rootDirectory(rootDirectory)
                .score(100)
                .owners(ownersList)
                .cardSets(Collections.singletonList(mock(CardSet.class)))
                .build();

        CardSet cardSet = CardSet.builder()
                .id(1L)
                .name("Sample Card Set")
                .visibility(Visibility.PUBLIC)
                .createdAt(new Date()) // Set the appropriate creation date
                .score(0) // Set the initial score
                .module(module) // Set the module
                .creator(creator) // Set the user who created this card set
                .originCardSetId(null) // Set the ID of the original card set if available
                .cards(new ArrayList<>()) // Initialize the cards list if needed
                .cardCount(0) // Initial card count
                .build();

        List<String> wrongAnswers = new ArrayList<>();
        wrongAnswers.add("1. wrong answer");

        return Card.builder()
                .id(1L)
                .question(createValidQuizField("Valid Question"))
                .answer(createValidQuizField("Valid Answer"))
                .status(Status.GOOD)
                .flagged(false)
                .creator(creator)
                .cardSet(cardSet)
                .wrongAnswers(wrongAnswers)
                .build();
    }

    private QuizField createValidQuizField(String text) {
        return QuizField.builder()
                .id(1L)
                .text(text)
                .build();
    }


}


