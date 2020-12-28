package camelinaction.chapter8.splitter;

public class Department {
    private int id;
    private String address;
    private String zip;
    private String country;

    public Department() {
    }

    public Department(int id, String address, String zip, String country) {
        this.id = id;
        this.address = address;
        this.zip = zip;
        this.country = country;
    }

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
}
