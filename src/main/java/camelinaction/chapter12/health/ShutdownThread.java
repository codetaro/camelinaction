package camelinaction.chapter12.health;

import org.apache.camel.CamelContext;

public class ShutdownThread extends Thread{

    private CamelContext context;

    public ShutdownThread(CamelContext context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            context.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
