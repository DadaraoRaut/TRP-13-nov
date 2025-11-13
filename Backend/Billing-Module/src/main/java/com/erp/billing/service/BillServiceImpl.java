package com.erp.billing.service;
import com.erp.billing.dto.BillCreateDTO;
import com.erp.billing.dto.BillItemDTO;
import com.erp.billing.entity.BillCreate;
import com.erp.billing.entity.BillItem;
import com.erp.billing.entity.PaymentMethod;
import com.erp.billing.entity.PaymentStatus;
import com.erp.billing.repository.BillCreateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final BillCreateRepository billCreateRepository;
    @Override
    public BillCreate createBill(BillCreateDTO dto) {
        // ✅ Validate GST
        if (dto.getGstPercentage() == null) dto.setGstPercentage(18.0);
        if (dto.getGstPercentage() < 0 || dto.getGstPercentage() > 18) {
            throw new IllegalArgumentException("GST must be between 0% and 18%");
        }
        // ✅ Auto-generate bill number
        String billNumber = "BILL" + String.format("%03d", (billCreateRepository.count() + 1));
        // ✅ Map directly from DTO items (no external call)
        List<BillItem> items = dto.getItems().stream().map(itemDTO -> {
            BillItem item = new BillItem();
            item.setItemId(itemDTO.getItemId());
            item.setName(itemDTO.getName());
            item.setCategory(itemDTO.getCategory());
            item.setBarcode(itemDTO.getBarcode());
            item.setPricePerUnit(itemDTO.getPricePerUnit());
            item.setQuantity(itemDTO.getQuantity());
            item.setTotalAmount(item.getPricePerUnit() * item.getQuantity());
            return item;
        }).collect(Collectors.toList());
        // ✅ Calculate totals
        double subtotal = items.stream().mapToDouble(BillItem::getTotalAmount).sum();
        double tax = subtotal * dto.getGstPercentage() / 100;
        double total = subtotal + tax;
        // ✅ Create Bill Entity
        BillCreate bill = new BillCreate();
        bill.setBillNumber(billNumber);
        bill.setDateTime(LocalDateTime.now());
        bill.setItems(items);
        bill.setSubtotal(subtotal);
        bill.setTax(tax);
        bill.setTotalAmount(total);
        bill.setPaymentMethod(dto.getPaymentMethod());
        bill.setPaymentDetail(dto.getPaymentDetail());
        bill.setBilledBy(dto.getBilledBy());
        bill.setPrinted(false);
        bill.setPaymentStatus(
                (dto.getPaymentMethod() == PaymentMethod.CASH ||
                        dto.getPaymentMethod() == PaymentMethod.UPI ||
                        dto.getPaymentMethod() == PaymentMethod.CARD)
                        ? PaymentStatus.PAID : PaymentStatus.PENDING
        );
        bill.setGstPercentage(dto.getGstPercentage());
        return billCreateRepository.save(bill);
    }
    @Override
    public List<BillCreate> getAllBills() {
        // ✅ Uses the built-in JPA method
        return billCreateRepository.findAll();
    }
}

