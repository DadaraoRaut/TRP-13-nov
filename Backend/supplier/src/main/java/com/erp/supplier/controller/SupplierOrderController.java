package com.erp.supplier.controller;

import com.erp.supplier.entity.PurchaseOrderResponse;
import com.erp.supplier.security.JwtUtil;
import com.erp.supplier.service.SupplierOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier/orders")
public class SupplierOrderController {

    private final SupplierOrderService orderService;
    private final JwtUtil jwtUtil;

    public SupplierOrderController(SupplierOrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    // 🔹 View all purchase orders assigned to logged-in supplier
    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getMyOrders(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        String supplierId = jwtUtil.extractUsername(token);

        List<PurchaseOrderResponse> orders = orderService.getOrdersForSupplier(supplierId, token);
        return ResponseEntity.ok(orders);
    }

    // 🔹 Accept or Reject a purchase order
    @PostMapping("/{orderId}/{action}")
    public ResponseEntity<PurchaseOrderResponse> respondToOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String orderId,
            @PathVariable String action) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        final String ACCEPTED = "ACCEPTED";
        final String REJECTED = "REJECTED";

        if (!action.equalsIgnoreCase("accept") && !action.equalsIgnoreCase("reject")) {
            return ResponseEntity.badRequest().body(null);
        }

        String status = action.equalsIgnoreCase("accept") ? ACCEPTED : REJECTED;

        try {
            PurchaseOrderResponse response = orderService.updateOrderStatus(orderId, status, token);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("Error responding to order: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
