package camelinaction.chapter12.health;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.management.DefaultManagementAgent;

public class PingServiceMain {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // configuring the ManagementAgent from Java
        DefaultManagementAgent agent = new DefaultManagementAgent(context);
        agent.setCreateConnector(true);
        context.getManagementStrategy().setManagementAgent(agent);

        context.addRoutes(new PingService());
        context.start();

        System.out.println("Ping service running. Try sending a HTTP GET to http://localhost:8080/ping");
        System.out.println("Camel started use ctrl+c to stop.");

        Runtime.getRuntime().addShutdownHook(new ShutdownThread(context));

        Thread.sleep(99999999);
    }
}
