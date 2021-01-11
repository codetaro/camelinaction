package camelinaction.chapter14;

import java.io.Serializable;

/**
 * Domain object.
 * <p/>
 * Must be serializable to be transferred over the network (from client to server)
 *
 */
public class Inventory implements Serializable {

    private String supplierId;
    private String partId;
    private String name;
    private String amount;

    public Inventory(String supplierId, String partId) {
        this.supplierId = supplierId;
        this.partId = partId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getPartId() {
        return partId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return supplierId + ", " + partId + ", " + name + ", " + amount;
    }
}
