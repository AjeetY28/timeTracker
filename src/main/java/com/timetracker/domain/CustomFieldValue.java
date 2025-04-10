package com.timetracker.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "custom_field_values", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"custom_field_id", "entity_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomFieldValue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id", nullable = false)
    private CustomField customField;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;

    @PrePersist
    @PreUpdate
    private void validateValue() {
        if (value != null && customField != null) {
            switch (customField.getType()) {
                case NUMBER:
                    try {
                        new BigDecimal(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Value must be a valid number for NUMBER type fields");
                    }
                    break;

                case DATE:
                    try {
                        LocalDate.parse(value);
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Value must be a valid date for DATE type fields");
                    }
                    break;

                case DROPDOWN:
                    if (customField.getOptions() != null) {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            List<String> options = mapper.readValue(customField.getOptions(), List.class);
                            if (!options.contains(value)) {
                                throw new IllegalArgumentException("Value must be one of the defined options for DROPDOWN type fields");
                            }
                        } catch (JsonProcessingException e) {
                            throw new IllegalStateException("Invalid options format in custom field");
                        }
                    }
                    break;

                case CHECKBOX:
                    if (!value.equals("true") && !value.equals("false")) {
                        throw new IllegalArgumentException("Value must be 'true' or 'false' for CHECKBOX type fields");
                    }
                    break;
            }
        }

        if (customField.getRequired() && (value == null || value.trim().isEmpty())) {
            throw new IllegalArgumentException("Value is required for this custom field");
        }
    }
}
