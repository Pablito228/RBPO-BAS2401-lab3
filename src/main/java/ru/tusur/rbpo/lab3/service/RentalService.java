package ru.tusur.rbpo.lab3.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tusur.rbpo.lab3.dto.*;
import ru.tusur.rbpo.lab3.exception.NotFoundException;
import ru.tusur.rbpo.lab3.model.*;
import ru.tusur.rbpo.lab3.repository.*;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {
    private final AppUserRepository users;
    private final EquipmentUnitRepository equipment;
    private final RentalOrderRepository orders;
    private final RentalItemRepository items;
    private final InspectionRecordRepository inspections;

    public RentalService(AppUserRepository users, EquipmentUnitRepository equipment,
                         RentalOrderRepository orders, RentalItemRepository items,
                         InspectionRecordRepository inspections) {
        this.users = users;
        this.equipment = equipment;
        this.orders = orders;
        this.items = items;
        this.inspections = inspections;
    }

    @Transactional(readOnly = true)
    public List<AppUser> users() { return users.findAll(); }
    @Transactional(readOnly = true)
    public AppUser user(Long id) { return users.findById(id).orElseThrow(() -> missing("User", id)); }
    @Transactional
    public AppUser createUser(UserRequest request) {
        required(request.name(), "name"); required(request.email(), "email");
        return users.save(new AppUser(request.name(), request.email()));
    }
    @Transactional
    public AppUser updateUser(Long id, UserRequest request) {
        AppUser user = user(id);
        if (request.name() != null) { required(request.name(), "name"); user.setName(request.name()); }
        if (request.email() != null) { required(request.email(), "email"); user.setEmail(request.email()); }
        return users.save(user);
    }
    @Transactional
    public void deleteUser(Long id) {
        AppUser user = user(id);
        if (orders.findAll().stream().anyMatch(order -> order.getUser().getId().equals(id))) {
            throw new IllegalArgumentException("User cannot be deleted while rental orders exist");
        }
        users.delete(user);
    }

    @Transactional(readOnly = true)
    public List<EquipmentUnit> equipment() { return equipment.findAll(); }
    @Transactional(readOnly = true)
    public EquipmentUnit equipment(Long id) { return equipment.findById(id).orElseThrow(() -> missing("Equipment unit", id)); }
    @Transactional
    public EquipmentUnit createEquipment(EquipmentUnitRequest request) {
        required(request.inventoryNumber(), "inventoryNumber"); required(request.type(), "type");
        positive(request.dailyPrice(), "dailyPrice");
        return equipment.save(new EquipmentUnit(request.inventoryNumber(), request.type(), request.dailyPrice(),
                request.condition() == null ? EquipmentCondition.AVAILABLE : request.condition()));
    }
    @Transactional
    public EquipmentUnit updateEquipment(Long id, EquipmentUnitRequest request) {
        EquipmentUnit unit = equipment(id);
        if (request.inventoryNumber() != null) { required(request.inventoryNumber(), "inventoryNumber"); unit.setInventoryNumber(request.inventoryNumber()); }
        if (request.type() != null) { required(request.type(), "type"); unit.setType(request.type()); }
        if (request.dailyPrice() != null) { positive(request.dailyPrice(), "dailyPrice"); unit.setDailyPrice(request.dailyPrice()); }
        if (request.condition() != null) { unit.setCondition(request.condition()); }
        return equipment.save(unit);
    }
    @Transactional
    public void deleteEquipment(Long id) {
        EquipmentUnit unit = equipment(id);
        if (!items.findByEquipmentUnitId(id).isEmpty()) {
            throw new IllegalArgumentException("Equipment unit cannot be deleted while rental items exist");
        }
        equipment.delete(unit);
    }

    @Transactional(readOnly = true)
    public List<RentalOrder> orders() { return orders.findAll(); }
    @Transactional(readOnly = true)
    public RentalOrder order(Long id) { return orders.findById(id).orElseThrow(() -> missing("Rental order", id)); }
    @Transactional
    public RentalOrder createOrder(RentalOrderRequest request) {
        required(request.userId(), "userId"); required(request.startDate(), "startDate"); required(request.endDate(), "endDate");
        validDates(request.startDate(), request.endDate());
        return orders.save(new RentalOrder(user(request.userId()), request.startDate(), request.endDate(),
                request.status() == null ? OrderStatus.DRAFT : request.status()));
    }
    @Transactional
    public RentalOrder updateOrder(Long id, RentalOrderRequest request) {
        RentalOrder order = order(id);
        if (order.getStatus() != OrderStatus.DRAFT) throw new IllegalArgumentException("Only DRAFT orders can be changed through CRUD");
        if (request.userId() != null) order.setUser(user(request.userId()));
        if (request.startDate() != null) order.setStartDate(request.startDate());
        if (request.endDate() != null) order.setEndDate(request.endDate());
        validDates(order.getStartDate(), order.getEndDate());
        if (request.status() != null) order.setStatus(request.status());
        return orders.save(order);
    }
    @Transactional
    public void deleteOrder(Long id) {
        RentalOrder order = order(id);
        if (order.getStatus() == OrderStatus.ACTIVE) throw new IllegalArgumentException("Active order must be completed before deletion");
        for (RentalItem item : items.findByRentalOrderId(id)) {
            if (order.getStatus() == OrderStatus.DRAFT) {
                EquipmentUnit unit = item.getEquipmentUnit();
                unit.setCondition(EquipmentCondition.AVAILABLE);
                equipment.save(unit);
            }
            inspections.deleteAll(itemInspections(item));
            items.delete(item);
        }
        orders.delete(order);
    }

    @Transactional(readOnly = true)
    public List<RentalItem> items() { return items.findAll(); }
    @Transactional(readOnly = true)
    public RentalItem item(Long id) { return items.findById(id).orElseThrow(() -> missing("Rental item", id)); }
    @Transactional
    public RentalItem createItem(RentalItemRequest request) {
        required(request.rentalOrderId(), "rentalOrderId"); required(request.equipmentUnitId(), "equipmentUnitId");
        RentalItem item = new RentalItem(order(request.rentalOrderId()), equipment(request.equipmentUnitId()));
        if (request.returnStatus() != null) item.setReturnStatus(request.returnStatus());
        RentalItem saved = items.save(item);
        recalculate(saved.getRentalOrder());
        return saved;
    }
    @Transactional
    public RentalItem updateItem(Long id, RentalItemRequest request) {
        RentalItem item = item(id);
        RentalOrder previousOrder = item.getRentalOrder();
        if (request.rentalOrderId() != null) item.setRentalOrder(order(request.rentalOrderId()));
        if (request.equipmentUnitId() != null) item.setEquipmentUnit(equipment(request.equipmentUnitId()));
        if (request.returnStatus() != null) item.setReturnStatus(request.returnStatus());
        RentalItem saved = items.save(item);
        if (!previousOrder.getId().equals(saved.getRentalOrder().getId())) recalculate(previousOrder);
        recalculate(saved.getRentalOrder());
        return saved;
    }
    @Transactional
    public void deleteItem(Long id) {
        RentalItem item = item(id);
        if (item.getRentalOrder().getStatus() == OrderStatus.ACTIVE) throw new IllegalArgumentException("Item cannot be deleted from an active order");
        if (item.getRentalOrder().getStatus() == OrderStatus.DRAFT) {
            EquipmentUnit unit = item.getEquipmentUnit();
            unit.setCondition(EquipmentCondition.AVAILABLE);
            equipment.save(unit);
        }
        inspections.deleteAll(itemInspections(item));
        items.delete(item);
        recalculate(item.getRentalOrder());
    }

    @Transactional(readOnly = true)
    public List<InspectionRecord> inspections() { return inspections.findAll(); }
    @Transactional(readOnly = true)
    public InspectionRecord inspection(Long id) { return inspections.findById(id).orElseThrow(() -> missing("Inspection record", id)); }
    @Transactional
    public InspectionRecord createInspection(InspectionRecordRequest request) {
        required(request.rentalItemId(), "rentalItemId"); required(request.inspectionType(), "inspectionType"); required(request.condition(), "condition");
        return inspections.save(new InspectionRecord(item(request.rentalItemId()), request.inspectionType(), request.condition()));
    }
    @Transactional
    public InspectionRecord updateInspection(Long id, InspectionRecordRequest request) {
        InspectionRecord record = inspection(id);
        if (request.rentalItemId() != null) record.setRentalItem(item(request.rentalItemId()));
        if (request.inspectionType() != null) record.setInspectionType(request.inspectionType());
        if (request.condition() != null) record.setCondition(request.condition());
        return inspections.save(record);
    }
    @Transactional
    public void deleteInspection(Long id) { inspections.delete(inspection(id)); }

    // Business operation 1: reserves an available unit and appends it to a draft order.
    @Transactional
    public RentalItem addItemToDraftOrder(Long orderId, AddItemRequest request) {
        required(request.equipmentUnitId(), "equipmentUnitId");
        RentalOrder order = order(orderId);
        requireStatus(order, OrderStatus.DRAFT, "Only DRAFT orders can receive equipment");
        EquipmentUnit unit = equipment(request.equipmentUnitId());
        if (unit.getCondition() != EquipmentCondition.AVAILABLE) throw new IllegalArgumentException("Equipment unit is not available");
        RentalItem item = items.save(new RentalItem(order, unit));
        unit.setCondition(EquipmentCondition.RENTED);
        equipment.save(unit);
        recalculate(order);
        return item;
    }

    // Business operation 2: issues a draft order and records an issue inspection for every item.
    @Transactional
    public RentalOrder issueOrder(Long orderId) {
        RentalOrder order = order(orderId);
        requireStatus(order, OrderStatus.DRAFT, "Only DRAFT orders can be issued");
        List<RentalItem> orderItems = items.findByRentalOrderId(orderId);
        if (orderItems.isEmpty()) throw new IllegalArgumentException("Order must contain at least one item");
        orderItems.forEach(item -> inspections.save(new InspectionRecord(item, InspectionType.ISSUE, InspectionCondition.GOOD)));
        order.setStatus(OrderStatus.ACTIVE);
        return orders.save(order);
    }

    // Business operation 3: returns one item, changes the unit condition and completes the order if all items are returned.
    @Transactional
    public RentalItem returnItem(Long itemId, ReturnItemRequest request) {
        required(request.condition(), "condition");
        RentalItem item = item(itemId);
        RentalOrder order = item.getRentalOrder();
        requireStatus(order, OrderStatus.ACTIVE, "Only items of ACTIVE orders can be returned");
        if (item.getReturnStatus() == ReturnStatus.RETURNED) throw new IllegalArgumentException("Rental item has already been returned");
        item.setReturnStatus(ReturnStatus.RETURNED);
        items.save(item);
        inspections.save(new InspectionRecord(item, InspectionType.RETURN, request.condition()));
        EquipmentUnit unit = item.getEquipmentUnit();
        unit.setCondition(request.condition() == InspectionCondition.GOOD ? EquipmentCondition.AVAILABLE : EquipmentCondition.MAINTENANCE);
        equipment.save(unit);
        if (items.findByRentalOrderId(order.getId()).stream().allMatch(i -> i.getReturnStatus() == ReturnStatus.RETURNED)) {
            order.setStatus(OrderStatus.COMPLETED);
            orders.save(order);
        }
        return item;
    }

    // Business operation 4: cancels a draft order and releases every reserved unit.
    @Transactional
    public RentalOrder cancelOrder(Long orderId) {
        RentalOrder order = order(orderId);
        requireStatus(order, OrderStatus.DRAFT, "Only DRAFT orders can be cancelled");
        for (RentalItem item : items.findByRentalOrderId(orderId)) {
            EquipmentUnit unit = item.getEquipmentUnit();
            unit.setCondition(EquipmentCondition.AVAILABLE);
            equipment.save(unit);
            inspections.deleteAll(itemInspections(item));
            items.delete(item);
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setTotalAmount(BigDecimal.ZERO);
        return orders.save(order);
    }

    // Business operation 5: sends an unused unit to maintenance.
    @Transactional
    public EquipmentUnit sendToMaintenance(Long equipmentId) {
        EquipmentUnit unit = equipment(equipmentId);
        if (unit.getCondition() != EquipmentCondition.AVAILABLE) throw new IllegalArgumentException("Only available equipment can be sent to maintenance");
        if (items.findByEquipmentUnitId(equipmentId).stream().anyMatch(item -> item.getRentalOrder().getStatus() == OrderStatus.ACTIVE)) {
            throw new IllegalArgumentException("Equipment is involved in an active rental");
        }
        unit.setCondition(EquipmentCondition.MAINTENANCE);
        return equipment.save(unit);
    }

    private void recalculate(RentalOrder order) {
        long days = Math.max(1, ChronoUnit.DAYS.between(order.getStartDate(), order.getEndDate()));
        BigDecimal total = items.findByRentalOrderId(order.getId()).stream()
                .map(item -> item.getFixedDailyPrice().multiply(BigDecimal.valueOf(days)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        orders.save(order);
    }

    private List<InspectionRecord> itemInspections(RentalItem item) {
        return inspections.findAll().stream().filter(record -> record.getRentalItem().getId().equals(item.getId())).toList();
    }
    private void validDates(java.time.LocalDate start, java.time.LocalDate end) {
        if (end.isBefore(start)) throw new IllegalArgumentException("endDate must not be before startDate");
    }
    private void requireStatus(RentalOrder order, OrderStatus status, String message) {
        if (order.getStatus() != status) throw new IllegalArgumentException(message);
    }
    private NotFoundException missing(String type, Long id) { return new NotFoundException(type + " " + id + " not found"); }
    private <T> void required(T value, String name) {
        if (value == null || value instanceof String text && text.isBlank()) throw new IllegalArgumentException(name + " is required");
    }
    private void positive(BigDecimal value, String name) {
        required(value, name);
        if (value.signum() <= 0) throw new IllegalArgumentException(name + " must be positive");
    }
}
