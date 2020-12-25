package camelinaction.chapter7.db;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jpa.JpaEndpoint;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.persistence.EntityManager;
import java.util.List;

public class JpaTest extends CamelTestSupport {
    
    protected ApplicationContext applicationContext;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("camelinaction/chapter7/camel-context-jpa.xml");
        return SpringCamelContext.springCamelContext(applicationContext);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SpringRouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:accounting")
                        .to("jpa:camelinaction.chapter7.db.PurchaseOrder")
                        .to("mock:result");
            }
        };
    }
    
    @Test
    public void testRouteJpa() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setName("motor");
        purchaseOrder.setAmount(1);
        purchaseOrder.setCustomer("honda");
        
        template.sendBody("jms:accounting", purchaseOrder);
        
        assertMockEndpointsSatisfied();
        assertEntityInDB();
    }

    @SuppressWarnings("unchecked")
    private void assertEntityInDB() throws Exception {
        JpaEndpoint endpoint = (JpaEndpoint) context.getEndpoint("jpa:camelinaction.chapter7.db.PurchaseOrder");
        EntityManager em = endpoint.getEntityManagerFactory().createEntityManager();

        List list = em.createQuery("select x from camelinaction.chapter7.db.PurchaseOrder x").getResultList();
        assertEquals(1, list.size());

        assertIsInstanceOf(PurchaseOrder.class, list.get(0));

        em.close();
    }
}
