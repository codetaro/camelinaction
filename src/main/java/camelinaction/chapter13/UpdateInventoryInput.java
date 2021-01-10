package camelinaction.chapter13;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "supplierId",
        "partId",
        "name",
        "amount"
})
@XmlRootElement(name = "updateInventoryInput")
public class UpdateInventoryInput {

    @XmlElement(required = true)
    protected String supplierId;
    @XmlElement(required = true)
    protected String partId;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String amount;

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getPartId() {
        return partId;
    }

    public void setPartId(String partId) {
        this.partId = partId;
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
}
