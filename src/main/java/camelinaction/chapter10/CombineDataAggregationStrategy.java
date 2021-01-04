package camelinaction.chapter10;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class CombineDataAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String to = newExchange.getProperty(Exchange.TO_ENDPOINT, String.class);
        if (to.contains("erp")) {
            return aggregate("ERP", oldExchange, newExchange);
        } else if (to.contains("crm")) {
            return aggregate("CRM", oldExchange, newExchange);
        } else {
            return aggregate("SHIPPING", oldExchange, newExchange);
        }
    }

    public Exchange aggregate(String system, Exchange oldExchange, Exchange newExchange) {
        Exchange answer = oldExchange == null ? newExchange : oldExchange;
        answer.getIn().setHeader(system, newExchange.getIn().getBody());
        return answer;
    }
}
