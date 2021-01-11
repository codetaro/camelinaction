package camelinaction.chapter14.middleware;

import camelinaction.chapter14.Inventory;
import camelinaction.chapter14.ShippingDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RiderAutoPartsMock implements RiderService {
    private static Logger LOG = LoggerFactory.getLogger(RiderAutoPartsMock.class);

    @Override
    public void updateInventory(Inventory inventory) {
        LOG.info("Updating inventory " + inventory);
    }

    @Override
    public List<ShippingDetail> shipInventory(String supplierId, String partId) {
        LOG.info("Shipping to Rider Road 66 and Ocean View 123");

        ShippingDetail detail = new ShippingDetail();
        detail.setAddress("Rider Road 66");
        detail.setCountry("USA");
        detail.setZip("90210");
        detail.setAmount("89");

        ShippingDetail detail2 = new ShippingDetail();
        detail2.setAddress("Ocean View 123");
        detail2.setCountry("USA");
        detail2.setZip("89103");
        detail2.setAmount("45");

        List<ShippingDetail> answer = new ArrayList<>();
        answer.add(detail);
        answer.add(detail2);
        return answer;
    }
}
