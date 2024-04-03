package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.QuizField;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository interface for managing and accessing QuizField entities in the database.
 * Provides basic CRUD (Create, Read, Update, Delete) operations for QuizField objects.
 *
 * This interface extends JpaRepository, offering various data access methods out of the box.
 *
 * @see JpaRepository
 */
public interface QuizFieldRepository extends JpaRepository<QuizField, Long> {
}

