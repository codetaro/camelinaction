package camelinaction.chapter14;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class InventoryServiceTest extends CamelSpringTestSupport {

    private JdbcTemplate jdbc;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("camelinaction/chapter14/camel-context.xml");
    }

    @Before
    public void setup() throws Exception {
        DataSource ds = context.getRegistry().lookupByNameAndType("inventoryDB", DataSource.class);
        jdbc = new JdbcTemplate(ds);
    }

    @Test
    public void testUpdateInventory() throws Exception {
        assertEquals(0, jdbc.queryForInt("select count(*) from partner_inventory"));

        Inventory inventory = new Inventory("1234", "4444");
        inventory.setName("Bumper");
        inventory.setAmount("57");

        template.sendBody("jms:partnerInventoryUpdate", inventory);

        Thread.sleep(1000);

        assertEquals(1, jdbc.queryForInt("select count(*) from partner_inventory"));
    }
}
