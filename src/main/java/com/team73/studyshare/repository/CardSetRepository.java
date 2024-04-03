package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.CardSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * A repository interface for managing and accessing CardSet entities in the database.
 * Provides basic CRUD (Create, Read, Update, Delete) operations for CardSet objects.
 *
 * This interface extends JpaRepository, offering various data access methods out of the box.
 *
 * @see JpaRepository
 */
public interface CardSetRepository extends JpaRepository<CardSet, Long> {
    @Query("SELECT cs FROM CardSet cs WHERE LOWER(cs.name) LIKE LOWER(CONCAT('%', :query, '%')) AND cs.visibility = com.team73.studyshare.model.Visibility.PUBLIC")
    List<CardSet> searchCardSets(@Param("query") String query);
}