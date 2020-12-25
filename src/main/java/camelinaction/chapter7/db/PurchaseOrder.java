package camelinaction.chapter7.db;

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 414632787969243627L;

    private String name;
    private double amount;
    private String customer;

    public PurchaseOrder() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
