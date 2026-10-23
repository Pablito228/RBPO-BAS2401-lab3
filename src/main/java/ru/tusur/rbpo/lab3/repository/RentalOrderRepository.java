package ru.tusur.rbpo.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.rbpo.lab3.model.RentalOrder;

public interface RentalOrderRepository extends JpaRepository<RentalOrder, Long> { }

