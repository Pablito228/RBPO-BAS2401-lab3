package ru.tusur.rbpo.lab3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tusur.rbpo.lab3.dto.RentalItemRequest;
import ru.tusur.rbpo.lab3.dto.ReturnItemRequest;
import ru.tusur.rbpo.lab3.model.RentalItem;
import ru.tusur.rbpo.lab3.service.RentalService;
import java.util.List;

@RestController
@RequestMapping("/api/rental-items")
public class RentalItemController {
    private final RentalService service;
    public RentalItemController(RentalService service) { this.service = service; }
    @GetMapping public List<RentalItem> all() { return service.items(); }
    @GetMapping("/{id}") public RentalItem one(@PathVariable Long id) { return service.item(id); }
    @PostMapping public ResponseEntity<RentalItem> create(@RequestBody RentalItemRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.createItem(request)); }
    @PatchMapping("/{id}") public RentalItem update(@PathVariable Long id, @RequestBody RentalItemRequest request) { return service.updateItem(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.deleteItem(id); }
    @PostMapping("/{id}/return") public RentalItem returnItem(@PathVariable Long id, @RequestBody ReturnItemRequest request) { return service.returnItem(id, request); }
}

