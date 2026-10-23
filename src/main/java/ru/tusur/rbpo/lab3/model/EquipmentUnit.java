package ru.tusur.rbpo.lab3.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "equipment_units", uniqueConstraints = @UniqueConstraint(name = "uk_equipment_inventory", columnNames = "inventory_number"))
public class EquipmentUnit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "inventory_number", nullable = false, length = 60)
    private String inventoryNumber;
    @Column(nullable = false, length = 100)
    private String type;
    @Column(name = "daily_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal dailyPrice;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private EquipmentCondition condition;
    @JsonIgnore
    @OneToMany(mappedBy = "equipmentUnit")
    private List<RentalItem> rentalItems = new ArrayList<>();

    protected EquipmentUnit() { }
    public EquipmentUnit(String inventoryNumber, String type, BigDecimal dailyPrice, EquipmentCondition condition) {
        this.inventoryNumber = inventoryNumber; this.type = type; this.dailyPrice = dailyPrice; this.condition = condition;
    }
    public Long getId() { return id; }
    public String getInventoryNumber() { return inventoryNumber; }
    public String getType() { return type; }
    public BigDecimal getDailyPrice() { return dailyPrice; }
    public EquipmentCondition getCondition() { return condition; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }
    public void setType(String type) { this.type = type; }
    public void setDailyPrice(BigDecimal dailyPrice) { this.dailyPrice = dailyPrice; }
    public void setCondition(EquipmentCondition condition) { this.condition = condition; }
}

