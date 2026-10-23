package ru.tusur.rbpo.lab3.dto;

import ru.tusur.rbpo.lab3.model.EquipmentCondition;
import java.math.BigDecimal;

public record EquipmentUnitRequest(String inventoryNumber, String type, BigDecimal dailyPrice,
                                   EquipmentCondition condition) { }

