package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.Role;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.repository.CardSetRepository;
import com.team73.studyshare.repository.UserRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    private static final String NAME = "MAX MUSTERMANN";
    private static final String EMAIL = "max@gmail.com";
    private static final Long USER_ID = 1L;
    private static final Long DIFFERENT_USER_ID = 2L;
    private static final List<Module> MODULE_LIST = List.of();
    private static final List<User> USER_LIST = List.of();
    private static final String ERROR_MSG_SAVE_USER = "One or more properties are not set in the request.";
    private static final String ERROR_MSG_UPDATE_USER_1 = "No 'id' is present in the User object or given user is null.";
    private static final String ERROR_MSG_UPDATE_USER_2 = "The userId in the path variable does not match the id in the request body.";
    private static final String ERROR_MSG_UPDATE_USER_3 = "The update request is incomplete.";

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private CardSetRepository cardSetRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepositoryMock, passwordEncoder, userRepositoryMock,cardSetRepository,jwtService);
    }

    @Test
    public void testGetAllUsers() {
        // Given
        when(userRepositoryMock.findAll()).thenReturn(USER_LIST);

        // When
        List<User> resultFiles = userService.getAllUsers();

        // Then
        assertNotNull(resultFiles);
        assertEquals(USER_LIST.size(), resultFiles.size());
    }

    @Test
    public void testGetUserById() throws InvalidRequestException {
        // Given
        User userMock = createUserMockWithOutId();
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(userMock));

        // When
        Optional<User> resultUser = userService.getUserById(USER_ID);

        // Then
        assertTrue(resultUser.isPresent());
        assertEquals(userMock, resultUser.get());
    }

    @Test
    public void testGetUserById_NotFound() throws InvalidRequestException {
        // Given
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

        // When
        Optional<User> resultFile = userService.getUserById(USER_ID);

        // Then
        assertTrue(resultFile.isEmpty());
    }

    @Test
    public void testCreateUser_happyPath() throws InvalidRequestException {
        // Given
        User userMock = createUserMockWithOutId();
        when(userRepositoryMock.save(any(User.class))).thenReturn(userMock);

        // When
        User resultUser = userService.createUser(userMock);

        // Then
        assertNotNull(resultUser);
        assertEquals(userMock, resultUser);
    }

    @Test
    public void testCreateUser_invalidRequest() {
        // Given
        User userMock = mock(User.class);

        // When
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.createUser(userMock));

        // Then
        assertEquals(ERROR_MSG_SAVE_USER, exception.getMessage());
        verify(userRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testUpdateUser_happyPath() throws InvalidRequestException {
        // Given
        User existingUserMock = createUserMockWithId();
        User updatedUserMock = createUserMockWithId();

        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(existingUserMock));
        when(userRepositoryMock.save(any(User.class))).thenReturn(updatedUserMock);

        // When
        Optional<User> resultUserOptional = userService.updateUser(USER_ID, updatedUserMock);
        User resultUser = resultUserOptional.get();

        // Then
        assertEquals(updatedUserMock, resultUser);
        verify(userRepositoryMock, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_ValidRequest_UserNotFoundById() throws InvalidRequestException {
        // Given
        User userMock = createUserMockWithId();


        // Then
        assertThrows(InvalidRequestException.class, () -> userService.updateUser(USER_ID, userMock));
        verify(userRepositoryMock, times(0)).save(any());
    }


    @Test
    public void testUpdateUser_invalidRequest_NoUserId() {
        // Given
        User userMock = mock(User.class);
        when(userMock.getId()).thenReturn(null);

        // When
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.updateUser(USER_ID, userMock));

        // Then
        assertEquals(ERROR_MSG_UPDATE_USER_1, exception.getMessage());
        verify(userRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testUpdateUser_invalidRequest_userIdParamNotEqualUserId() {
        // Given
        User userMock = mock(User.class);
        when(userMock.getId()).thenReturn(DIFFERENT_USER_ID);

        // When
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.updateUser(USER_ID, userMock));

        // Then
        assertEquals(ERROR_MSG_UPDATE_USER_2, exception.getMessage());
        verify(userRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testUpdateUser_invalidRequest_missingAttributes() {
        // Given
        User userMock = createUserMockWithId();
        userMock.setName(null);

        // When
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userService.updateUser(USER_ID, userMock));

        // Then
        assertEquals(ERROR_MSG_UPDATE_USER_3, exception.getMessage());
        verify(userRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testDeleteUser_happyPath() throws InvalidRequestException {
        // Given
        when(userRepositoryMock.existsById(USER_ID)).thenReturn(true);
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        // When
        boolean result = userService.deleteUser(USER_ID);

        // Then
        assertTrue(result);
    }

    @Test
    public void testDeleteUser_notFoundById() throws InvalidRequestException {
        // Given
        when(userRepositoryMock.existsById(USER_ID)).thenReturn(false);
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        // When
        boolean result = userService.deleteUser(USER_ID);

        // Then
        assertFalse(result);
    }

    @Test
    public void testGetModulesForUser_happyPath() throws InvalidRequestException {
        // Given
        User userMock = createUserMockWithId();
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.of(userMock));

        // When
        List<Module> modules = userService.getModulesForUser(USER_ID);

        // Then
        assertNotNull(modules);
        assertEquals(MODULE_LIST.size(), modules.size());
    }

    @Test
    public void testGetModulesForUser_UserNotFound() {
        // Given
        when(userRepositoryMock.findById(USER_ID)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(InvalidRequestException.class, () -> userService.getModulesForUser(USER_ID));
    }

    private User createUserMockWithId() {
        User userMock = createUserMockWithOutId();
        userMock.setId(1L);
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        return userMock;
    }

    private User createUserMockWithOutId() {

        return User.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("password")
                .visibility(Visibility.PUBLIC)
                .role(Role.USER)
                .description("Some description")
                .modules(new ArrayList<>())
                .lastQuizzedCardSets(new HashMap<Long, Date>()) // Add the lastQuizzedCardSets field
                .build();

    }
}
