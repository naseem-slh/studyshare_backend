package com.team73.studyshare.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team73.studyshare.model.FileType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {
    /**
     * The unique identifier for the module.
     */
    @Id
    @GeneratedValue
    private Long id;

    @Lob
    @Column(name = "data", length = 10485760) // 10 MB in Bytes
    @JsonIgnore
    private byte[] data;

    /**
     * The type of the file (e.g., "PDF," "JPG," "PNG").
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FileType type;
}
