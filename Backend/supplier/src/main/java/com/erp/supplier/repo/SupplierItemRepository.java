package com.erp.supplier.repo;

import com.erp.supplier.entity.SupplierItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierItemRepository extends JpaRepository<SupplierItem, Long> {
    List<SupplierItem> findBySupplierId(String supplierId);
    Optional<SupplierItem> findByProductId(String productId);
}

