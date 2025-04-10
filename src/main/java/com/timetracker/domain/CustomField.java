package com.timetracker.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Entity
@Table(name = "custom_fields", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_id", "entity_type", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomField extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private EntityType entityType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "label", nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FieldType type;

    @Column(name = "required")
    private Boolean required = false;

    @Column(name = "options", columnDefinition = "TEXT")
    private String options;  // Stores JSON array of options

    @Column(name = "default_value", columnDefinition = "TEXT")
    private String defaultValue;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    public enum EntityType {
        TIME_ENTRY,
        PROJECT,
        TASK,
        USER,
        CLIENT,
        INVOICE
    }

    public enum FieldType {
        TEXT,
        NUMBER,
        DATE,
        DROPDOWN,
        CHECKBOX
    }

    @PrePersist
    @PreUpdate
    private void validateOptions() {
        if (type == FieldType.DROPDOWN && options != null) {
            try {
                // Validate that options is a valid JSON array
                ObjectMapper mapper = new ObjectMapper();
                mapper.readValue(options, List.class);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Options must be a valid JSON array for dropdown fields");
            }
        }
    }
}