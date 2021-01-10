package camelinaction.chapter13.routepolicy;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.support.RoutePolicySupport;

public class FlipRoutePolicy extends RoutePolicySupport {
    private final String name1;
    private final String name2;

    public FlipRoutePolicy(String name1, String name2) {
        this.name1 = name1;
        this.name2 = name2;
    }

    @Override
    public void onExchangeDone(Route route, Exchange exchange) {
        String stop = route.getId().equals(name1) ? name1 : name2;
        String start = route.getId().equals(name1) ? name2 : name1;

        CamelContext context = exchange.getContext();
        try {
            exchange.getContext().getInflightRepository().remove(exchange);
            context.stopRoute(stop);
            context.startRoute(start);
        } catch (Exception e) {
            // by default log the exception
            getExceptionHandler().handleException(e);
        }
    }
}
