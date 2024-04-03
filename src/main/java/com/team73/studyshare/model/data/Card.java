package com.team73.studyshare.model.data;

import com.team73.studyshare.model.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a card in a card set.
 */
@Entity
@Table(name = "card")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    /**
     * The unique identifier for the card.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    /**
     * The question associated with this card.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private QuizField question;

    /**
     * The answer associated with this card.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private QuizField answer;

    @ElementCollection
    @CollectionTable(name = "wrong_answers", joinColumns = @JoinColumn(name = "card_id"))
    @Column(name = "wrong_answer", length = 3000)
    private List<String> wrongAnswers = new ArrayList<>();

    /**
     * The status of the card (e.g., "Good," "Bad," "Undone").
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    /**
     * Indicates whether the card is flagged.
     */
    @Column(name = "flagged")
    private Boolean flagged;

    /**
     * The user who created this Card.
     */
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    /**
     * The card set to which this card belongs.{CascadeType.PERSIST, CascadeType.REMOVE}
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "card_set_id")
    private CardSet cardSet;

}

