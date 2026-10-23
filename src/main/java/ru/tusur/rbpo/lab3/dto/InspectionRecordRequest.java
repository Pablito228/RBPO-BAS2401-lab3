package ru.tusur.rbpo.lab3.dto;

import ru.tusur.rbpo.lab3.model.InspectionCondition;
import ru.tusur.rbpo.lab3.model.InspectionType;

public record InspectionRecordRequest(Long rentalItemId, InspectionType inspectionType,
                                      InspectionCondition condition) { }

