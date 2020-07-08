package com.ignas.propertiesregister;

public class TaxRate {
    private String typeName;
    private double taxRate;

    TaxRate(String t, double rate){
        typeName = t;
        taxRate = rate;
    }

    public String getTypeName() {
        return typeName;
    }
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    public double getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }
}
