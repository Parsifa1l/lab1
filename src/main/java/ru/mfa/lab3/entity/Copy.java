package ru.mfa.lab3.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "copies")
public class Copy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "edition_id", nullable = false)
    private BookEdition edition;

    @Column(name = "inventory_number", nullable = false, unique = true)
    private String inventoryNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "book_condition", nullable = false)
    private CopyCondition condition;

    protected Copy() {}

    public Copy(BookEdition edition, String inventoryNumber, CopyCondition condition) {
        this.edition = edition;
        this.inventoryNumber = inventoryNumber;
        this.condition = condition;
    }

    public Long getId() { return id; }
    public BookEdition getEdition() { return edition; }
    public void setEdition(BookEdition edition) { this.edition = edition; }
    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }
    public CopyCondition getCondition() { return condition; }
    public void setCondition(CopyCondition condition) { this.condition = condition; }
}
