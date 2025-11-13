package com.erp.admin_service.repository;

import com.erp.admin_service.model.PurchaseOrder;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    Optional<PurchaseOrder> findByOrderId(String orderId);

    List<PurchaseOrder> findBySupplierId(String supplierId);

}

