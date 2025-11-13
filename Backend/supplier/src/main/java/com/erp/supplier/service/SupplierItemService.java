package com.erp.supplier.service;

import com.erp.supplier.entity.SupplierItem;
import com.erp.supplier.repo.SupplierItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierItemService {

    private final SupplierItemRepository itemRepository;

    public SupplierItemService(SupplierItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    // 🔹 Add new item with auto-generated productId (ITEM001, ITEM002...)
    public SupplierItem addItem(SupplierItem item) {
        long count = itemRepository.count() + 1;
        String productId = String.format("ITEM%03d", count);
        item.setProductId(productId);
        return itemRepository.save(item);
    }

    // 🔹 Get all items for a supplier
    public List<SupplierItem> getSupplierItems(String supplierId) {
        return itemRepository.findBySupplierId(supplierId);
    }

    // 🔹 Fetch product by ID (return null if not found instead of throwing)
    public SupplierItem getByProductId(String productId) {
        return itemRepository.findByProductId(productId)
                .orElse(null);
    }
}
