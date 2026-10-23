package ru.tusur.rbpo.lab3.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "inspection_records")
public class InspectionRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "rental_item_id", nullable = false)
    private RentalItem rentalItem;
    @Enumerated(EnumType.STRING) @Column(name = "inspection_type", nullable = false, length = 20)
    private InspectionType inspectionType;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private InspectionCondition condition;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected InspectionRecord() { }
    public InspectionRecord(RentalItem rentalItem, InspectionType inspectionType, InspectionCondition condition) {
        this.rentalItem = rentalItem; this.inspectionType = inspectionType; this.condition = condition; this.createdAt = Instant.now();
    }
    public Long getId() { return id; }
    public RentalItem getRentalItem() { return rentalItem; }
    public InspectionType getInspectionType() { return inspectionType; }
    public InspectionCondition getCondition() { return condition; }
    public Instant getCreatedAt() { return createdAt; }
    public void setRentalItem(RentalItem rentalItem) { this.rentalItem = rentalItem; }
    public void setInspectionType(InspectionType inspectionType) { this.inspectionType = inspectionType; }
    public void setCondition(InspectionCondition condition) { this.condition = condition; }
}

