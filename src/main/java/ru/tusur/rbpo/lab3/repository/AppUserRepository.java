package ru.tusur.rbpo.lab3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tusur.rbpo.lab3.model.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> { }

