package com.erp.admin_service.repository;

import com.erp.admin_service.model.Supplier;
import org.hibernate.validator.constraints.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Optional<Supplier> findBySupplierId(String supplierId);
}

