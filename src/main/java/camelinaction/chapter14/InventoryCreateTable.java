package camelinaction.chapter14;

import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class InventoryCreateTable {

    public InventoryCreateTable(DataSource ds) {
        JdbcTemplate jdbc = new JdbcTemplate(ds);

        try {
            jdbc.execute("drop table partner_inventory");
        } catch (Exception e) {
            // e.printStackTrace();
        }
        jdbc.execute("create table partner_inventory "
                + "( supplier_id varchar(10), part_id varchar(10), name varchar(10), amount varchar(10) )");
    }
}
