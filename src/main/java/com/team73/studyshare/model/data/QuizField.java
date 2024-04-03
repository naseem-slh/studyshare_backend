package com.team73.studyshare.model.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Represents a quiz field, which can contain text and image data.
 */
@Entity
@Table(name = "quiz_field")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizField {

    /**
     * The unique identifier for the quiz field.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /**
     * The text content of the quiz field.
     */
    @Column(name = "text")
    private String text;

    /**
     * Identifier for the binary data of the file.
     */
    @Column(name = "documentId")
    private Long documentId;

}
