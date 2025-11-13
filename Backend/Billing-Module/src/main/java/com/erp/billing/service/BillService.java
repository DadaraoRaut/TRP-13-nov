package com.erp.billing.service;

import com.erp.billing.dto.BillCreateDTO;
import com.erp.billing.entity.BillCreate;

import java.util.List;

public interface BillService {
    BillCreate createBill(BillCreateDTO billCreateDTO);
    List<BillCreate> getAllBills();
}

