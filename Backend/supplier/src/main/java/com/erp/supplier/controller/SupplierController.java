package com.erp.supplier.controller;


import com.erp.supplier.dto.PurchaseOrder;
import com.erp.supplier.entity.UnitType;
import com.erp.supplier.service.SupplierItemService;
import com.erp.supplier.dto.SupplierItemRequest;
import com.erp.supplier.entity.ProductType;
import com.erp.supplier.entity.SupplierItem;
import com.erp.supplier.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supplier")
public class SupplierController {

    private final SupplierItemService itemService;
    private final JwtUtil jwtUtil;

    public SupplierController(SupplierItemService itemService, JwtUtil jwtUtil) {
        this.itemService = itemService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/items")
    public ResponseEntity<?> addItem(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody SupplierItemRequest request) {

        String token = authHeader.substring(7);
        String supplierId = jwtUtil.extractUsername(token);

        // Validate unit based on product type
        if ((request.getProductType() == ProductType.VEGETABLE || request.getProductType() == ProductType.FRUIT)
                && !(request.getUnitType() == UnitType.KG || request.getUnitType() == UnitType.GM)) {
            return ResponseEntity.badRequest().body("Unit must be KG or GM for Vegetables/Fruits");
        }
        if (request.getProductType() == ProductType.GROCERY
                && !(request.getUnitType() == UnitType.QUANTITY || request.getUnitType() == UnitType.LITER || request.getUnitType() == UnitType.KG)) {
            return ResponseEntity.badRequest().body("Unit must be QUANTITY or LITER for Grocery");
        }

        SupplierItem item = new SupplierItem();
        item.setSupplierId(supplierId);
        item.setProductName(request.getProductName());
        item.setProductType(request.getProductType());
        item.setUnitType(request.getUnitType());
        item.setQuantity(request.getQuantity());
        item.setPricePerUnit(request.getPricePerUnit());

        SupplierItem saved = itemService.addItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }



    @GetMapping("/items")
    public ResponseEntity<List<SupplierItem>> getItems(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String supplierId = jwtUtil.extractUsername(token);
        List<SupplierItem> items = itemService.getSupplierItems(supplierId);
        return ResponseEntity.ok(items);
    }
}
