package com.team73.studyshare.model.data;

import com.team73.studyshare.model.FileType;
import com.team73.studyshare.model.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Represents a file in a directory.
 */
@Entity
@Table(name = "file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    /**
     * The unique identifier for the file.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the file.
     */
    @Column(name = "name")
    private String name;

    /**
     * The visibility of the file (e.g., "Private" or "Public").
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;

    /**
     * The module to which this file belongs.
     */
    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    /**
     * The user who created this File.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    /**
     * The timestamp when the file was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * Identifier for the binary data of the file.
     */
    @Column(name = "documentId")
    private Long documentId;

    /**
     * The directory to which this file belongs.
     */
    @ManyToOne
    @JoinColumn(name = "directory_id")
    private Directory directory;

    /**
     * The type of the file (e.g., "PDF," "JPG," "PNG").
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FileType type;
}
