package com.erp.supplier.dto;


import com.erp.supplier.entity.ProductType;
import com.erp.supplier.entity.UnitType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class SupplierItemRequest {

    @NotBlank
    private String productName;

    @NotNull
    private ProductType productType;

    @NotNull
    private UnitType unitType;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal pricePerUnit;

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }
// Getters & Setters
}
