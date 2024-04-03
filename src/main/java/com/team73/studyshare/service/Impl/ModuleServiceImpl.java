package com.team73.studyshare.service.Impl;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.repository.*;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.DocumentService;
import com.team73.studyshare.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImpl implements ModuleService {
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final CardSetRepository cardSetRepository;
    private final DocumentService documentService;
    private final JwtService jwtService;

    @Autowired
    public ModuleServiceImpl(UserRepository userRepository, ModuleRepository moduleRepository,
                             CardSetRepository cardSetRepository, DocumentService documentService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
        this.cardSetRepository = cardSetRepository;
        this.documentService = documentService;
        this.jwtService = jwtService;
    }

    @Override
    public List<Module> getAllModules() {
        Long requestingUserId = jwtService.extractIdFromToken();

        return moduleRepository.findAll().stream()
                .filter(module -> module.getVisibility() == Visibility.PUBLIC ||
                        module.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId)))
                .toList();
    }

    @Override
    public Optional<Module> getModuleById(Long moduleId) throws InvalidRequestException {
        Optional<Module> moduleOptional = moduleRepository.findById(moduleId);

        if (moduleOptional.isEmpty()) {
            return Optional.empty();
        }

        Module requestedModule = moduleOptional.get();
        Long requestingUserId = jwtService.extractIdFromToken();

        if (requestedModule.getVisibility().equals(Visibility.PUBLIC) ||
                requestedModule.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            return Optional.of(requestedModule);
        } else {
            throw new InvalidRequestException("You do not have permission to access this module.");
        }
    }

    @Override
    public List<CardSet> getCardSetsFromModule(Long moduleId) throws InvalidRequestException {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new InvalidRequestException("Module with ID " + moduleId + " does not exist."));

        List<CardSet> cardSets = new ArrayList<>();
        Long requestingUserId = jwtService.extractIdFromToken();

        if (module.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            cardSets = module.getCardSets();
        } else if (module.getVisibility().equals(Visibility.PUBLIC)) {
            cardSets = module.getCardSets().stream()
                    .filter(cardSet -> cardSet.getVisibility().equals(Visibility.PUBLIC))
                    .toList();
        }
        return cardSets;
    }

    @Override
    public Module createModule(Module module) throws InvalidRequestException {
        checkIfModuleIsInvalidForCreation(module);
        Long requestingUserId = jwtService.extractIdFromToken();

        if (!module.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to create this module.");
        }

        module.setCreatedAt(new Date());
        module.getRootDirectory().setCreatedAt(new Date()); //TODO:Validierung nochmal überarbeiten

        for (User user : module.getOwners()) {
            user.addModule(module);
        }

        return moduleRepository.save(module);
    }

    @Override
    public Optional<Module> updateModule(Long moduleId, Module updatedModule) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();

        if (updatedModule == null || updatedModule.getId() == null) {
            throw new InvalidRequestException("No 'id' is present in the Module object or given module is null.");
        }

        if (!updatedModule.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
            throw new InvalidRequestException("You do not have permission to update this module.");
        }

        if (!updatedModule.getId().equals(moduleId))
            throw new InvalidRequestException("The moduleId in the path variable does not match the id in the request body.");

        checkIfModuleIsInvalidForCreation(updatedModule);

        return moduleRepository.findById(moduleId)
                .map(existingModule-> {
                    existingModule.setName(updatedModule.getName());
                    existingModule.setVisibility(updatedModule.getVisibility());
                    existingModule.setDescription(updatedModule.getDescription());
                    //existingModule.setOwners(updatedModule.getOwners());
                    
                    Module savedModule = moduleRepository.save(existingModule);
                    return Optional.of(savedModule);
                })
                .orElseThrow(() -> new InvalidRequestException("CardSet with ID " + moduleId + " does not exist."));
    }

    public Optional<Module> updateScoreOfModule(Long moduleId) throws InvalidRequestException {

        return moduleRepository.findById(moduleId)
                .map(existingModule -> {
                    List<CardSet> cardSets = existingModule.getCardSets().stream()
                            .filter(cardSet -> !cardSet.getCards().isEmpty())
                            .collect(Collectors.toList());

                    existingModule.setScore(calculateScoreOfModule(cardSets));

                    Module savedModule = moduleRepository.save(existingModule);
                    return Optional.of(savedModule);
                })
                .orElseThrow(() -> new InvalidRequestException("CardSet with ID " + moduleId + " does not exist."));
    }

    @Override
    public void deleteModule(Long moduleId) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();

        if (!moduleRepository.existsById(moduleId)) {
            throw new InvalidRequestException("Module with ID " + moduleId + " does not exist.");
        }
        Optional<Module> m = moduleRepository.findById(moduleId);
        if(m.isPresent()){
            Module module = m.get();
            if (!module.getOwners().stream().anyMatch(owner -> owner.getId().equals(requestingUserId))) {
                throw new InvalidRequestException("You do not have permission to delete this module.");
            }
            documentService.deleteAllDocumentsFromModul(moduleId);
            module.setRootDirectory(null);
            module.getCreator().getModules().remove(module);
            module.setCreator(null);
            List<CardSet> cardSets = module.getCardSets();
            documentService.deleteAllDocumentsFromCardSets(cardSets);
            module.setCardSets(null);
            cardSets.forEach(cardSet -> cardSet.setModule(null));
            cardSetRepository.deleteAll(cardSets);
            List<User> moduleOwners = module.getOwners();
            moduleOwners.forEach(user -> {
                user.getModules().remove(module);
            });
            userRepository.saveAll(moduleOwners);
        }
        moduleRepository.deleteById(moduleId);
        Optional<Module> shouldBeEmpty = moduleRepository.findById(moduleId);
        if(shouldBeEmpty.isPresent()){
            throw new InvalidRequestException("Module could not be deleted from the database.");
        }
    }

    private List<String> getInvalidFieldsForCreation(Module module) {
        List<String> invalidFields = new ArrayList<>();

        if (module.getName() == null) {
            invalidFields.add("name");
        }

        if (module.getVisibility() == null) {
            invalidFields.add("visibility");
        }

        if (module.getScore() == null) {
            invalidFields.add("score");
        }

        if (module.getCreator() == null) {
            invalidFields.add("creator");
        }

        if (module.getRootDirectory() == null) {
            invalidFields.add("rootDirectory");
        }

        if (module.getOwners() == null) {
            invalidFields.add("owners");
        }

        return invalidFields;
    }

    private void checkIfModuleIsInvalidForCreation(Module module) throws InvalidRequestException {
        List<String> invalidFields = getInvalidFieldsForCreation(module);

        if (!invalidFields.isEmpty()) {
            throw new InvalidRequestException("Invalid module fields: " + String.join(", ", invalidFields));
        }
    }

    private Integer calculateScoreOfModule(List<CardSet> cardSets) {
        int numberOfCardSets = cardSets.size();

        if (numberOfCardSets == 0) {
            return 0;
        }

        int sumOfScores = 0;

        for (CardSet cardSet : cardSets) {
            sumOfScores += cardSet.getScore();
        }

        int rawPercentage = (int) Math.round(((double) sumOfScores / numberOfCardSets));
        return Math.min(100, Math.max(0, rawPercentage));
    }
}