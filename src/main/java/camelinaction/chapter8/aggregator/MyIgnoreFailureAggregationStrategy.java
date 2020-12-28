package camelinaction.chapter8.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MyIgnoreFailureAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        // Ignores the exception
        if (newExchange.getException() != null) {
            return oldExchange;
        }
        if (oldExchange == null) {
            return newExchange;
        }

        String body = newExchange.getIn().getBody(String.class);
        String existing = oldExchange.getIn().getBody(String.class);
        oldExchange.getIn().setBody(existing + "+" + body);
        return oldExchange;
    }
}
