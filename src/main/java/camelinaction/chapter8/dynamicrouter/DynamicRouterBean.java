package camelinaction.chapter8.dynamicrouter;

import org.apache.camel.DynamicRouter;
import org.apache.camel.Exchange;
import org.apache.camel.Header;

public class DynamicRouterBean {

    @DynamicRouter
    public String route(String body, @Header(Exchange.SLIP_ENDPOINT) String previous) {
        return whereToGo(body, previous);
    }

    private String whereToGo(String body, String previous) {
        if (previous == null) {
            return "mock://a";
        } else if ("mock://a".equals(previous)) {
            return "language://simple:Bye ${body}";
        } else {
            return  null;
        }
    }
}
