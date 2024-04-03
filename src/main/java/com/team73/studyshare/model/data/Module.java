package com.team73.studyshare.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team73.studyshare.model.Visibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Represents a module, which is a collection of card sets, directories, and users.
 */
@Entity
@Table(name = "module")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    /**
     * The unique identifier for the module.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the module.
     */
    @Column(name = "name")
    private String name;

    /**
     * The description of the module.
     */
    @Column(name = "description")
    private String description;

    /**
     * The visibility of the module (e.g., "Private" or "Public").
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;

    /**
     * The timestamp when the module was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The score associated with the module.
     */
    @Column(name = "score")
    private Integer score;

    /**
     * The count of card sets in this module.
     */
    @Column(name = "card_set_count")
    private int cardSetCount;

    /**
     * The count of files in this module.
     */
    @Column(name = "file_count")
    private int fileCount;

    /**
     * The user who created this module.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    /**
     * The original module from which this module was derived.
     */
    @OneToOne
    @JoinColumn(name = "origin_module_id")
    private Module originModule;

    /**
     * The root directory of this module.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "root_directory_id")
    private Directory rootDirectory;

    /**
     * The list of card sets in this module.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "module", cascade = CascadeType.ALL)
    @JsonIgnore
    @Builder.Default
    private List<CardSet> cardSets = new ArrayList<>();

    /**
     * The list of users who own or are associated with this module.
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.REFRESH})
    @JoinTable(
            name = "module_user",
            joinColumns = @JoinColumn(name = "module_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> owners = new ArrayList<>();

    /**
     * Adds a card set to the module and sets its reference to this module.
     *
     * @param tempCardSet The card set to add to the module.
     */
    public void addCardSet(CardSet tempCardSet) {
        cardSets.add(tempCardSet);
        tempCardSet.setModule(this);
        cardSetCount=cardSets.size();
    }

    /**
     * deletes a card set from the module.
     *
     * @param tempCardSet The card set to remove from the module.
     */
    public void removeCardSet(CardSet tempCardSet) {
        cardSets.remove(tempCardSet);
        tempCardSet.setModule(null);
        cardSetCount=cardSets.size();
    }

    /**
     * Adds a user as an owner or associate of this module.
     *
     * @param tempUser The user to add as an owner or associate.
     */
    public void addOwner(User tempUser) {
        owners.add(tempUser);
    }



    public void incrementFileCount() {
        fileCount++;
    }

    public void decrementFileCount() {
        if (fileCount > 0) {
            fileCount--;
        } else {
            System.out.println("File count is already at 0.");
        }
    }
}

//TODO: Validierung
