package camelinaction.chapter14;

import java.io.Serializable;

public class ShippingDetail implements Serializable {

    private String address;
    private String zip;
    private String country;
    private String amount;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return address + ", " + zip + ", " + country + ", " + amount;
    }
}
