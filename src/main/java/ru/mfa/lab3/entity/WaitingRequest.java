package ru.mfa.lab3.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "waiting_requests")
public class WaitingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "edition_id", nullable = false)
    private BookEdition edition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WaitingStatus status;

    /** Заполнено только в статусе READY: экземпляр, закреплённый за читателем. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_copy_id")
    private Copy reservedCopy;

    protected WaitingRequest() {}

    public WaitingRequest(BookEdition edition, User user, LocalDateTime requestDate, WaitingStatus status) {
        this.edition = edition;
        this.user = user;
        this.requestDate = requestDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public BookEdition getEdition() { return edition; }
    public User getUser() { return user; }
    public LocalDateTime getRequestDate() { return requestDate; }
    public WaitingStatus getStatus() { return status; }
    public void setStatus(WaitingStatus status) { this.status = status; }
    public Copy getReservedCopy() { return reservedCopy; }
    public void setReservedCopy(Copy reservedCopy) { this.reservedCopy = reservedCopy; }
}
