package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.Directory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * A repository interface for managing and accessing Directory entities in the database.
 * Provides basic CRUD (Create, Read, Update, Delete) operations for Directory objects.
 *
 * This interface extends JpaRepository, which offers various data access methods out of the box.
 *
 * @see JpaRepository
 */
public interface DirectoryRepository extends JpaRepository<Directory, Long> {
}