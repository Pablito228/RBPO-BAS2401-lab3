package ru.tusur.rbpo.lab3.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rental_items", uniqueConstraints = @UniqueConstraint(name = "uk_order_equipment", columnNames = {"rental_order_id", "equipment_unit_id"}))
public class RentalItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "rental_order_id", nullable = false)
    private RentalOrder rentalOrder;
    @ManyToOne(optional = false) @JoinColumn(name = "equipment_unit_id", nullable = false)
    private EquipmentUnit equipmentUnit;
    @Column(name = "fixed_daily_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal fixedDailyPrice;
    @Enumerated(EnumType.STRING) @Column(name = "return_status", nullable = false, length = 20)
    private ReturnStatus returnStatus = ReturnStatus.NOT_RETURNED;
    @JsonIgnore
    @OneToMany(mappedBy = "rentalItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InspectionRecord> inspections = new ArrayList<>();

    protected RentalItem() { }
    public RentalItem(RentalOrder rentalOrder, EquipmentUnit equipmentUnit) {
        this.rentalOrder = rentalOrder; this.equipmentUnit = equipmentUnit; this.fixedDailyPrice = equipmentUnit.getDailyPrice();
    }
    public Long getId() { return id; }
    public RentalOrder getRentalOrder() { return rentalOrder; }
    public EquipmentUnit getEquipmentUnit() { return equipmentUnit; }
    public BigDecimal getFixedDailyPrice() { return fixedDailyPrice; }
    public ReturnStatus getReturnStatus() { return returnStatus; }
    public void setRentalOrder(RentalOrder rentalOrder) { this.rentalOrder = rentalOrder; }
    public void setEquipmentUnit(EquipmentUnit equipmentUnit) { this.equipmentUnit = equipmentUnit; this.fixedDailyPrice = equipmentUnit.getDailyPrice(); }
    public void setReturnStatus(ReturnStatus returnStatus) { this.returnStatus = returnStatus; }
}

