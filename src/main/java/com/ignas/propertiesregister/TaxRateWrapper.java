package com.ignas.propertiesregister;

import java.util.List;

public class TaxRateWrapper {
    // @param pricings is a list of different tax rates given by the post request
    private List<TaxRate> pricings;

    public List<TaxRate> getPricings() {
        return pricings;
    }

    public void setPricings(List<TaxRate> pricings) {
        this.pricings = pricings;
    }
}
