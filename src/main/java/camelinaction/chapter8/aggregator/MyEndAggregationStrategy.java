package camelinaction.chapter8.aggregator;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class MyEndAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);

        if ("END".equals(newBody)) {
            return oldExchange;
        }

        String body = oldBody + newBody;

        oldExchange.getIn().setBody(body);
        return oldExchange;
    }
}
