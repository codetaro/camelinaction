package camelinaction.chapter12.custom;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

public class ERPMain {

    public static void main(String[] args) throws Exception {
        ERPMain client = new ERPMain();
        System.out.println("Starting ERPMain... press ctrl+c to stop it");
        client.start();
        System.out.println("... started.");
        Thread.sleep(99999999);
    }

    private void start() throws Exception {
        CamelContext camel = new DefaultCamelContext();

        camel.addComponent("erp", new ERPComponent());
        camel.addRoutes(new ERPRoute());

        camel.start();
    }
}
