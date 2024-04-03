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
 * Represents a card set containing a collection of cards.
 */
@Entity
@Table(name = "card_set")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSet {

    /**
     * The unique identifier for the card set.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The name of the card set.
     */
    @Column(name = "name")
    private String name;

    /**
     * The visibility of the card set (e.g., "Private" or "Public").
     */
    @Column(name = "visibility")
    private Visibility visibility;

    /**
     * The timestamp when the card set was created.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    /**
     * The score associated with the card set.
     */
    @Column(name = "score")
    private Integer score;

    /**
     * The module to which this card set belongs.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private Module module;

    /**
     * The user who created this card set.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    /**
     * The original cardset from which this cardset was derived.
     */
    @JoinColumn(name = "origin_cardset_id")
    private Long originCardSetId;

    /**
     * The list of cards in this card set.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "cardSet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Card> cards = new ArrayList<>();

    /**
     * The amount of cards within the card set.
     */
    @Column(name = "card_count")
    private Integer cardCount;

    /**
     * Adds a card to the card set and sets its reference to this card set.
     *
     * @param tempCard The card to add to the card set.
     */
    public void addCard(Card tempCard) {
        cards.add(tempCard);
        cardCount++;
    }
}

