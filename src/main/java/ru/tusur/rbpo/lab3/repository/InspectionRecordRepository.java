package ru.tusur.rbpo.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.rbpo.lab3.model.InspectionRecord;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Long> { }

