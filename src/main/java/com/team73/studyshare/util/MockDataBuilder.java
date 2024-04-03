package com.team73.studyshare.util;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.exception.StorageFileNotFoundException;
import com.team73.studyshare.model.FileType;
import com.team73.studyshare.model.Role;
import com.team73.studyshare.model.Status;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.*;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.repository.DocumentRepository;
import com.team73.studyshare.repository.FileRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.security.auth.AuthenticationService;
import com.team73.studyshare.security.auth.RegisterRequest;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.*;
import com.team73.studyshare.service.Impl.FileSystemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class MockDataBuilder {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private CardSetService cardSetService;
    private CardService cardService;
    private ModuleService moduleService;
    private DirectoryService directoryService;
    private FileRepository fileRepository;
    private FileSystemServiceImpl fileSystemServiceImpl;
    private DocumentRepository documentRepository;
    private ModuleRepository moduleRepository;
    private final JwtService jwtService;

    @Autowired
    public MockDataBuilder(AuthenticationService authenticationService, UserService userService, CardSetService cardSetService, CardService cardService,
                           ModuleService moduleService, DirectoryService directoryService, JwtService jwtService,
                           FileRepository fileRepository, FileSystemServiceImpl fileSystemServiceImpl, DocumentRepository documentRepository,
                           ModuleRepository moduleRepository) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.cardSetService = cardSetService;
        this.cardService = cardService;
        this.moduleService = moduleService;
        this.directoryService = directoryService;
        this.fileRepository = fileRepository;
        this.fileSystemServiceImpl = fileSystemServiceImpl;
        this.jwtService = jwtService;
        this.documentRepository = documentRepository;
        this.moduleRepository = moduleRepository;
    }

    public void generateMockData() throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User tabea = getUserFirst();
        User julian = getUserSecond();
        User hamza = getHamza();
        User momo = getMomo();
        User naseem = getNaseem();
        createWebModule(List.of(tabea));
        createSoftwareEngineeringIIModule(List.of(tabea, julian));
        createMathematicsIIModule(List.of(tabea, julian));
        createReactModule(List.of(tabea, julian));
        createMediaDesignModule(List.of(tabea, julian));
        createBioEngineeringModule(List.of(tabea, hamza, momo, naseem));
        createMachineLearningModule(List.of(tabea, hamza, momo, naseem));
        createFrenchModule(List.of(tabea, naseem));
        createGeographyModule(List.of(tabea, hamza));
        jwtService.testCreatedIdWasUsed = true;

    }

    private void createGeographyModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Geography Directory")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("Geography")
                .description("Explore advanced concepts in Geography")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(List.of(users.get(0)))
                .build();

        moduleService.createModule(module);

        QuizField quizFieldQuestionOne = QuizField.builder()
                .text("What is the capital of France?")
                .build();

        QuizField quizFieldQuestionTwo = QuizField.builder()
                .text("Name the longest river in the world.")
                .build();

        QuizField quizFieldQuestionThree = QuizField.builder()
                .text("Which mountain range spans the western part of South America?")
                .build();

        QuizField quizFieldAnswerOne = QuizField.builder()
                .text("The capital of France is Paris.")
                .build();

        QuizField quizFieldAnswerTwo = QuizField.builder()
                .text("The longest river in the world is the Nile.")
                .build();

        QuizField quizFieldAnswerThree = QuizField.builder()
                .text("The Andes mountain range spans the western part of South America.")
                .build();

        CardSet cardSet = CardSet.builder()
                .name("Geography Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(cardSet);

        Card cardOne = Card.builder()
                .question(quizFieldQuestionOne)
                .answer(quizFieldAnswerOne)
                .status(Status.UNDONE)
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .wrongAnswers(new ArrayList<>())
                .build();

        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(quizFieldQuestionTwo)
                .answer(quizFieldAnswerTwo)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardTwo, null, null);

        Card cardThree = Card.builder()
                .question(quizFieldQuestionThree)
                .answer(quizFieldAnswerThree)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardThree, null, null);

        Directory subDirectoryOne = Directory.builder()
                .name("Geographic Regions")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryOne);

        Directory subDirectoryTwo = Directory.builder()
                .name("Countries and Capitals")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryTwo);

        // Hier können Sie den Code hinzufügen, um Dokumente oder Dateien für das Geographie-Thema hinzuzufügen,
        // ähnlich wie im vorherigen Code-Abschnitt für Französisch.

        userService.addQuizzedCardSetToUser(cardSet.getId());
    }


    private void createFrenchModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Français Répertoire")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("Français")
                .description("Explore advanced concepts in French language")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(List.of(users.get(0)))
                .build();

        moduleService.createModule(module);

        QuizField quizFieldQuestionOne = QuizField.builder()
                .text("What is the French word for 'hello'?")
                .build();

        QuizField quizFieldQuestionTwo = QuizField.builder()
                .text("Translate 'chien' to English.")
                .build();

        QuizField quizFieldQuestionThree = QuizField.builder()
                .text("Give the French word for 'book'.")
                .build();

        QuizField quizFieldAnswerOne = QuizField.builder()
                .text("'Bonjour' is the French word for 'hello'.")
                .build();

        QuizField quizFieldAnswerTwo = QuizField.builder()
                .text("'Chien' translates to 'dog' in English.")
                .build();

        QuizField quizFieldAnswerThree = QuizField.builder()
                .text("'Livre' is the French word for 'book'.")
                .build();

        CardSet cardSet = CardSet.builder()
                .name("French Language Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(cardSet);

        Card cardOne = Card.builder()
                .question(quizFieldQuestionOne)
                .answer(quizFieldAnswerOne)
                .status(Status.UNDONE)
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .wrongAnswers(new ArrayList<>())
                .build();

        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(quizFieldQuestionTwo)
                .answer(quizFieldAnswerTwo)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardTwo, null, null);

        Card cardThree = Card.builder()
                .question(quizFieldQuestionThree)
                .answer(quizFieldAnswerThree)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardThree, null, null);

        Directory subDirectoryOne = Directory.builder()
                .name("French Literature")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryOne);

        Directory subDirectoryTwo = Directory.builder()
                .name("French Grammar")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryTwo);

        // Hier können Sie den Code hinzufügen, um Dokumente oder Dateien für das Französisch-Thema hinzuzufügen,
        // ähnlich wie im vorherigen Code-Abschnitt für Machine Learning.

        userService.addQuizzedCardSetToUser(cardSet.getId());
    }


    private void createMachineLearningModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Machine Learning Directory")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("Machine Learning")
                .description("Explore advanced concepts in Machine Learning")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(List.of(users.get(0)))
                .build();

        moduleService.createModule(module);

        QuizField quizFieldQuestionOne = QuizField.builder()
                .text("What is supervised learning in machine learning?")
                .build();

        QuizField quizFieldQuestionTwo = QuizField.builder()
                .text("Explain the concept of overfitting in machine learning.")
                .build();

        QuizField quizFieldQuestionThree = QuizField.builder()
                .text("Name a popular deep learning framework.")
                .build();

        QuizField quizFieldAnswerOne = QuizField.builder()
                .text("Supervised learning is a type of machine learning where the algorithm is trained on labeled data " +
                        "and learns to make predictions based on that labeled data.")
                .build();

        QuizField quizFieldAnswerTwo = QuizField.builder()
                .text("Overfitting occurs when a machine learning model is too complex and fits the training data " +
                        "too closely, capturing noise and leading to poor generalization on unseen data.")
                .build();

        QuizField quizFieldAnswerThree = QuizField.builder()
                .text("TensorFlow is a popular deep learning framework.")
                .build();

        CardSet cardSet = CardSet.builder()
                .name("Machine Learning Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(cardSet);

        Card cardOne = Card.builder()
                .question(quizFieldQuestionOne)
                .answer(quizFieldAnswerOne)
                .status(Status.UNDONE)
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .wrongAnswers(new ArrayList<>())
                .build();

        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(quizFieldQuestionTwo)
                .answer(quizFieldAnswerTwo)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardTwo, null, null);

        Card cardThree = Card.builder()
                .question(quizFieldQuestionThree)
                .answer(quizFieldAnswerThree)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardThree, null, null);

        Directory subDirectoryOne = Directory.builder()
                .name("Research Papers")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryOne);

        Directory subDirectoryTwo = Directory.builder()
                .name("Projects")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryTwo);

        userService.addQuizzedCardSetToUser(cardSet.getId());
    }


    private void createBioEngineeringModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Bioengineering Directory")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("Bioengineering")
                .description("Explore advanced concepts in Bioengineering")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(users)
                .build();

        moduleService.createModule(module);

        QuizField quizFieldQuestionOne = QuizField.builder()
                .text("What is the primary goal of bioengineering?")
                .build();

        QuizField quizFieldQuestionTwo = QuizField.builder()
                .text("Give an example of an application of bioengineering in the medical field.")
                .build();

        QuizField quizFieldQuestionThree = QuizField.builder()
                .text("What are some ethical considerations in bioengineering?")
                .build();

        QuizField quizFieldAnswerOne = QuizField.builder()
                .text("The primary goal of bioengineering is to apply principles of engineering to biological systems " +
                        "to design and create new products, processes, and technologies.")
                .build();

        QuizField quizFieldAnswerTwo = QuizField.builder()
                .text("One example of a bioengineering application in the medical field is the development of artificial organs " +
                        "such as artificial hearts and kidneys.")
                .build();

        QuizField quizFieldAnswerThree = QuizField.builder()
                .text("Ethical considerations in bioengineering include issues related to genetic engineering, " +
                        "human cloning, and the potential for unintended consequences in modifying living organisms.")
                .build();

        CardSet cardSet = CardSet.builder()
                .name("Bioengineering Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(cardSet);

        Card cardOne = Card.builder()
                .question(quizFieldQuestionOne)
                .answer(quizFieldAnswerOne)
                .status(Status.UNDONE)
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .wrongAnswers(new ArrayList<>())
                .build();

        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(quizFieldQuestionTwo)
                .answer(quizFieldAnswerTwo)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardTwo, null, null);

        Card cardThree = Card.builder()
                .question(quizFieldQuestionThree)
                .answer(quizFieldAnswerThree)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardThree, null, null);

        Directory subDirectoryOne = Directory.builder()
                .name("Research Papers")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryOne);

        Directory subDirectoryTwo = Directory.builder()
                .name("Lab Reports")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryTwo);
        userService.addQuizzedCardSetToUser(cardSet.getId());
    }


    private void createMediaDesignModule(List<User> users) throws InvalidRequestException, IOException {
        User user = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Media Design Directory")
                .visibility(Visibility.PUBLIC)
                .creator(user)
                .mainDirectory(null)
                .build();

        Module mediaDesignModule = Module.builder()
                .name("Media Design")
                .description("Explore concepts in media design")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(user)
                .rootDirectory(rootDirectory)
                .owners(List.of(user))
                .build();
        moduleService.createModule(mediaDesignModule);

        CardSet mediaDesignCardSet = CardSet.builder()
                .name("Media Design Basics Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(mediaDesignModule)
                .creator(user)
                .build();
        cardSetService.createCardSet(mediaDesignCardSet);

        Card cardOne = Card.builder()
                .question(QuizField.builder().text("What is the principle of contrast in media design?").build())
                .answer(QuizField.builder().text("Contrast refers to the arrangement of opposite elements (light vs. dark, rough vs. smooth, large vs. small) to create visual interest or draw attention to particular elements.").build())
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(user)
                .cardSet(mediaDesignCardSet)
                .build();
        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(QuizField.builder().text("What is the role of typography in media design?").build())
                .answer(QuizField.builder().text("Typography in media design involves choosing typefaces, adjusting font size, and spacing to improve legibility and create a visually pleasing and effective design.").build())
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(user)
                .cardSet(mediaDesignCardSet)
                .build();
        cardService.createCard(cardTwo, null, null);

        Card cardThree = Card.builder()
                .question(QuizField.builder().text("Explain the use of color theory in media design.").build())
                .answer(QuizField.builder().text("Color theory in media design is used to create a color palette that evokes the desired response from the audience, enhances aesthetics, and ensures readability and visibility.").build())
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(user)
                .cardSet(mediaDesignCardSet)
                .build();
        cardService.createCard(cardThree, null, null);
    }

    private void createSoftwareEngineeringIIModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Software Engineering II Directory")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("Software Engineering II")
                .description("Explore advanced concepts in Software Engineering II")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(List.of(users.get(0)))
                .build();

        moduleService.createModule(module);

        QuizField quizFieldQuestionOne = QuizField.builder()
                .text("What does Dependency Injection mean?")
                .build();

        QuizField quizFieldQuestionTwo = QuizField.builder()
                .text("What does Inversion of Control (IoC) mean?")
                .build();

        QuizField quizFieldQuestionThree = QuizField.builder()
                .text("What are some common types or approaches for implementing Dependency Injection in software development?")
                .build();

        QuizField quizFieldAnswerOne = QuizField.builder()
                .text("Dependency Injection is a specific form of the Inversion-of-Control (IoC) design pattern in which an object receives other objects it requires " +
                        "(or depends on, hence: dependencies) rather than the object requesting or obtaining them.")
                .build();

        QuizField quizFieldAnswerTwo = QuizField.builder()
                .text("Inversion-of-Control (IoC) is a design pattern in which code is not called from other parts of the code (like one method calls another), but are invoked from outside the code," +
                        " such as triggered by an event or invoked from a framework")
                .build();

        QuizField quizFieldAnswerThree = QuizField.builder()
                .text("Constructor injection, Setter injection and Interface injection.")
                .build();

        CardSet cardSet = CardSet.builder()
                .name("Software Engineering II Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(cardSet);

        Card cardOne = Card.builder()
                .question(quizFieldQuestionOne)
                .answer(quizFieldAnswerOne)
                .status(Status.UNDONE)
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .wrongAnswers(new ArrayList<>())
                .build();

        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(quizFieldQuestionTwo)
                .answer(quizFieldAnswerTwo)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardTwo, null, null);

        Card cardThree = Card.builder()
                .question(quizFieldQuestionThree)
                .answer(quizFieldAnswerThree)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardThree, null, null);

        Directory subDirectoryOne = Directory.builder()
                .name("Exams")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryOne);

        Directory subDirectoryTwo = Directory.builder()
                .name("lecture notes")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryTwo);

        Resource resourceJPG = fileSystemServiceImpl.getFileFromJPGDirectory("Springboot for Beginners.jpg");

        Document documentOne = Document.builder()
                .type(FileType.JPG)
                .data(resourceJPG.getContentAsByteArray())
                .build();

        documentRepository.save(documentOne);

        File file = File.builder()
                .name("Springboot for Beginners")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .directory(subDirectoryOne)
                .createdAt(new Date())
                .documentId(documentOne.getId())
                .type(FileType.JPG)
                .build();

        saveFileAndIncrementCount(file, module);

        Resource resourcePNG = fileSystemServiceImpl.getFileFromPNGDirectory("Springboot for Advanced.png");

        Document document = Document.builder()
                .type(FileType.PNG)
                .data(resourcePNG.getContentAsByteArray())
                .build();

        documentRepository.save(document);

        File file2 = File.builder()
                .name("Springboot for Advanced")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .directory(subDirectoryOne)
                .createdAt(new Date())
                .documentId(document.getId())
                .type(FileType.PNG)
                .build();

        saveFileAndIncrementCount(file2, module);
        userService.addQuizzedCardSetToUser(cardSet.getId());
    }


    private void createWebModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Web Engineering II Directory")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("Web Engineering II")
                .description("Explore advanced concepts in web engineering")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(users)
                .build();

        moduleService.createModule(module);

        Resource imageQuizFieldQuestion = fileSystemServiceImpl.getFileFromPNGDirectory("HTTP methods questions.png");

        Document documentQuizFieldQuestion = Document.builder()
                .type(FileType.PNG)
                .data(imageQuizFieldQuestion.getContentAsByteArray())
                .build();

        documentRepository.save(documentQuizFieldQuestion);

        QuizField quizFieldQuestionOne = QuizField.builder()
                .text("What are the common HTTP methods?")
                .documentId(documentQuizFieldQuestion.getId())
                .build();

        QuizField quizFieldQuestionTwo = QuizField.builder()
                .text("What does the status code 401 stand for in HTTP?")
                .build();

        Resource imageQuizFieldAnswer = fileSystemServiceImpl.getFileFromPNGDirectory("HTTP methods answers.png");

        Document documentQuizFieldAnswer = Document.builder()
                .type(FileType.PNG)
                .data(imageQuizFieldAnswer.getContentAsByteArray())
                .build();

        documentRepository.save(documentQuizFieldAnswer);

        QuizField quizFieldAnswerOne = QuizField.builder()
                .text("GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH, TRACE, CONNECT")
                .documentId(documentQuizFieldAnswer.getId())
                .build();

        QuizField quizFieldAnswerTwo = QuizField.builder()
                .text("Unauthorized")
                .build();

        CardSet cardSet = CardSet.builder()
                .name("Web Engineering II Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(cardSet);

        Card cardOne = Card.builder()
                .question(quizFieldQuestionOne)
                .answer(quizFieldAnswerOne)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(quizFieldQuestionTwo)
                .answer(quizFieldAnswerTwo)
                .status(Status.BAD)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardTwo, null, null);

        Card cardThree = Card.builder()
                .question(QuizField.builder().text("What is RESTful architecture?").build())
                .answer(QuizField.builder().text("Representational State Transfer (REST) is an architectural style for designing networked applications.").build())
                .status(Status.GOOD)
                .creator(userFirst)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardThree, null, null);

        Card cardFour = Card.builder()
                .question(QuizField.builder().text("Explain the concept of CSRF in web security.").build())
                .answer(QuizField.builder().text("CSRF (Cross-Site Request Forgery) is an attack where a malicious website sends a request on behalf of a user.").build())
                .status(Status.GOOD)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardFour, null, null);

        Card cardFive = Card.builder()
                .question(QuizField.builder().text("What is the role of a CDN in web development?").build())
                .answer(QuizField.builder().text("A Content Delivery Network (CDN) improves website performance by caching and delivering content from multiple servers located worldwide.").build())
                .status(Status.OK)
                .creator(userFirst)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardFive, null, null);

        Card cardSix = Card.builder()
                .question(QuizField.builder().text("Explain the concept of a Single Page Application (SPA).").build())
                .answer(QuizField.builder().text("A Single Page Application (SPA) is a web application or website that interacts with the user by dynamically rewriting the current page, rather than loading entire new pages from the server.").build())
                .status(Status.OK)
                .flagged(false)
                .creator(userFirst)
                .wrongAnswers(new ArrayList<>())
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardSix, null, null);

        Card cardSeven = Card.builder()
                .question(QuizField.builder().text("What is the purpose of the HTTP OPTIONS method?").build())
                .answer(QuizField.builder().text("The HTTP OPTIONS method is used to describe the communication options for the target resource, allowing the client to determine the options and requirements associated with a resource, or the capabilities of a server.").build())
                .status(Status.OK)
                .flagged(false)
                .creator(userFirst)
                .wrongAnswers(new ArrayList<>())
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardSeven, null, null);

        Card cardEight = Card.builder()
                .question(QuizField.builder().text("What is the purpose of the HTTP PUT method?").build())
                .answer(QuizField.builder().text("The HTTP PUT method is used to update a resource or create a new resource if it does not exist.").build())
                .status(Status.GOOD)
                .creator(userFirst)
                .wrongAnswers(new ArrayList<>())
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardEight, null, null);

        Card cardNine = Card.builder()
                .question(QuizField.builder().text("Explain the concept of a WebSocket in web development.").build())
                .answer(QuizField.builder().text("WebSocket is a communication protocol that provides full-duplex communication channels over a single TCP connection, allowing for real-time updates between the client and server.").build())
                .status(Status.OK)
                .flagged(false)
                .creator(userFirst)
                .wrongAnswers(new ArrayList<>())
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardNine, null, null);


        CardSet cardSet2 = CardSet.builder()
                .name("Javascript Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSet2 = cardSetService.createCardSet(cardSet2);

// Erstellen von Karten für cardSet2
        QuizField cardSet2Question1 = QuizField.builder()
                .text("What is JavaScript used for in web development?")
                .build();

        QuizField cardSet2Answer1 = QuizField.builder()
                .text("JavaScript is a programming language used for adding interactivity and dynamic behavior to websites.")
                .build();

        Card cardSet2Card1 = Card.builder()
                .question(cardSet2Question1)
                .answer(cardSet2Answer1)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet2)
                .build();

        cardService.createCard(cardSet2Card1, null, null);

// Erstellen von weiteren Karten für cardSet2 (insgesamt 5 Karten).
        QuizField cardSet2Question2 = QuizField.builder()
                .text("What is the syntax for declaring a variable in JavaScript?")
                .build();

        QuizField cardSet2Answer2 = QuizField.builder()
                .text("In JavaScript, you can declare a variable using 'var', 'let', or 'const' followed by the variable name.")
                .build();

        Card cardSet2Card2 = Card.builder()
                .question(cardSet2Question2)
                .answer(cardSet2Answer2)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet2)
                .build();

        cardService.createCard(cardSet2Card2, null, null);

// Erstellen von weiteren Karten für cardSet2 (insgesamt 5 Karten).
// Fügen Sie ähnlich wie oben Karten hinzu, um insgesamt 5 Karten für cardSet2 zu erstellen.


        CardSet cardSet3 = CardSet.builder()
                .name("React Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSet3 = cardSetService.createCardSet(cardSet3);

// Erstellen von Karten für cardSet3 (React Quiz)
        QuizField cardSet3Question1 = QuizField.builder()
                .text("What is React?")
                .build();

        QuizField cardSet3Answer1 = QuizField.builder()
                .text("React is a JavaScript library for building user interfaces, particularly for single-page applications.")
                .build();

        Card cardSet3Card1 = Card.builder()
                .question(cardSet3Question1)
                .answer(cardSet3Answer1)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet3)
                .build();

        cardService.createCard(cardSet3Card1, null, null);

// Erstellen von weiteren Karten für cardSet3 (insgesamt 5 Karten).
        QuizField cardSet3Question2 = QuizField.builder()
                .text("What is JSX in React?")
                .build();

        QuizField cardSet3Answer2 = QuizField.builder()
                .text("JSX (JavaScript XML) is a syntax extension for JavaScript often used with React to describe what the UI should look like.")
                .build();

        Card cardSet3Card2 = Card.builder()
                .question(cardSet3Question2)
                .answer(cardSet3Answer2)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet3)
                .build();

        cardService.createCard(cardSet3Card2, null, null);
        CardSet cardSet4 = CardSet.builder()
                .name("Angular Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSet4 = cardSetService.createCardSet(cardSet4);

// Erstellen von Karten für cardSet4 (Angular Quiz)
        QuizField cardSet4Question1 = QuizField.builder()
                .text("What is Angular?")
                .build();

        QuizField cardSet4Answer1 = QuizField.builder()
                .text("Angular is a TypeScript-based open-source framework for building web applications, developed and maintained by Google.")
                .build();

        Card cardSet4Card1 = Card.builder()
                .question(cardSet4Question1)
                .answer(cardSet4Answer1)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet4)
                .build();

        cardService.createCard(cardSet4Card1, null, null);

// Erstellen von weiteren Karten für cardSet4 (insgesamt 5 Karten).
        QuizField cardSet4Question2 = QuizField.builder()
                .text("What is the purpose of NgModule in Angular?")
                .build();

        QuizField cardSet4Answer2 = QuizField.builder()
                .text("NgModule is a decorator used to define a module in Angular, which can include components, services, and other features.")
                .build();

        Card cardSet4Card2 = Card.builder()
                .question(cardSet4Question2)
                .answer(cardSet4Answer2)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet4)
                .build();

        cardService.createCard(cardSet4Card2, null, null);

// Erstellen von weiteren Karten für cardSet4 (insgesamt 5 Karten).
// Fügen Sie ähnlich wie oben Karten hinzu, um insgesamt 5 Karten für cardSet4 zu erstellen.


        CardSet cardSet5 = CardSet.builder()
                .name("VueJS Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSet5 = cardSetService.createCardSet(cardSet5);

// Erstellen von Karten für cardSet5 (VueJS Quiz)
        QuizField cardSet5Question1 = QuizField.builder()
                .text("What is Vue.js?")
                .build();

        QuizField cardSet5Answer1 = QuizField.builder()
                .text("Vue.js is a progressive JavaScript framework for building user interfaces. It is designed to be incrementally adoptable.")
                .build();

        Card cardSet5Card1 = Card.builder()
                .question(cardSet5Question1)
                .answer(cardSet5Answer1)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet5)
                .build();

        cardService.createCard(cardSet5Card1, null, null);

// Erstellen von weiteren Karten für cardSet5 (insgesamt 5 Karten).
        QuizField cardSet5Question2 = QuizField.builder()
                .text("What is the Vue.js directive 'v-model' used for?")
                .build();

        QuizField cardSet5Answer2 = QuizField.builder()
                .text("The 'v-model' directive is used for two-way data binding in Vue.js, allowing you to bind form input values to data properties.")
                .build();

        Card cardSet5Card2 = Card.builder()
                .question(cardSet5Question2)
                .answer(cardSet5Answer2)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet5)
                .build();

        cardService.createCard(cardSet5Card2, null, null);


        Directory subDirectory = Directory.builder()
                .name("lecture notes")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectory);

        Resource resourcePNG = fileSystemServiceImpl.getFileFromPNGDirectory("React For Beginners.png");

        Document document = Document.builder()
                .type(FileType.PNG)
                .data(resourcePNG.getContentAsByteArray())
                .build();

        documentRepository.save(document);

        File file = File.builder()
                .name("React for Beginners")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .directory(subDirectory)
                .createdAt(new Date())
                .documentId(document.getId())
                .type(FileType.PNG)
                .build();

        saveFileAndIncrementCount(file, module);
        userService.addQuizzedCardSetToUser(cardSet.getId());

    }


    private void createReactModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("React Module Directory")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("React Module")
                .description("Explore React.js concepts and development")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(users)
                .build();

        moduleService.createModule(module);

        QuizField quizFieldQuestionOne = QuizField.builder()
                .text("What is JSX in React?")
                .build();

        QuizField quizFieldQuestionTwo = QuizField.builder()
                .text("Explain the concept of Virtual DOM in React.")
                .build();

        QuizField quizFieldAnswerOne = QuizField.builder()
                .text("JSX (JavaScript XML) is a syntax extension for JavaScript, often used with React to describe what the UI should look like.")
                .build();

        QuizField quizFieldAnswerTwo = QuizField.builder()
                .text("The Virtual DOM is a lightweight copy of the real DOM in React, designed to improve performance by minimizing direct manipulation of the actual DOM.")
                .build();

        CardSet cardSet = CardSet.builder()
                .name("React Quiz")
                .visibility(Visibility.PRIVATE)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(cardSet);

        Card cardOne = Card.builder()
                .question(quizFieldQuestionOne)
                .answer(quizFieldAnswerOne)
                .status(Status.OK)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardOne, null, null);

        Card cardTwo = Card.builder()
                .question(quizFieldQuestionTwo)
                .answer(quizFieldAnswerTwo)
                .status(Status.GOOD)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .flagged(false)
                .cardSet(cardSet)
                .build();

        cardService.createCard(cardTwo, null, null);

        Directory subDirectoryOne = Directory.builder()
                .name("Exams")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryOne);

        Directory subDirectoryTwo = Directory.builder()
                .name("lecture notes")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectoryTwo);

        Resource resourceJPG = fileSystemServiceImpl.getFileFromJPGDirectory("Springboot for Beginners.jpg");

        Document document = Document.builder()
                .type(FileType.JPG)
                .data(resourceJPG.getContentAsByteArray())
                .build();

        documentRepository.save(document);

        File file = File.builder()
                .name("Springboot for Beginners")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .directory(subDirectoryOne)
                .createdAt(new Date())
                .documentId(document.getId())
                .type(FileType.JPG)
                .build();

        saveFileAndIncrementCount(file, module);

        Resource resourcePNG = fileSystemServiceImpl.getFileFromPNGDirectory("Springboot for Advanced.png");

        Document documentPNG = Document.builder()
                .type(FileType.PNG)
                .data(resourcePNG.getContentAsByteArray())
                .build();

        documentRepository.save(documentPNG);

        File file2 = File.builder()
                .name("Springboot for Advanced")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .directory(subDirectoryOne)
                .createdAt(new Date())
                .documentId(documentPNG.getId())
                .type(FileType.PNG)
                .build();

        saveFileAndIncrementCount(file2, module);
    }


    private void createMathematicsIIModule(List<User> users) throws InvalidRequestException, IOException, StorageFileNotFoundException {
        User userFirst = users.get(0);

        Directory rootDirectory = Directory.builder()
                .name("Mathematics II Directory")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(null)
                .build();

        Module module = Module.builder()
                .name("Mathematics II")
                .description("Explore advanced concepts in Mathematics II")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardSetCount(0)
                .fileCount(0)
                .creator(userFirst)
                .rootDirectory(rootDirectory)
                .owners(users)
                .build();

        moduleService.createModule(module);

        QuizField originalQuestion = QuizField.builder()
                .text("What is the Pythagorean theorem?")
                .build();

        QuizField originalAnswer = QuizField.builder()
                .text("The Pythagorean theorem states that in a right-angled triangle, the square of the length of the hypotenuse (the side opposite the right angle) " +
                        "is equal to the sum of the squares of the lengths of the other two sides.")
                .build();

        CardSet originalCardSet = CardSet.builder()
                .name("Pythagorean Theorem Quiz")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(originalCardSet);

        Card originalCard = Card.builder()
                .question(originalQuestion)
                .answer(originalAnswer)
                .status(Status.UNDONE)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .flagged(false)
                .cardSet(originalCardSet)
                .build();

        cardService.createCard(originalCard, null, null);

        QuizField logicQuestionOne = QuizField.builder()
                .text("Solve the following logic puzzle: A or B, not A, therefore...")
                .build();

        QuizField logicAnswerOne = QuizField.builder()
                .text("B")
                .build();

        QuizField logicQuestionTwo = QuizField.builder()
                .text("If all cats have tails, and Fluffy is a cat, does Fluffy have a tail?")
                .build();

        QuizField logicAnswerTwo = QuizField.builder()
                .text("Yes")
                .build();

        CardSet logicCardSet = CardSet.builder()
                .name("Mathematics II Logic Questions")
                .visibility(Visibility.PUBLIC)
                .score(0)
                .cardCount(0)
                .module(module)
                .creator(userFirst)
                .build();

        cardSetService.createCardSet(logicCardSet);

        Card logicCardOne = Card.builder()
                .question(logicQuestionOne)
                .answer(logicAnswerOne)
                .wrongAnswers(new ArrayList<>())
                .status(Status.UNDONE)
                .flagged(false)
                .creator(userFirst)
                .cardSet(logicCardSet)
                .build();

        cardService.createCard(logicCardOne, null, null);

        Card logicCardTwo = Card.builder()
                .question(logicQuestionTwo)
                .answer(logicAnswerTwo)
                .status(Status.UNDONE)
                .flagged(false)
                .wrongAnswers(new ArrayList<>())
                .creator(userFirst)
                .cardSet(logicCardSet)
                .build();

        cardService.createCard(logicCardTwo, null, null);

        Directory subDirectory = Directory.builder()
                .name("lecture notes")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .mainDirectory(rootDirectory)
                .build();

        directoryService.createDirectory(subDirectory);

        Resource resourcePNG = fileSystemServiceImpl.getFileFromPNGDirectory("Pythagorean Theorem.png");

        Document document = Document.builder()
                .type(FileType.PNG)
                .data(resourcePNG.getContentAsByteArray())
                .build();

        documentRepository.save(document);

        File file = File.builder()
                .name("Pythagorean Theorem")
                .visibility(Visibility.PUBLIC)
                .creator(userFirst)
                .directory(subDirectory)
                .createdAt(new Date())
                .documentId(document.getId())
                .type(FileType.PNG)
                .build();

        saveFileAndIncrementCount(file, module);
    }


    private User getUserFirst() throws InvalidRequestException {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Tabea")
                .email("tabea@gmail.com")
                .password("tabeateam73")
                .visibility(Visibility.PUBLIC)
                .role(Role.ADMIN)
                .description("Hello, I'm Tabea!")
                .build();

        authenticationService.register(registerRequest);

        Optional<User> userOptional = userService.getUserById(1L);
        return userOptional.orElse(null);
    }

    private User getUserSecond() throws InvalidRequestException {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Julian")
                .email("julian@gmail.com")
                .password("julianteam73")
                .visibility(Visibility.PUBLIC)
                .role(Role.USER)
                .description("Hello, I'm Julian!")
                .build();

        authenticationService.register(registerRequest);

        Optional<User> userOptional = userService.getUserById(2L);
        return userOptional.orElse(null);
    }

    private User getHamza() throws InvalidRequestException {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Hamza")
                .email("hamza@gmail.com")
                .password("hamzateam73")
                .visibility(Visibility.PUBLIC)
                .role(Role.USER)
                .description("Hello, I'm Hamza!")
                .build();

        authenticationService.register(registerRequest);

        Optional<User> userOptional = userService.getUserById(3L);
        return userOptional.orElse(null);
    }

    private User getMomo() throws InvalidRequestException {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Momo")
                .email("momo@gmail.com")
                .password("momoteam73")
                .visibility(Visibility.PUBLIC)
                .role(Role.USER)
                .description("Hello, I'm Momo!")
                .build();

        authenticationService.register(registerRequest);

        Optional<User> userOptional = userService.getUserById(4L);
        return userOptional.orElse(null);
    }

    private User getNaseem() throws InvalidRequestException {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Naseem")
                .email("naseem@gmail.com")
                .password("naseemteam73")
                .visibility(Visibility.PUBLIC)
                .role(Role.USER)
                .description("Hello, I'm Naseem!")
                .build();

        authenticationService.register(registerRequest);

        Optional<User> userOptional = userService.getUserById(5L);
        return userOptional.orElse(null);
    }

    private void saveFileAndIncrementCount(File file, Module module) {
        Optional<Module> moduleOpt = moduleRepository.findById(module.getId());

        if (moduleOpt.isPresent()) {
            var moduleDB = moduleOpt.get();
            file.setModule(moduleDB);
            fileRepository.save(file);
            moduleDB.incrementFileCount();
            moduleRepository.save(moduleDB);
        }
    }


}
