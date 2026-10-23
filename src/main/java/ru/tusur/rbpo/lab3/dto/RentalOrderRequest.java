package ru.tusur.rbpo.lab3.dto;

import ru.tusur.rbpo.lab3.model.OrderStatus;
import java.time.LocalDate;

public record RentalOrderRequest(Long userId, LocalDate startDate, LocalDate endDate, OrderStatus status) { }

