package com.fawwaz.transactionmonitor.domain;

import com.fawwaz.transactionmonitor.domain.enums.AlertStatus;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * One immutable audit record per alert status change: what it changed from/to,
 * the note the analyst left, who did it, and when. Rows are never updated.
 */
@Entity
@Table(name = "alert_events")
public class AlertEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alert_id")
    private Alert alert;

    /** Null when the alert was first created. */
    @Enumerated(EnumType.STRING)
    private AlertStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus toStatus;

    @Column(length = 2000)
    private String note;

    @Column(nullable = false)
    private String actor;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public AlertEvent() {}

    public AlertEvent(Alert alert, AlertStatus fromStatus, AlertStatus toStatus, String note, String actor) {
        this.alert = alert;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
        this.note = note;
        this.actor = actor;
    }

    public Long getId() { return id; }
    public Alert getAlert() { return alert; }
    public AlertStatus getFromStatus() { return fromStatus; }
    public AlertStatus getToStatus() { return toStatus; }
    public String getNote() { return note; }
    public String getActor() { return actor; }
    public Instant getCreatedAt() { return createdAt; }
}
