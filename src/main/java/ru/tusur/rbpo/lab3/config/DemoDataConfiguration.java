package ru.tusur.rbpo.lab3.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tusur.rbpo.lab3.model.*;
import ru.tusur.rbpo.lab3.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DemoDataConfiguration {
    @Bean
    CommandLineRunner seedData(AppUserRepository users, EquipmentUnitRepository equipment,
                               RentalOrderRepository orders, RentalItemRepository items,
                               InspectionRecordRepository inspections) {
        return args -> {
            if (users.count() != 0) return;

            AppUser customer = users.save(new AppUser("Иван Петров", "ivan.petrov@example.com"));
            EquipmentUnit tent = equipment.save(new EquipmentUnit("TENT-001", "Палатка", new BigDecimal("1200.00"), EquipmentCondition.AVAILABLE));
            equipment.save(new EquipmentUnit("BAG-001", "Спальный мешок", new BigDecimal("700.00"), EquipmentCondition.AVAILABLE));
            equipment.save(new EquipmentUnit("KAYAK-001", "Байдарка", new BigDecimal("2500.00"), EquipmentCondition.MAINTENANCE));

            RentalOrder completed = orders.save(new RentalOrder(customer, LocalDate.now().minusDays(4), LocalDate.now().minusDays(2), OrderStatus.COMPLETED));
            RentalItem completedItem = items.save(new RentalItem(completed, tent));
            completedItem.setReturnStatus(ReturnStatus.RETURNED);
            items.save(completedItem);
            inspections.save(new InspectionRecord(completedItem, InspectionType.ISSUE, InspectionCondition.GOOD));
            inspections.save(new InspectionRecord(completedItem, InspectionType.RETURN, InspectionCondition.GOOD));
            completed.setTotalAmount(new BigDecimal("2400.00"));
            orders.save(completed);

            orders.save(new RentalOrder(customer, LocalDate.now().plusDays(2), LocalDate.now().plusDays(5), OrderStatus.DRAFT));
        };
    }
}

