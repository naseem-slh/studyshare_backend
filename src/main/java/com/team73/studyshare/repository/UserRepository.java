package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * A repository interface for managing and accessing User entities in the database.
 * Provides basic CRUD (Create, Read, Update, Delete) operations for User objects.
 *
 * This interface extends JpaRepository, offering various data access methods out of the box.
 *
 * @see JpaRepository
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Searches for a user by their email address and returns them (if found) as an Optional.
     *
     * @param email The email address of the user to be searched for.
     * @return An Optional containing the found user (if available) or an empty Optional.
     */
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :query, '%')) AND u.visibility = com.team73.studyshare.model.Visibility.PUBLIC")
    List<User> searchUsers(@Param("query") String query);
}
