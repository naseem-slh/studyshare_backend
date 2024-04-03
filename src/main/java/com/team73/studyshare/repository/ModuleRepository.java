package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * A repository interface for managing and accessing Module entities in the database.
 * Provides basic CRUD (Create, Read, Update, Delete) operations for Module objects.
 *
 * This interface extends JpaRepository, offering various data access methods out of the box.
 *
 * @see JpaRepository
 */
public interface ModuleRepository extends JpaRepository<Module, Long> {
    @Query("SELECT m FROM Module m WHERE LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) AND m.visibility = com.team73.studyshare.model.Visibility.PUBLIC")
    List<Module> searchModules(@Param("query") String query);

    @Query("SELECT m FROM Module m WHERE m.rootDirectory.id = :rootDirectoryId")
    Optional<Module> findModuleByRootDirectoryId(@Param("rootDirectoryId") Long rootDirectoryId);
}