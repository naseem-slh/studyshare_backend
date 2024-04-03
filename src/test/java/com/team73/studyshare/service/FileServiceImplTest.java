package com.team73.studyshare.service;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.FileType;
import com.team73.studyshare.model.Role;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.*;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.repository.DocumentRepository;
import com.team73.studyshare.repository.FileRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.Impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceImplTest {

    private static final String FILE_NAME_ARG = "nameArg.pdf";
    private static final String FILE_NAME_ORIGINAL = "nameOriginal.pdf";
    private static final String CONTENT_TYPE = "application/pdf";
    private static final Long FILE_ID = 1L;
    private static final List<File> FILE_LIST = List.of();
    private static final String ERROR_MSG_1 = "No 'id' is present in the File object or given fileResource is null.";
    private static final String ERROR_MSG_2 = "The cardId in the path variable does not match the id in the request body.";
    private static final String ERROR_MSG_3 = "One or more properties are not set in the request.";

    private static final String errmsg = "The File in the path variable does not match the id in the request body.";

    @Mock
    private FileRepository fileRepositoryMock;
    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private DocumentRepository documentRepository;

    private Module module;



    @Test
    public void testGetAllFiles() {
        // Given
        when(fileRepositoryMock.findAll()).thenReturn(FILE_LIST);

        // When
        List<File> resultFiles = fileService.getAllFiles();

        // Then
        assertNotNull(resultFiles);
        assertEquals(FILE_LIST.size(), resultFiles.size());
    }


    @Test
    public void testCreateFile_happyPathWithFileNameInFileResource() throws IOException, InvalidRequestException {
        // Given
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        when(multipartFileMock.getBytes()).thenReturn(new byte[0]);
        when(multipartFileMock.getContentType()).thenReturn(CONTENT_TYPE);


        File fileMock = createValidFile();
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        when(fileRepositoryMock.save(any(File.class))).thenReturn(fileMock);
        when(moduleRepository.findById(1L)).thenReturn(Optional.ofNullable(module));

        // When
        File resultFile = fileService.createFile(multipartFileMock, fileMock);

        // Then
        assertNotNull(resultFile);
        assertEquals(fileMock, resultFile);
        verify(fileRepositoryMock, times(1)).save(any());
    }

    @Test
    public void testCreateFile_happyPathOriginalFileName() throws IOException, InvalidRequestException {
        // Given
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        when(multipartFileMock.getBytes()).thenReturn(new byte[0]);
        when(multipartFileMock.getContentType()).thenReturn(CONTENT_TYPE);
        when(multipartFileMock.getOriginalFilename()).thenReturn(FILE_NAME_ORIGINAL);

        File fileMock = createValidFile();
        fileMock.setName(null);

        File expectedFile = createValidFile();
        expectedFile.setName(FILE_NAME_ORIGINAL);

        when(fileRepositoryMock.save(any())).thenReturn(expectedFile);
        when(jwtService.extractIdFromToken()).thenReturn(1L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.ofNullable(module));

        // When
        File resultFile = fileService.createFile(multipartFileMock, fileMock);

        // Then
        assertNotNull(resultFile);
        assertEquals(expectedFile, resultFile);
        assertNotEquals(FILE_NAME_ARG, resultFile.getName());
        assertEquals(resultFile.getName(), FILE_NAME_ORIGINAL);

        verify(fileRepositoryMock, times(1)).save(any());
    }

    @Test
    public void testCreateFile_invalidRequestDueMissingAttributes() {
        // Given
        MultipartFile multipartFileMock = mock(MultipartFile.class);

        File fileMock = mock(File.class);

        // When, Then
        assertThrows(InvalidRequestException.class, () -> fileService.createFile(multipartFileMock, fileMock));
        verify(fileRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testCreateFile_IOException() throws IOException {
        // Given
        MultipartFile multipartFileMock = mock(MultipartFile.class);
        when(multipartFileMock.getBytes()).thenReturn(new byte[0]);

        File fileMock = createValidFile();


        when(jwtService.extractIdFromToken()).thenReturn(1L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.ofNullable(module));


        when(multipartFileMock.getBytes()).thenThrow(new IOException("Simulated IOException"));

        // When, Then
        assertThrows(IOException.class, () -> fileService.createFile(multipartFileMock, fileMock));
    }

    @Test
    public void testUpdateFile_happyPath() throws InvalidRequestException {
        // Given
        Long fileId = 1L;

        File fileMock = createValidFile();

        when(fileRepositoryMock.findById(fileId)).thenReturn(Optional.of(fileMock));
        when(fileRepositoryMock.save(any())).thenReturn(fileMock);
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        // When
        Optional<File> resultFile = fileService.updateFile(fileId, fileMock);

        // Then
        assertTrue(resultFile.isPresent());
        assertEquals(fileMock, resultFile.get());
        verify(fileRepositoryMock, times(1)).findById(fileId);
        verify(fileRepositoryMock, times(1)).save(any());
    }

    @Test
    public void testUpdateFile_notFoundById() {
        // Given
        Long fileId = 1L;

        File fileMock = createValidFile();

        when(fileRepositoryMock.findById(fileId)).thenReturn(Optional.empty());
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        // When, Then
        assertThrows(InvalidRequestException.class, () -> fileService.updateFile(fileId, fileMock));
        verify(fileRepositoryMock, times(1)).findById(fileId);
        verify(fileRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testUpdateFile_invalidRequestNoIdInFileResource() {
        // Given
        Long fileId = 1L;

        File fileMock = createValidFile();
        fileMock.setId(null);

        // When, Then
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> fileService.updateFile(fileId, fileMock));

        // Then
        verify(fileRepositoryMock, times(0)).findById(fileId);
        verify(fileRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testUpdateFile_invalidRequestMismatchedId() {
        // Given
        Long fileId = 1L;

        File fileMock = createValidFile();
        fileMock.setId(2L); // Mismatched ID

        // When, Then
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> fileService.updateFile(fileId, fileMock));

        // Then
        assertEquals(errmsg, exception.getMessage());
        verify(fileRepositoryMock, times(0)).findById(fileId);
        verify(fileRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testUpdateFile_invalidRequestMissingAttributes() {
        // Given
        Long fileId = 1L;

        File fileMock = createValidFile();
        fileMock.setVisibility(null);


        // When, Then
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> fileService.updateFile(fileId, fileMock));

        // Then
        assertEquals(ERROR_MSG_3, exception.getMessage());
        verify(fileRepositoryMock, times(0)).findById(fileId);
        verify(fileRepositoryMock, times(0)).save(any());
    }

    @Test
    public void testDeleteFile_happyPath() throws InvalidRequestException {
        // Given

        File f = createValidFile();
        when(fileRepositoryMock.findById(FILE_ID)).thenReturn(Optional.ofNullable(f));
        when(jwtService.extractIdFromToken()).thenReturn(1L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.ofNullable(module));

        // When
        boolean result = fileService.deleteFile(FILE_ID);

        // Then
        assertTrue(result);
    }

    @Test
    public void testDeleteFile_notFoundById() throws InvalidRequestException {
        // Given
        when(jwtService.extractIdFromToken()).thenReturn(1L);

        when(fileRepositoryMock.findById(FILE_ID)).thenReturn(Optional.empty());

        // When
        assertThrows(InvalidRequestException.class, () -> fileService.deleteFile(FILE_ID));

    }

    File createValidFile() {

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

        return File.builder()
                .id(1L)
                .name("SampleFile.txt")
                .visibility(Visibility.PUBLIC)
                .module(module)
                .creator(creator)
                .createdAt(new Date())
                .directory(rootDirectory)
                .build();
    }
}
