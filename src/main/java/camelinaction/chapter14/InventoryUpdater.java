package camelinaction.chapter14;

import org.apache.camel.Consume;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class InventoryUpdater {

    @Produce(ref = "auditQueue", context = "camel-1")
    ProducerTemplate partnerAudit;

    private JdbcTemplate jdbc;

    public InventoryUpdater(DataSource ds) {
        jdbc = new JdbcTemplate(ds);
    }

    @Consume(ref = "inventoryQueue", context = "camel-1")
    public void updateInventory(Inventory inventory) {
        jdbc.execute(toSql(inventory));
        partnerAudit.sendBody(inventory);
    }

    private String toSql(Inventory inventory) {
        Object[] args = new Object[]{
                inventory.getSupplierId(), inventory.getPartId(),
                inventory.getName(), inventory.getAmount()};
        return String.format("insert into partner_inventory " +
                "(supplier_id, part_id, name, amount) values " +
                "('%s', '%s', '%s', '%s')", args);
    }
}
