package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * A repository interface for managing and accessing Card entities in the database.
 * Provides basic CRUD (Create, Read, Update, Delete) operations for Card objects.
 *
 * This interface extends JpaRepository, providing various data access methods out of the box.
 *
 * @see JpaRepository
 */
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c FROM Card c WHERE c.cardSet.id = :card_set_id")
    List<Card> findCardSetCards(@Param("card_set_id") Long card_set_id);
}
