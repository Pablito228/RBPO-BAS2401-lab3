package ru.tusur.rbpo.lab3.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tusur.rbpo.lab3.dto.InspectionRecordRequest;
import ru.tusur.rbpo.lab3.model.InspectionRecord;
import ru.tusur.rbpo.lab3.service.RentalService;
import java.util.List;

@RestController
@RequestMapping("/api/inspection-records")
public class InspectionRecordController {
    private final RentalService service;
    public InspectionRecordController(RentalService service) { this.service = service; }
    @GetMapping public List<InspectionRecord> all() { return service.inspections(); }
    @GetMapping("/{id}") public InspectionRecord one(@PathVariable Long id) { return service.inspection(id); }
    @PostMapping public ResponseEntity<InspectionRecord> create(@RequestBody InspectionRecordRequest request) { return ResponseEntity.status(HttpStatus.CREATED).body(service.createInspection(request)); }
    @PatchMapping("/{id}") public InspectionRecord update(@PathVariable Long id, @RequestBody InspectionRecordRequest request) { return service.updateInspection(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.deleteInspection(id); }
}

