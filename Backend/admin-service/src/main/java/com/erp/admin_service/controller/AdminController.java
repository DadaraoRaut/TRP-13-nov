package com.erp.admin_service.controller;


import com.erp.admin_service.dto.StockUpdateRequest;
import com.erp.admin_service.model.Employee;
import com.erp.admin_service.model.Supplier;
import com.erp.admin_service.model.Inventory;
import com.erp.admin_service.model.PurchaseOrder;
import com.erp.admin_service.service.EmployeeService;
import com.erp.admin_service.service.InventoryService;
import com.erp.admin_service.service.PurchaseOrderService;
import com.erp.admin_service.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final EmployeeService employeeService;
    private final SupplierService supplierService;
    private final PurchaseOrderService poService;
    private final InventoryService inventoryService;

    public AdminController(EmployeeService employeeService, SupplierService supplierService, PurchaseOrderService poService, InventoryService inventoryService) {
        this.employeeService = employeeService;
        this.supplierService = supplierService;
        this.poService = poService;
        this.inventoryService = inventoryService;
    }

    // 🧍 Employee APIs
    @PostMapping("/employees")
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee emp) {
        Employee saved = employeeService.addEmployee(emp);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return employees.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Employee> getEmployeeByEmployeeId(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmployeeId(employeeId));
    }

    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok("Employee deleted successfully with EmployeeId: " + employeeId);
    }


    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> addSupplier(
            @Valid @RequestBody Supplier sup,
            @RequestHeader("Authorization") String authHeader) {

        // Extract JWT token from "Bearer <token>"
        String adminJwtToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        Supplier saved = supplierService.addSupplier(sup, adminJwtToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return suppliers.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(suppliers);
    }

    // 📦 Purchase Order APIs
    @PostMapping("/purchase-orders")
    public ResponseEntity<PurchaseOrder> createPO(@Valid @RequestBody PurchaseOrder po) {
        PurchaseOrder saved = poService.createOrder(po);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<PurchaseOrder>> getAllPOs() {
        List<PurchaseOrder> orders = poService.getAllOrders();
        return orders.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(orders);
    }


    // 📦 Update Purchase Order Status (ACCEPTED / REJECTED)
    @PostMapping("/purchase-orders/{orderId}/status")
    public ResponseEntity<PurchaseOrder> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status) {

        PurchaseOrder updatedOrder = poService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }

    @PreAuthorize("#supplierId == authentication.principal.username or hasRole('ADMIN')")
    @GetMapping("/purchase-orders/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>> getOrdersBySupplier(@PathVariable String supplierId) {
        List<PurchaseOrder> orders = poService.getOrdersBySupplierId(supplierId);
        return orders.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(orders);
    }


    // 🏪 Add or update stock
    @PostMapping("/inventory")
    public ResponseEntity<Inventory> addOrUpdateStock(@Valid @RequestBody Inventory stock) {
        Inventory saved = inventoryService.addOrUpdateStock(stock);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // 🏪 Get all inventory items
    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getAllStock() {
        List<Inventory> stockList = inventoryService.getAllStock();
        return stockList.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(stockList);
    }

    // Optional: Get stock by productId
    @GetMapping("/inventory/{productId}")
    public ResponseEntity<Inventory> getStockByProductId(@PathVariable String productId) {
        return inventoryService.getStockByProductId(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // 🏪 Decrement stock after billing (Accessible by Billing Service)
    @PostMapping("/inventory/update-stock")
    public ResponseEntity<String> updateStockAfterBilling(@RequestBody List<StockUpdateRequest> updates) {
        boolean allUpdated = true;

        for (StockUpdateRequest update : updates) {
            boolean result = inventoryService.decrementStock(update.getProductId(), update.getQuantity());
            if (!result) {
                allUpdated = false;
            }
        }

        if (allUpdated)
            return ResponseEntity.ok("All stock updated successfully");
        else
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("Some products could not be updated due to insufficient stock");
    }

}

