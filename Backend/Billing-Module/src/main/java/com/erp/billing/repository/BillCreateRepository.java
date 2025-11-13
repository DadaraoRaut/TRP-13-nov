package com.erp.billing.repository;

import com.erp.billing.entity.BillCreate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface BillCreateRepository extends JpaRepository<BillCreate, Long> {
    boolean existsByBillNumber(String billNumber);
}
