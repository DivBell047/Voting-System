// Candidate.java
package com.example.candidate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name", length = 100, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String name;

    @Column(name = "party", length = 100, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String party;

    @Column(name = "position", length = 100, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String position;

    @Column(name = "description", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String description;

    @Column(name = "image_url", columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String imageUrl;

    @Column(name = "created_at")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
    }
}