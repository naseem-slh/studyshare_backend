package com.team73.studyshare.service.Impl;

import com.team73.studyshare.exception.InvalidRequestException;
import com.team73.studyshare.model.Visibility;
import com.team73.studyshare.model.data.CardSet;
import com.team73.studyshare.model.data.Module;
import com.team73.studyshare.model.data.User;
import com.team73.studyshare.model.requestEntity.ChangePasswordRequest;
import com.team73.studyshare.repository.CardSetRepository;
import com.team73.studyshare.repository.UserRepository;
import com.team73.studyshare.security.config.JwtService;
import com.team73.studyshare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final CardSetRepository cardSetRepository;
    private final JwtService jwtService;

    @Override
    public List<User> getAllUsers() {
        Long requestingUserId = jwtService.extractIdFromToken();

        return userRepository.findAll().stream()
                .filter(user -> user.getVisibility() == Visibility.PUBLIC || user.getId().equals(requestingUserId))
                .toList();
    }

    @Override
    public Optional<User> getUserById(Long userId) throws InvalidRequestException {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User requestedUser = userOptional.get();

        if (requestedUser.getVisibility().equals(Visibility.PUBLIC) ||
                requestedUser.getId().equals(jwtService.extractIdFromToken())) {
            return userOptional;
        } else {
            throw new InvalidRequestException("You do not have permission to access this user's information.");
        }
    }

    @Override
    public List<Module> getModulesForUser(Long userId) throws InvalidRequestException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidRequestException("User with ID " + userId + " not found."));

        Long requestingUserId = jwtService.extractIdFromToken();

        if (requestingUserId.equals(user.getId())) {
            return user.getModules();
        } else if (user.getVisibility() == Visibility.PUBLIC) {
            return user.getModules().stream()
                    .filter(module -> module.getVisibility() == Visibility.PUBLIC)
                    .toList();
        } else throw new InvalidRequestException("You do not have permission to access modules for this user.");
    }

    @Override
    public List<CardSet> getLastQuizzedCardSets() throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();
        Optional<User> userOpt = getUserById(requestingUserId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<Long, Date> lastQuizzedIdToDate = user.getLastQuizzedCardSets();

            List<CardSet> unorderedCardSets =
                    cardSetRepository.findAllById(lastQuizzedIdToDate.keySet())
                            .stream()
                            .filter(cardSet -> isPermittedToAccessCardSet(requestingUserId, cardSet))
                            .toList();

            return unorderedCardSets.stream()
                    .sorted(Comparator.comparing((CardSet cardSet) -> lastQuizzedIdToDate.get(cardSet.getId())).reversed())
                    .collect(Collectors.toList());
        } else {
            throw new InvalidRequestException("No permission");
        }
    }

    @Override
    public Optional<User> addQuizzedCardSetToUser(Long cardSetId) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();
        Optional<User> userOpt = userRepository.findById(requestingUserId);
        if (userOpt.isEmpty()) {
            throw new InvalidRequestException("User not found.");
        }
        User user = userOpt.get();
        Optional<CardSet> cardSetOpt = cardSetRepository.findById(cardSetId);
        if (cardSetOpt.isEmpty()) {
            throw new InvalidRequestException("CardSet does not exist.");
        }

        CardSet cardSet = cardSetOpt.get();
        if(!isPermittedToAccessCardSet(user.getId(), cardSet)){
            throw new InvalidRequestException("No Permission.");
        }

        user.addLastQuizzed(cardSet.getId());
        //wenn > 5, lösche das älteste CardSet aus der Schnellzugriffsliste
        if (user.getLastQuizzedCardSets().size() > 5) {
            Long oldestEntryId = Collections.min(user.getLastQuizzedCardSets().entrySet(),
                            Comparator.comparing(Map.Entry::getValue))
                    .getKey();
            user.removeLastQuizzed(oldestEntryId);
        }
        User savedUser = userRepository.save(user);
        return Optional.of(savedUser);

    }


    @Override
    public User createUser(User user) throws InvalidRequestException {
        if (userIsIncomplete(user)) {
            throw new InvalidRequestException("One or more properties are not set in the request.");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> updateUser(Long userId, User user) throws InvalidRequestException {
        Long userIdFromUserObject;

        if (user == null || user.getId() == null) {
            throw new InvalidRequestException("No 'id' is present in the User object or given user is null.");
        }

        userIdFromUserObject = user.getId();

        if (!userIdFromUserObject.equals(userId)) {
            throw new InvalidRequestException("The userId in the path variable does not match the id in the request body.");
        }

        Long requestingUserId = jwtService.extractIdFromToken();

        if (!userIdFromUserObject.equals(requestingUserId)) {
            throw new InvalidRequestException("You do not have permission to create this module.");
        }

        if (userIsIncomplete(user)) {
            throw new InvalidRequestException("The update request is incomplete.");
        }

        return userRepository.findById(userId)
                .map(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setVisibility(user.getVisibility());
                    existingUser.setRole(user.getRole());
                    existingUser.setDescription(user.getDescription());

                    User savedUser = userRepository.save(existingUser);
                    return Optional.of(savedUser);
                })
                .orElseThrow(() -> new InvalidRequestException("User with ID " + userId + " does not exist."));
    }

    @Override
    public boolean deleteUser(Long userId) throws InvalidRequestException {
        Long requestingUserId = jwtService.extractIdFromToken();

        if (!userId.equals(requestingUserId)) {
            throw new InvalidRequestException("You do not have permission to delete this user.");
        }

        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }
    //TODO: bei den delete Methoden kann return false weg, nochmal absprechen

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }
    //TODO: Hier nochmal genauer schauen, getPrincipal etc.

    private boolean userIsIncomplete(User user) {
        return user.getName() == null ||
                user.getEmail() == null ||
                user.getVisibility() == null ||
                user.getRole() == null ||
                user.getLastQuizzedCardSets() == null ||
                user.getDescription() == null;
    }

    private boolean isPermittedToAccessCardSet(Long requestingUserId, CardSet cardSet) {
        if(requestingUserId == null || cardSet == null) return false;
        boolean isOwner = cardSet.getModule().getOwners()
                .stream()
                .anyMatch(owner -> owner.getId().equals(requestingUserId));
        boolean isPublic = cardSet.getVisibility() == Visibility.PUBLIC;
        return isOwner || isPublic;
    }
}
