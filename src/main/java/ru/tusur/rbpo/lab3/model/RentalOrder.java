package ru.tusur.rbpo.lab3.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rental_orders")
public class RentalOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.EAGER) @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private OrderStatus status;
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @JsonIgnore
    @OneToMany(mappedBy = "rentalOrder")
    private List<RentalItem> items = new ArrayList<>();

    protected RentalOrder() { }
    public RentalOrder(AppUser user, LocalDate startDate, LocalDate endDate, OrderStatus status) {
        this.user = user; this.startDate = startDate; this.endDate = endDate; this.status = status; this.createdAt = Instant.now();
    }
    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setUser(AppUser user) { this.user = user; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}

