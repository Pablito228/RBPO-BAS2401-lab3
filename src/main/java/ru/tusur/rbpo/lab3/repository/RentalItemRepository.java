package ru.tusur.rbpo.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.rbpo.lab3.model.RentalItem;
import java.util.List;

public interface RentalItemRepository extends JpaRepository<RentalItem, Long> {
    List<RentalItem> findByRentalOrderId(Long rentalOrderId);
    List<RentalItem> findByEquipmentUnitId(Long equipmentUnitId);
}

