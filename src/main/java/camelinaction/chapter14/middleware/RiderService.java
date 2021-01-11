package camelinaction.chapter14.middleware;

import camelinaction.chapter14.Inventory;
import camelinaction.chapter14.ShippingDetail;

import java.util.List;

public interface RiderService {
    // send update inventory information to Rider Auto Parts
    void updateInventory(Inventory inventory);

    // find out which of the Rider Auto Parts stores
    // inventory should be shipped to
    List<ShippingDetail> shipInventory(String supplierId, String partId);
}
