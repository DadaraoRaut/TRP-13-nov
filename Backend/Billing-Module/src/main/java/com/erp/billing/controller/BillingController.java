package com.erp.billing.controller;

import com.erp.billing.dto.BillCreateDTO;

import com.erp.billing.entity.BillCreate;

import com.erp.billing.service.BillService;

import com.erp.billing.service.ExcelService;

import com.erp.billing.service.PdfService;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/billing")

@RequiredArgsConstructor

public class BillingController {

    private final BillService billService;

    private final PdfService pdfService;

    private final ExcelService excelService;

    // ✅ Create Bill

    @PostMapping("/create")

    public ResponseEntity<BillCreate> createBill(@RequestBody BillCreateDTO dto){

        return ResponseEntity.ok(billService.createBill(dto));

    }

    // ✅ Get All Bills

    @GetMapping("/all")

    public ResponseEntity<List<BillCreate>> getAllBills() {

        return ResponseEntity.ok(billService.getAllBills());

    }

    // ✅ Download Bill as PDF

    @GetMapping("/download/pdf/{billId}")

    public void downloadPdf(@PathVariable long billId, HttpServletResponse response){

        pdfService.generateBillPdf(billId, response);

    }

    // ✅ Download Bill as Excel

    @GetMapping("/download/excel/{billId}")

    public void downloadExcel(@PathVariable long billId, HttpServletResponse response){

        excelService.generateBillExcel(billId, response);

    }

}

