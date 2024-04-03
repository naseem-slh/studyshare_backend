package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.Role;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.*;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.repository.CardSetRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.Impl.CardSetServiceImpl;
import com.team73.studyshare.service.Impl.ModuleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardSetServiceImplTest {

    @Mock
    private CardSetRepository cardSetRepositoryMock;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private CardSetServiceImpl cardSetService;

    @Mock
    private JwtService jwtService;

    private Module module;

    @Mock
    ModuleServiceImpl moduleService;

    @Test
    public void createCardSet_InvalidCardSet_ThrowsException() {
        // Arrange
        CardSet invalidCardSet = createInvalidCardSet();

        // Act and Assert
        assertThrows(InvalidRequestException.class, () -> cardSetService.createCardSet(invalidCardSet));
        verify(cardSetRepositoryMock, times(0)).save(any(CardSet.class));
    }

    @Test
    public void getCardSetById_ExistingCardSetId_ReturnsCardSetOptional() throws InvalidRequestException{
        // Arrange
        long existingCardSetId = 1L;
        CardSet cardSetMock = createCardSetMockWithId();
        when(cardSetRepositoryMock.findById(existingCardSetId)).thenReturn(Optional.of(cardSetMock));

        // Act
        Optional<CardSet> resultCardSet = cardSetService.getCardSetById(existingCardSetId);

        // Assert
        assertTrue(resultCardSet.isPresent());
        assertEquals(cardSetMock, resultCardSet.get());
        verify(cardSetRepositoryMock, times(1)).findById(existingCardSetId);
    }

    @Test
    public void getCardSetById_NonExistingCardSetId_ThrowsException() throws InvalidRequestException {
        // Arrange
        long nonExistingCardSetId = 999L;

        when(cardSetRepositoryMock.findById(nonExistingCardSetId)).thenReturn(Optional.empty());

        // Act
        Optional<CardSet> result = cardSetService.getCardSetById(nonExistingCardSetId);

        // Assert
        assertEquals(result, Optional.empty());

    }


    @Test
    public void updateCardSet_ExistingCardSet_ReturnsUpdatedCardSet() throws InvalidRequestException{
        // Arrange
        long existingCardSetId = 1L;
        CardSet existingCardSet = createCardSetMockWithOutId();
        CardSet updatedCardSet=createCardSetMockWithId();
        updatedCardSet.setName("Updated Name");
        when(cardSetRepositoryMock.findById(existingCardSetId)).thenReturn(Optional.of(existingCardSet));
        when(cardSetRepositoryMock.save(any(CardSet.class))).thenReturn(updatedCardSet);

        // Act
        Optional<CardSet> resultCardSet = cardSetService.updateCardSet(existingCardSetId, updatedCardSet);

        // Assert
        assertNotNull(resultCardSet.get());
        assertEquals(updatedCardSet, resultCardSet.get());
        assertNotEquals(existingCardSet, resultCardSet.get());
        verify(cardSetRepositoryMock, times(1)).save(any(CardSet.class));
    }

    @Test
    public void updateCardSet_NonExistingCardSet_ThrowsException() {
        // Arrange
        long nonExistingCardSetId = 999L;
        CardSet updatedCardSet = createCardSetMockWithOutId();
        updatedCardSet.setId(nonExistingCardSetId);

        // Act and Assert
        assertThrows(InvalidRequestException.class, () -> cardSetService.updateCardSet(nonExistingCardSetId, updatedCardSet));
        verify(cardSetRepositoryMock, times(0)).save(any(CardSet.class));
    }


    @Test
    public void createCardSet_NullCardSet_ThrowsException() {
        // Act and Assert
        assertThrows(InvalidRequestException.class, () -> cardSetService.createCardSet(null));
        verify(cardSetRepositoryMock, times(0)).save(any(CardSet.class));
    }

    @Test
    public void getAllCardSets_ReturnsListOfCardSets() {
        // Arrange
        List<CardSet> allCardSets = Arrays.asList(createCardSetMockWithId(), createCardSetMockWithId());
        when(cardSetRepositoryMock.findAll()).thenReturn(allCardSets);

        // Act
        List<CardSet> result = cardSetService.getAllCardSets();

        // Assert
        assertNotNull(result);
        assertEquals(allCardSets.size(), result.size());
        verify(cardSetRepositoryMock, times(1)).findAll();
    }

    @Test
    public void updateCardSet_NullUpdatedCardSet_ThrowsException() {
        // Arrange
        long existingCardSetId = 1L;

        // Act and Assert
        assertThrows(InvalidRequestException.class, () -> cardSetService.updateCardSet(existingCardSetId, null));
        verify(cardSetRepositoryMock, times(0)).save(any(CardSet.class));
    }

    @Test
    public void deleteCardSet_ExistingCardSetId_DeletesCardSet() throws InvalidRequestException{
        // Arrange
        long existingCardSetId = 1L;

        CardSet c = createCardSetMockWithId();
        when(cardSetRepositoryMock.existsById(existingCardSetId)).thenReturn(true);


        // Act
        cardSetService.deleteCardSet(existingCardSetId);

        // Assert
        verify(cardSetRepositoryMock, times(1)).deleteById(existingCardSetId);
    }

    @Test
    public void deleteCardSet_NonExistingCardSetId_ThrowsException() {
        // Arrange
        long nonExistingCardSetId = 999L;
        when(cardSetRepositoryMock.existsById(nonExistingCardSetId)).thenReturn(false);

        // Act and Assert
        assertThrows(InvalidRequestException.class, () -> cardSetService.deleteCardSet(nonExistingCardSetId));
        verify(cardSetRepositoryMock, times(1)).existsById(nonExistingCardSetId);
        verify(cardSetRepositoryMock, times(0)).deleteById(anyLong());
    }

    @Test
    public void deleteCardSet_ExistingCardSetId_DeletesCardSetAndAssociatedCards() throws InvalidRequestException{
        // Arrange
        long existingCardSetId = 1L;
        CardSet c = createCardSetMockWithId();

        when(cardSetRepositoryMock.existsById(existingCardSetId)).thenReturn(true);
        // Act
        cardSetService.deleteCardSet(existingCardSetId);

        // Assert
        verify(cardSetRepositoryMock, times(1)).existsById(existingCardSetId);
        verify(cardSetRepositoryMock, times(1)).deleteById(existingCardSetId);
    }

    @Test
    public void deleteCardSet_ExistingCardSetId_NoAssociatedCards_DeletesCardSet() throws InvalidRequestException{
        // Arrange
        long existingCardSetId = 1L;
        CardSet c = createCardSetMockWithId();

        when(cardSetRepositoryMock.existsById(existingCardSetId)).thenReturn(true);
        // Act
        cardSetService.deleteCardSet(existingCardSetId);

        // Assert
        verify(cardSetRepositoryMock, times(1)).existsById(existingCardSetId);
        verify(cardSetRepositoryMock, times(1)).deleteById(existingCardSetId);
        verify(cardSetRepositoryMock, times(0)).delete(any(CardSet.class));
    }

    private CardSet createInvalidCardSet() {
        return new CardSet();
    }

    private CardSet createCardSetMockWithId() {
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

        module =  Module.builder()
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

        when(jwtService.extractIdFromToken()).thenReturn(1L);

        return CardSet.builder()
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
    }



    private CardSet createCardSetMockWithOutId() {
        CardSet cardSet = createCardSetMockWithId();
        cardSet.setId(null);
        return cardSet;
    }


}

