package com.erp.billing.service;


import com.erp.billing.entity.BillCreate;
import com.erp.billing.repository.BillCreateRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExcelService {
    private final BillCreateRepository billCreateRepository;

    public void generateBillExcel(long billId, HttpServletResponse response){
        BillCreate bill = billCreateRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=bill_"+bill.getBillNumber()+".xlsx");

        // Use Apache POI to write bill content
    }
}

