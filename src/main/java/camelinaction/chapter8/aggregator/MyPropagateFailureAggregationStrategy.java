package camelinaction.chapter8.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MyPropagateFailureAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (newExchange.getException() != null) {
            if (oldExchange == null) {
                return newExchange;
            } else {
                // Propagates exception
                oldExchange.setException(newExchange.getException());
                return oldExchange;
            }
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
