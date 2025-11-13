package com.erp.admin_service.service;

import com.erp.admin_service.exception.ResourceNotFoundException;
import com.erp.admin_service.model.Supplier;
import com.erp.admin_service.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    @Value("${auth.service.url:http://localhost:8080/api/auth}")
    private String authServiceUrl;

    public SupplierService(SupplierRepository supplierRepository, EmailService emailService) {
        this.supplierRepository = supplierRepository;
        this.emailService = emailService;
        this.restTemplate = new RestTemplate();
    }

    // Add new Supplier (Admin-only)
    public Supplier addSupplier(Supplier supplier, String adminJwtToken) {
        // Generate Supplier ID
        String supplierId = "SUP" + String.format("%03d", new Random().nextInt(999));
        while (supplierRepository.findBySupplierId(supplierId).isPresent()) {
            supplierId = "SUP" + String.format("%03d", new Random().nextInt(999));
        }

        // Generate random password
        String password = UUID.randomUUID().toString().substring(0, 8);

        supplier.setSupplierId(supplierId);
        supplier.setPassword(password);

        // Save supplier in Admin DB
        Supplier savedSupplier = supplierRepository.save(supplier);
        System.out.println("✅ Supplier saved locally with ID: " + supplierId);

        // Send credentials email
        try {
            emailService.sendCredentials(supplier.getEmail(), supplierId, password, "Supplier");
            System.out.println("📧 Credentials sent to " + supplier.getEmail());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to send email: " + e.getMessage());
        }

        // Register supplier in AuthService (passing admin JWT)
        registerInAuthService(supplierId, password, "SUPPLIER", adminJwtToken);

        return savedSupplier;
    }

    // 🔐 Register supplier credentials in AuthService
    private void registerInAuthService(String username, String password, String role, String adminJwtToken) {
        try {
            String url = authServiceUrl + "/register";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("role", role);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminJwtToken); // pass admin JWT

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Registered supplier in AuthService: " + username);
            } else {
                System.err.println("⚠️ AuthService registration failed: " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("🚨 Error calling AuthService: " + e.getMessage());
        }
    }

    // Get all suppliers
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // Get supplier by ID
    public Supplier getSupplierById(String supplierId) {
        return supplierRepository.findBySupplierId(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));
    }

    // Delete supplier
    public void deleteSupplier(String supplierId) {
        Supplier sup = supplierRepository.findBySupplierId(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot delete — Supplier not found with ID: " + supplierId));

        supplierRepository.delete(sup);
        System.out.println("🗑️ Supplier deleted: " + supplierId);
    }
}
