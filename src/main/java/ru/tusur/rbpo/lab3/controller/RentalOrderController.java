package ru.tusur.rbpo.lab3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tusur.rbpo.lab3.dto.AddItemRequest;
import ru.tusur.rbpo.lab3.dto.RentalOrderRequest;
import ru.tusur.rbpo.lab3.model.RentalItem;
import ru.tusur.rbpo.lab3.model.RentalOrder;
import ru.tusur.rbpo.lab3.service.RentalService;
import java.util.List;

@RestController
@RequestMapping("/api/rental-orders")
public class RentalOrderController {
    private final RentalService service;
    public RentalOrderController(RentalService service) { this.service = service; }
    @GetMapping public List<RentalOrder> all() { return service.orders(); }
    @GetMapping("/{id}") public RentalOrder one(@PathVariable Long id) { return service.order(id); }
    @PostMapping public ResponseEntity<RentalOrder> create(@RequestBody RentalOrderRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.createOrder(request)); }
    @PatchMapping("/{id}") public RentalOrder update(@PathVariable Long id, @RequestBody RentalOrderRequest request) { return service.updateOrder(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.deleteOrder(id); }
    @PostMapping("/{id}/items") public ResponseEntity<RentalItem> addItem(@PathVariable Long id, @RequestBody AddItemRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.addItemToDraftOrder(id, request)); }
    @PostMapping("/{id}/issue") public RentalOrder issue(@PathVariable Long id) { return service.issueOrder(id); }
    @PostMapping("/{id}/cancel") public RentalOrder cancel(@PathVariable Long id) { return service.cancelOrder(id); }
}

