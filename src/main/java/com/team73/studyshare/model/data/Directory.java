package com.team73.studyshare.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team73.studyshare.model.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a directory that contains files and is associated with a module.
 */
@Entity
@Table(name = "directory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Directory {

    /**
     * The unique identifier for the directory.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the directory.
     */
    @Column(name = "name")
    private String name;

    /**
     * The visibility of the directory (e.g., "Private" or "Public").
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;

    /**
     * The timestamp when the directory was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The user who created this directory.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    /**
     * The main directory to which this directory belongs.
     */
    @ManyToOne
    @JoinColumn(name = "main_directory_id")
    private Directory mainDirectory;

    /**
     * The list of files contained in this directory.
     */
    @OneToMany(mappedBy = "directory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<File> files = new ArrayList<>();

    /**
     * The list of directories contained in this directory.
     */
    @OneToMany(mappedBy = "mainDirectory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @Builder.Default
    private List<Directory> subDirectories = new ArrayList<>();

    /**
     * Adds a file to the directory and sets its reference to this directory.
     *
     * @param tempFile The file to add to the directory.
     */
    public void addFiles(File tempFile) {
        files.add(tempFile);
        tempFile.setDirectory(this);
    }
}
