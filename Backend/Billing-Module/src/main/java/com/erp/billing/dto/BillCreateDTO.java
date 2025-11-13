package com.erp.billing.dto;

import com.erp.billing.entity.PaymentMethod;
import lombok.Data;
import java.util.List;

@Data
public class BillCreateDTO {
        private List<BillItemDTO> items;
        private PaymentMethod paymentMethod;
        private String paymentDetail;
        private Long billedBy;
        private Double gstPercentage;



    public List<BillItemDTO> getItems() {
        return items;
    }

    public void setItems(List<BillItemDTO> items) {
        this.items = items;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(String paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    public Long getBilledBy() {
        return billedBy;
    }

    public void setBilledBy(Long billedBy) {
        this.billedBy = billedBy;
    }

    public Double getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(Double gstPercentage) {
        this.gstPercentage = gstPercentage;
    }
}


