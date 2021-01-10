package camelinaction.chapter13;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "code"
})
@XmlRootElement(name = "updateInventoryOutput")
public class UpdateInventoryOutput {

    @XmlElement(required = true)
    protected String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
