package com.team73.studyshare.repository;

import com.team73.studyshare.model.data.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}