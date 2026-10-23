package ru.tusur.rbpo.lab3.dto;

import ru.tusur.rbpo.lab3.model.ReturnStatus;

public record RentalItemRequest(Long rentalOrderId, Long equipmentUnitId, ReturnStatus returnStatus) { }

