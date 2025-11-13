package com.erp.supplier.service;

import com.erp.supplier.dto.InventoryDTO;
import com.erp.supplier.entity.PurchaseOrderResponse;
import com.erp.supplier.entity.SupplierItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SupplierOrderService {

    private final RestTemplate restTemplate;
    private final SupplierItemService supplierItemService;

    @Value("${gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @Autowired
    public SupplierOrderService(RestTemplate restTemplate, SupplierItemService supplierItemService) {
        this.restTemplate = restTemplate;
        this.supplierItemService = supplierItemService;
    }

    // 🔹 Get all purchase orders for this supplier
    public List<PurchaseOrderResponse> getOrdersForSupplier(String supplierId, String jwtToken) {
        String url = String.format("%s/admin/purchase-orders/supplier/%s", gatewayUrl, supplierId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<PurchaseOrderResponse[]> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, PurchaseOrderResponse[].class);

            PurchaseOrderResponse[] orders = response.getBody();
            if (orders == null || orders.length == 0) {
                return Collections.emptyList();
            }

            return Arrays.stream(orders)
                    .map(this::enrichOrderWithProductDetails)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            System.err.println("❌ Error fetching orders from Admin service: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    // 🔸 Convert productIds → product details (with name, type, etc.)
    private PurchaseOrderResponse enrichOrderWithProductDetails(PurchaseOrderResponse order) {
        if (order.getProductIds() != null && !order.getProductIds().isEmpty()) {

            List<PurchaseOrderResponse.ProductDetail> detailedProducts = order.getProductIds().stream()
                    .map(productId -> {
                        System.out.println("🔎 Fetching product details for " + productId);
                        SupplierItem item = supplierItemService.getByProductId(productId);

                        PurchaseOrderResponse.ProductDetail detail = new PurchaseOrderResponse.ProductDetail();
                        detail.setProductId(productId);

                        if (item != null) {
                            System.out.println("✅ Found product: " + item.getProductName());
                            detail.setProductName(item.getProductName());
                            detail.setProductType(item.getProductType() != null ? item.getProductType().name() : "N/A");
                            detail.setUnitType(item.getUnitType() != null ? item.getUnitType().name() : "N/A");
                            detail.setPricePerUnit(item.getPricePerUnit());
                            detail.setQuantity(item.getQuantity());
                        } else {
                            System.out.println("⚠️ Product not found for ID: " + productId);
                            detail.setProductName("Unknown Product (" + productId + ")");
                            detail.setProductType("N/A");
                            detail.setUnitType("N/A");
                        }

                        return detail;
                    }).collect(Collectors.toList());

            order.setProducts(detailedProducts);
        }
        return order;
    }

    // 🔹 Update order status (and inventory if accepted)
    public PurchaseOrderResponse updateOrderStatus(String orderId, String status, String jwtToken) {
        String url = String.format("%s/admin/purchase-orders/%s/status?status=%s", gatewayUrl, orderId, status);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);

        ResponseEntity<PurchaseOrderResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(headers), PurchaseOrderResponse.class);

        PurchaseOrderResponse order = response.getBody();

        // ✅ Update inventory if the order is accepted
        if ("ACCEPTED".equalsIgnoreCase(status) && order != null && order.getProducts() != null) {
            for (PurchaseOrderResponse.ProductDetail p : order.getProducts()) {
                try {
                    InventoryDTO stock = new InventoryDTO();
                    stock.setProductId(p.getProductId());
                    stock.setProductName(p.getProductName());
                    stock.setCategory(p.getProductType());
                    stock.setUnit(p.getUnitType());
                    stock.setQuantity(p.getQuantity());
                    stock.setPricePerUnit(p.getPricePerUnit());
                    stock.setSupplierId(order.getSupplierId());

                    HttpEntity<InventoryDTO> inventoryRequest = new HttpEntity<>(stock, headers);
                    restTemplate.postForEntity(gatewayUrl + "/admin/inventory", inventoryRequest, Void.class);

                } catch (Exception ex) {
                    System.err.println("⚠️ Error updating inventory for product " + p.getProductId() + ": " + ex.getMessage());
                }
            }
        }

        return order;
    }
}
