package com.ignas.propertiesregister;

import org.springframework.hateoas.RepresentationModel;

public class TotalTax extends RepresentationModel<TotalTax> {
    // @param owner is the name of the person total is stored
    private String owner;
    private String total;

    public TotalTax(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
