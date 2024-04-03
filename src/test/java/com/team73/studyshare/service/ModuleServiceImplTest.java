package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.Role;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Directory;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.Impl.ModuleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModuleServiceImplTest {

    @Mock
    private ModuleRepository moduleRepository;

    @InjectMocks
    private ModuleServiceImpl moduleService;

    @Mock
    private JwtService jwtService;

    private static final Long MODULE_ID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createModule_Valid() throws InvalidRequestException {
        // Given
        Module validModule = createValidModule();
        when(moduleRepository.save(any())).thenReturn(validModule);

        // When
        Module savedModule = moduleService.createModule(validModule);

        // Then
        assertNotNull(savedModule);
        assertEquals(validModule.getId(), savedModule.getId());
        assertEquals(validModule.getName(), savedModule.getName());
        verify(moduleRepository, times(1)).save(any());
    }

    @Test
    void createModule_Valid_originModule_Null() throws InvalidRequestException {
        // Given
        Module moduleMissingOrigin = createValidModule();
        moduleMissingOrigin.setOriginModule(null);

        when(moduleRepository.save(any())).thenReturn(moduleMissingOrigin);

        // When
        Module savedModule = moduleService.createModule(moduleMissingOrigin);

        // Then
        assertNotNull(savedModule);
        assertNull(moduleMissingOrigin.getOriginModule());
        verify(moduleRepository, times(1)).save(any());
    }

    @Test
    void createModule_Invalid_MissingName() {
        // Given
        Module moduleMissingName = createValidModule();
        moduleMissingName.setName(null);

        // Then
        assertEquals(moduleMissingName.getName(),null);
        assertThrows(InvalidRequestException.class,
                () -> moduleService.createModule(moduleMissingName));
    }

    @Test
    void getModuleById_ExistingId() throws InvalidRequestException {
        // Given
        Module existingModule = createValidModule();

        when(moduleRepository.existsById(MODULE_ID)).thenReturn(true);
        when(moduleRepository.findById(MODULE_ID)).thenReturn(Optional.of(existingModule));

        // When
        Optional<Module> result = moduleService.getModuleById(MODULE_ID);

        // Then
        assertTrue(result.isPresent());
        assertEquals(existingModule, result.get());
    }

    @Test
    void getModuleById_Non_ExistingId() throws InvalidRequestException {
        // Given
        Long nonExistingId = 22L;

        when(moduleRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when
        Optional<Module> result = moduleService.getModuleById(nonExistingId);

        // Then
        assertEquals(result, Optional.empty());
    }

    @Test
    void getAllModules_FilledList() {
        // Given
        Module module1 = createValidModule();
        Module module2 = createValidModule();

        List<Module> modules = Arrays.asList(module1, module2);
        when(moduleRepository.findAll()).thenReturn(modules);

        // When
        List<Module> result = moduleService.getAllModules();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void getAllModules_EmptyList() {
        // Given
        when(moduleRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Module> result = moduleService.getAllModules();

        // Then
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
    }

    @Test
    void updateModule_Valid() throws InvalidRequestException {
        // Given
        Module existingModule = createValidModule();
        Module updatedModule = createValidModule();
        updatedModule.setName("Updated Module");

        when(moduleRepository.existsById(MODULE_ID)).thenReturn(true);
        when(moduleRepository.findById(MODULE_ID)).thenReturn(Optional.of(existingModule));
        when(moduleRepository.save(any(Module.class))).thenReturn(updatedModule);

        // When
        Optional<Module> resultModuleOptional = moduleService.updateModule(MODULE_ID, updatedModule);

        // Then
        assertTrue(resultModuleOptional.isPresent());

        Module resultModule = resultModuleOptional.get();

        assertEquals(updatedModule.getName(), resultModule.getName());
        assertEquals(updatedModule.getId(), resultModule.getId());

        verify(moduleRepository, times(1)).save(any(Module.class));
    }



    @Test
    void updateModule_ModuleNotFound() {
        // Given
        Module updatedModule = createValidModule();

        when(moduleRepository.existsById(MODULE_ID)).thenReturn(false);

        // Then
        assertThrows(InvalidRequestException.class, () -> {
            moduleService.updateModule(MODULE_ID, updatedModule);
        });

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void updateModule_IdsDoNotMatch() {
        // Given
        Module existingModule = createValidModule();
        Module updatedModule = createValidModule();
        updatedModule.setId(2L); // Set a different ID in the request body

        when(moduleRepository.existsById(MODULE_ID)).thenReturn(true);
        when(moduleRepository.findById(MODULE_ID)).thenReturn(Optional.of(existingModule));

        // When and Then
        assertThrows(InvalidRequestException.class, () -> moduleService.updateModule(MODULE_ID, updatedModule));

        // Verify that the repository methods were not called
        verify(moduleRepository, never()).save(any(Module.class));
    }


    @Test
    void updateModule_InvalidModule() {
        // Given
        Module existingModule = createValidModule();
        Module updatedModule = createValidModule();
        updatedModule.setName(null);

        when(moduleRepository.existsById(MODULE_ID)).thenReturn(true);
        when(moduleRepository.findById(MODULE_ID)).thenReturn(Optional.of(existingModule));

        // Then
        assertThrows(InvalidRequestException.class, () -> {
            moduleService.updateModule(MODULE_ID, updatedModule);
        });

        verify(moduleRepository, never()).save(any(Module.class));
    }

    @Test
    void deleteModule_Valid() throws InvalidRequestException {
        // Given

        Module m = createValidModule();
        when(moduleRepository.existsById(MODULE_ID)).thenReturn(true);

        // When
        moduleService.deleteModule(MODULE_ID);

        // Then
        verify(moduleRepository, times(1)).deleteById(MODULE_ID);
    }

    @Test
    void deleteModule_NonExistingModule() {
        // Given
        Long nonExistingId = 2L;

        when(moduleRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Then
        assertThrows(InvalidRequestException.class, () -> {
            moduleService.deleteModule(nonExistingId);
        });

        verify(moduleRepository, never()).deleteById(nonExistingId);
    }

    @Test
    public void getCardSetsFromModule_Valid() throws InvalidRequestException {
        Module module = createValidModule();
        List<CardSet> cardSets = new ArrayList<>();
        module.setCardSets(cardSets);

        when(moduleRepository.findById(MODULE_ID)).thenReturn(Optional.of(module));

        List<CardSet> result = moduleService.getCardSetsFromModule(MODULE_ID);

        assertNotNull(result);
        assertEquals(cardSets, result);

        verify(moduleRepository, times(1)).findById(MODULE_ID);
    }

    @Test
    public void getCardSetsFromModule_ModuleNotFound() {
        Long moduleId = 2L;

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.empty());

        assertThrows(InvalidRequestException.class, () -> moduleService.getCardSetsFromModule(moduleId));

        verify(moduleRepository, times(1)).findById(moduleId);
    }


    private Module createValidModule() {

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

        when(jwtService.extractIdFromToken()).thenReturn(1L);

        List<User> ownersList = new ArrayList<>();
        ownersList.add(creator);

        return Module.builder()
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
    }
}
