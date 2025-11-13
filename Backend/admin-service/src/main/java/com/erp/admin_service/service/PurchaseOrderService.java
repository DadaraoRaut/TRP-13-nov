package com.erp.admin_service.service;

import com.erp.admin_service.exception.ResourceNotFoundException;
import com.erp.admin_service.model.Inventory;
import com.erp.admin_service.model.OrderStatus;
import com.erp.admin_service.model.PurchaseOrder;
import com.erp.admin_service.repository.PurchaseOrderRepository;
import com.erp.admin_service.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final EmailService emailService;
    private final SupplierRepository supplierRepository;
    private final InventoryService inventoryService; // ✅ inject inventory service

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
                                EmailService emailService,
                                SupplierRepository supplierRepository,
                                InventoryService inventoryService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.emailService = emailService;
        this.supplierRepository = supplierRepository;
        this.inventoryService = inventoryService;
    }

    // 🆕 Create Purchase Order
    public PurchaseOrder createOrder(PurchaseOrder order) {
        order.setOrderId("PO" + String.format("%03d", new Random().nextInt(999)));
        order.setPaymentStatus("SUCCESS");
        PurchaseOrder saved = purchaseOrderRepository.save(order);

        supplierRepository.findBySupplierId(order.getSupplierId())
                .ifPresent(supplier -> emailService.sendCredentials(
                        supplier.getEmail(),
                        order.getOrderId(),
                        "Payment Successful - Amount: ₹" + order.getTotalAmount(),
                        "Purchase Order"));

        return saved;
    }

    // 🔹 Fetch all POs for a supplier
    public List<PurchaseOrder> getOrdersBySupplierId(String supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId);
    }

    // 🔁 Update Order Status (ACCEPTED / REJECTED)
    public PurchaseOrder updateOrderStatus(String orderId, String status) {
        PurchaseOrder order = purchaseOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        order.setOrderStatus(OrderStatus.valueOf(status.toUpperCase()));
        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

//        // ✅ If ACCEPTED, update inventory automatically
//        if (OrderStatus.ACCEPTED.equals(order.getOrderStatus()) && order.getProductIds() != null) {
//            order.getProductIds().forEach(productId -> {
//                SupplierItem item = supplierItemService.getByProductId(productId); // fetch details from supplier service
//
//                Inventory inventory = new Inventory();
//                inventory.setProductId(item.getProductId());
//                inventory.setProductName(item.getProductName());
//                inventory.setCategory(item.getProductType().name());
//                inventory.setUnit(item.getUnitType().name());
//                inventory.setPricePerUnit(item.getPricePerUnit());
//                inventory.setQuantity(item.getQuantity()); // or default quantity
//                inventory.setSupplierId(order.getSupplierId());
//
//                inventoryService.addOrUpdateStock(inventory);
//            });
//        }



        return savedOrder;
    }

    // 📋 Get All Orders
    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }
}
