package ru.tusur.rbpo.lab3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tusur.rbpo.lab3.dto.UserRequest;
import ru.tusur.rbpo.lab3.model.AppUser;
import ru.tusur.rbpo.lab3.service.RentalService;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final RentalService service;
    public UserController(RentalService service) { this.service = service; }
    @GetMapping public List<AppUser> all() { return service.users(); }
    @GetMapping("/{id}") public AppUser one(@PathVariable Long id) { return service.user(id); }
    @PostMapping public ResponseEntity<AppUser> create(@RequestBody UserRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(request)); }
    @PatchMapping("/{id}") public AppUser update(@PathVariable Long id, @RequestBody UserRequest request) { return service.updateUser(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.deleteUser(id); }
}

