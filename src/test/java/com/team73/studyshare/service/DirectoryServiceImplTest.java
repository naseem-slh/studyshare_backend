package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.DirectoryContent;
import com.team73.studyshare.model.Role;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Directory;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.repository.DirectoryRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.Impl.DirectoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DirectoryServiceImplTest {

    @Mock
    private DirectoryRepository directoryRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private DirectoryServiceImpl directoryService;

    @Mock
    private JwtService jwtService;

    private Module module;

    private static final Long DIRECTORY_ID = 1L;

    private static final Long ORIGINAL_ROOTDIRECTORY_ID = 5L;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDirectory_Valid() throws InvalidRequestException {
        // Given
        Directory validDirectory = createValidDirectory();
        when(directoryRepository.save(any())).thenReturn(validDirectory);
        when(moduleRepository.findModuleByRootDirectoryId(ORIGINAL_ROOTDIRECTORY_ID)).thenReturn(Optional.of(module));

        // When
        Directory savedDirectory = directoryService.createDirectory(validDirectory);

        // Then
        assertNotNull(savedDirectory);
        assertEquals(validDirectory.getId(), savedDirectory.getId());
        assertEquals(validDirectory.getName(), savedDirectory.getName());
        verify(directoryRepository, times(1)).save(any());
    }

    @Test
    void getDirectoryById_ExistingId() throws InvalidRequestException {
        // Given
        Directory existingDirectory = createValidDirectory();

        when(directoryRepository.existsById(DIRECTORY_ID)).thenReturn(true);
        when(directoryRepository.findById(DIRECTORY_ID)).thenReturn(Optional.of(existingDirectory));

        // When
        Optional<Directory> result = directoryService.getDirectoryById(DIRECTORY_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(existingDirectory, result.get());
    }

    @Test
    void getDirectoryById_Non_ExistingId() throws InvalidRequestException {
        // Given
        Long nonExistingId = 22L;

        when(directoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when
        Optional<Directory> result = directoryService.getDirectoryById(nonExistingId);

        // Then
        assertEquals(result, Optional.empty());
    }

    @Test
    void getAllDirectories_FilledList() {
        // Given
        Directory directory1 = createValidDirectory();
        Directory directory2 = createValidDirectory();
        directory2.setId(2L);

        List<Directory> directories = Arrays.asList(directory1, directory2);
        when(directoryRepository.findAll()).thenReturn(directories);

        // When
        List<Directory> result = directoryService.getAllDirectories();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void getAllDirectories_EmptyList() {
        // Given
        when(directoryRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Directory> result = directoryService.getAllDirectories();

        // Then
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void updateDirectory_Valid() throws InvalidRequestException {
        // Given
        Directory existingDirectory = createValidDirectory();
        Directory updatedDirectory = createValidDirectory();
        updatedDirectory.setName("Updated Directory");

        when(directoryRepository.existsById(DIRECTORY_ID)).thenReturn(true);
        when(directoryRepository.findById(DIRECTORY_ID)).thenReturn(Optional.of(existingDirectory));
        when(directoryRepository.save(any(Directory.class))).thenReturn(updatedDirectory);
        when(moduleRepository.findModuleByRootDirectoryId(ORIGINAL_ROOTDIRECTORY_ID)).thenReturn(Optional.of(module));

        // When
        Optional<Directory> resultDirectoryOptional = directoryService.updateDirectory(DIRECTORY_ID, updatedDirectory);

        // Then
        assertTrue(resultDirectoryOptional.isPresent());

        Directory resultDirectory = resultDirectoryOptional.get();

        assertEquals(updatedDirectory.getName(), resultDirectory.getName());
        assertEquals(resultDirectory.getId(), updatedDirectory.getId());

        verify(directoryRepository, times(1)).save(any(Directory.class));
    }

    @Test
    void updateDirectory_DirectoryNotFound() {
        // Given
        Directory updatedDirectory = createValidDirectory();

        when(directoryRepository.existsById(DIRECTORY_ID)).thenReturn(false);
        when(moduleRepository.findModuleByRootDirectoryId(ORIGINAL_ROOTDIRECTORY_ID)).thenReturn(Optional.of(module));
        // Then
        assertThrows(InvalidRequestException.class, () -> {
            directoryService.updateDirectory(DIRECTORY_ID, updatedDirectory);
        });

        verify(directoryRepository, never()).save(any(Directory.class));
    }

    @Test
    void updateModule_IdsDoNotMatch() {
        // Given
        Directory existingDirectory = createValidDirectory();
        Directory updatedDirectory = createValidDirectory();
        updatedDirectory.setId(2L);

        when(directoryRepository.existsById(DIRECTORY_ID)).thenReturn(true);
        when(directoryRepository.findById(DIRECTORY_ID)).thenReturn(Optional.of(existingDirectory));
        // When and Then
        assertThrows(InvalidRequestException.class, () -> directoryService.updateDirectory(DIRECTORY_ID, updatedDirectory));

        // Verify that the repository methods were not called
        verify(directoryRepository, never()).save(any(Directory.class));
    }


    @Test
    void updateDirectory_InvalidDirectory() {
        // Given
        Directory existingDirectory = createValidDirectory();
        Directory updatedDirectory = createValidDirectory();
        updatedDirectory.setName(null);

        when(directoryRepository.existsById(DIRECTORY_ID)).thenReturn(true);
        when(directoryRepository.findById(DIRECTORY_ID)).thenReturn(Optional.of(existingDirectory));

        // Then
        assertThrows(InvalidRequestException.class, () -> {
            directoryService.updateDirectory(DIRECTORY_ID, updatedDirectory);
        });

        verify(directoryRepository, never()).save(any(Directory.class));
    }

    @Test
    void deleteDirectory_Valid() throws InvalidRequestException {
        // Given
        Directory directory = createValidDirectory();
        when(directoryRepository.findById(DIRECTORY_ID)).thenReturn(Optional.ofNullable(directory));
        when(moduleRepository.findModuleByRootDirectoryId(ORIGINAL_ROOTDIRECTORY_ID)).thenReturn(Optional.of(module));

        // When
        directoryService.deleteDirectory(DIRECTORY_ID);

        // Then
        verify(directoryRepository, times(1)).deleteById(DIRECTORY_ID);
    }

    @Test
    void deleteDirectory_nonExistingDirectory() {
        // Given
        Long nonExistingId = 2L;

        when(directoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Then
        assertThrows(InvalidRequestException.class, () -> {
            directoryService.deleteDirectory(nonExistingId);
        });

        verify(directoryRepository, never()).deleteById(nonExistingId);
    }

    @Test
    void getDirectoryContent_Valid() throws InvalidRequestException {
        Directory directory = createValidDirectory();

        when(directoryRepository.findById(DIRECTORY_ID)).thenReturn(Optional.of(directory));
        when(moduleRepository.findModuleByRootDirectoryId(ORIGINAL_ROOTDIRECTORY_ID)).thenReturn(Optional.of(module));

        DirectoryContent directoryContent = directoryService.getDirectoryContent(DIRECTORY_ID);

        assertNotNull(directoryContent);
        assertEquals(directory.getSubDirectories(), directoryContent.getSubdirectories());
        assertEquals(directory.getFiles(), directoryContent.getFiles());

        verify(directoryRepository, times(1)).findById(DIRECTORY_ID);
    }

    @Test
    void getDirectoryContent_DirectoryNotFound() {
        Long directoryId = 2L;

        when(directoryRepository.findById(directoryId)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> directoryService.getDirectoryContent(directoryId));

        verify(directoryRepository, times(1)).findById(directoryId);
    }

    private Directory createValidDirectory() {
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
                .id(5L)
                .name("Web Engineering II Directory")
                .visibility(Visibility.PUBLIC)
                .createdAt(new Date())
                .creator(creator)
                .mainDirectory(null)
                .files(new ArrayList<>())
                .subDirectories(new ArrayList<>())
                .build();

        Directory rootDirectory1 = Directory.builder()
                .name("Web Engineering II ")
                .visibility(Visibility.PUBLIC)
                .createdAt(new Date())
                .creator(creator)
                .mainDirectory(rootDirectory)
                .files(new ArrayList<>())
                .subDirectories(new ArrayList<>())
                .build();

        when(jwtService.extractIdFromToken()).thenReturn(1L);


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

        return Directory.builder()
                .id(1L)
                .name("Valid Directory")
                .visibility(Visibility.PUBLIC)
                .createdAt(new Date())
                .creator(creator)
                .mainDirectory(rootDirectory1)
                .files(new ArrayList<>())
                .subDirectories(new ArrayList<>())
                .build();

    }

}
