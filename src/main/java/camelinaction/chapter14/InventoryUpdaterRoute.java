package camelinaction.chapter14;

import org.apache.camel.builder.RouteBuilder;

public class InventoryUpdaterRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("jms:partnerInventoryUpdate")
                .to("bean:inventoryHelperInstance");
    }
}
