package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * A repository interface for managing and accessing File entities in the database.
 * Provides basic CRUD (Create, Read, Update, Delete) operations for File objects.
 *
 * This interface extends JpaRepository, offering various data access methods out of the box.
 *
 * @see JpaRepository
 */
public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT f FROM File f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) AND f.visibility = com.team73.studyshare.model.Visibility.PUBLIC")
    List<File> searchFiles(@Param("query") String query);

    /**
     * Finds all files associated with a given module ID.
     *
     * @param moduleId The ID of the module to find files for.
     * @return A list of files associated with the specified module.
     */
    List<File> findByModuleId(Long moduleId);
}
