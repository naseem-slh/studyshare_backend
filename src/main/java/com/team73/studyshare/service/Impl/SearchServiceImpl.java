package com.team73.studyshare.service.Impl;

import com.team73.studyshare.model.ItemType;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.File;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.repository.CardSetRepository;
import com.team73.studyshare.repository.FileRepository;
import com.team73.studyshare.repository.ModuleRepository;
import com.team73.studyshare.repository.UserRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.team73.studyshare.model.data.Module;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final CardSetRepository cardSetRepository;
    private final ModuleRepository moduleRepository;
    private final FileRepository fileRepository;
    private final JwtService jwtService;


    @Autowired
    public SearchServiceImpl(UserRepository userRepository, CardSetRepository cardSetRepository,
                             ModuleRepository moduleRepository, FileRepository fileRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.cardSetRepository = cardSetRepository;
        this.moduleRepository = moduleRepository;
        this.fileRepository = fileRepository;
        this.jwtService = jwtService;
    }

    public List<?> searchItems(String query, ItemType type) {
        switch (type) {
            case USER:
                return getUsers(query);
            case CARD_SET:
                return getCardSets(query);
            case MODULE:
                return getModules(query);
            case FILE:
                return getFiles(query);
            case ALL:
                List<Object> allResults = new ArrayList<>();
                allResults.addAll(getUsers(query));
                allResults.addAll(getCardSets(query));
                allResults.addAll(getModules(query));
                allResults.addAll(getFiles(query));
                return allResults;
            default:
                throw new IllegalArgumentException("Invalid item type");
        }
    }

    private List<User> getUsers(String query) {
        Long requestingUserId = jwtService.extractIdFromToken();

        return userRepository.searchUsers(query).stream().
                filter(user -> !user.getId().equals(requestingUserId)).toList();
    }

    private List<CardSet> getCardSets(String query) {
        Long requestingUserId = jwtService.extractIdFromToken();

        return cardSetRepository.searchCardSets(query).stream()
                .filter(cardSet -> cardSet.getModule().getOwners().stream().
                        noneMatch(owner -> owner.getId().equals(requestingUserId)))
                .toList();
    }

    private List<Module> getModules(String query) {
        Long requestingUserId = jwtService.extractIdFromToken();

        return moduleRepository.searchModules(query).stream()
                .filter(module -> module.getOwners().stream().
                        noneMatch(owner -> owner.getId().equals(requestingUserId)))
                .toList();
    }

    private List<File> getFiles(String query) {
        Long requestingUserId = jwtService.extractIdFromToken();

        return fileRepository.searchFiles(query).stream()
                .filter(file -> file.getModule().getOwners().stream().
                        noneMatch(owner -> owner.getId().equals(requestingUserId)))
                .toList();
    }

}

