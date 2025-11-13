package com.erp.admin_service.service;

import com.erp.admin_service.model.Inventory;
import com.erp.admin_service.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // 🔹 Add or update stock
    public Inventory addOrUpdateStock(Inventory inventory) {
        Optional<Inventory> existing = inventoryRepository.findByProductId(inventory.getProductId());
        if (existing.isPresent()) {
            Inventory inv = existing.get();
            inv.setQuantity(inv.getQuantity() + inventory.getQuantity()); // increment quantity
            inv.setPricePerUnit(inventory.getPricePerUnit()); // update price if needed
            inv.setUpdatedAt(LocalDateTime.now());
            return inventoryRepository.save(inv);
        } else {
            inventory.setUpdatedAt(LocalDateTime.now());
            return inventoryRepository.save(inventory);
        }
    }

    // 🔹 Get all inventory items
    public List<Inventory> getAllStock() {
        return inventoryRepository.findAll();
    }

    // 🔹 Find by productId
    public Optional<Inventory> getStockByProductId(String productId) {
        return inventoryRepository.findByProductId(productId);
    }


    @Transactional
    public boolean decrementStock(String productId, Double quantity) {
        Optional<Inventory> optional = inventoryRepository.findByProductId(productId);
        if (optional.isEmpty()) return false;

        Inventory product = optional.get();
        if (product.getQuantity() < quantity) {
            return false; // insufficient stock
        }

        product.setQuantity(product.getQuantity() - quantity);
        product.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(product);
        return true;
    }

}
