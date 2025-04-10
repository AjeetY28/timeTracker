package com.timetracker.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "scheduled_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType;

    @Column(name = "parameters", columnDefinition = "json", nullable = false)
    private String parameters;  // Stored as JSON string

    @Column(name = "recipients", columnDefinition = "TEXT", nullable = false)
    private String recipients;  // Comma-separated email addresses

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "schedule_day")
    private Integer scheduleDay;  // Day of week (1-7) or day of month (1-31)

    @Column(name = "schedule_time", nullable = false)
    private LocalTime scheduleTime;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status = ReportStatus.ACTIVE;

    public enum ScheduleType {
        DAILY,
        WEEKLY,
        MONTHLY
    }

    public enum ReportStatus {
        ACTIVE,
        PAUSED
    }

    // Convenience methods for parameters handling
    @SuppressWarnings("unchecked")
    public Map<String, Object> getParametersAsMap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(parameters, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid JSON in parameters", e);
        }
    }

    public void setParametersFromMap(Map<String, Object> parametersMap) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.parameters = mapper.writeValueAsString(parametersMap);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize parameters to JSON", e);
        }
    }

    // Convenience methods for recipients handling
    public List<String> getRecipientsList() {
        return Arrays.asList(recipients.split(","));
    }

    public void setRecipientsFromList(List<String> recipientsList) {
        this.recipients = String.join(",", recipientsList);
    }

    @PrePersist
    @PreUpdate
    private void validateSchedule() {
        // Validate schedule_day based on schedule_type
        if (scheduleType == ScheduleType.WEEKLY && (scheduleDay == null || scheduleDay < 1 || scheduleDay > 7)) {
            throw new IllegalArgumentException("Weekly schedule requires a valid day of week (1-7)");
        }
        if (scheduleType == ScheduleType.MONTHLY && (scheduleDay == null || scheduleDay < 1 || scheduleDay > 31)) {
            throw new IllegalArgumentException("Monthly schedule requires a valid day of month (1-31)");
        }
        if (scheduleType == ScheduleType.DAILY && scheduleDay != null) {
            scheduleDay = null; // Clear schedule_day for daily schedules
        }

        // Validate parameters JSON
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.readValue(parameters, Map.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON in parameters field");
        }

        // Validate email addresses
        List<String> emailList = getRecipientsList();
        for (String email : emailList) {
            if (!isValidEmail(email.trim())) {
                throw new IllegalArgumentException("Invalid email address: " + email);
            }
        }
    }

    private boolean isValidEmail(String email) {
        // Basic email validation - you might want to use a more sophisticated validation
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public boolean isReadyToRun(LocalDateTime currentTime) {
        if (status != ReportStatus.ACTIVE) {
            return false;
        }

        if (lastRunAt == null) {
            return true;
        }

        LocalDateTime nextRunTime = calculateNextRunTime();
        return currentTime.isAfter(nextRunTime) || currentTime.isEqual(nextRunTime);
    }

    private LocalDateTime calculateNextRunTime() {
        if (lastRunAt == null) {
            return null;
        }

        LocalDateTime nextRun = lastRunAt.with(scheduleTime);

        switch (scheduleType) {
            case DAILY:
                nextRun = nextRun.plusDays(1);
                break;
            case WEEKLY:
                nextRun = nextRun.plusWeeks(1);
                break;
            case MONTHLY:
                nextRun = nextRun.plusMonths(1);
                break;
        }

        return nextRun;
    }
}