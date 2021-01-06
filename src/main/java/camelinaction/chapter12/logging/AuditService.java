package camelinaction.chapter12.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditService {

    private Logger LOG = LoggerFactory.getLogger(AuditService.class);

    public void auditFile(String body) {
        String[] parts = body.split(",");
        String id = parts[0];
        String customerId = parts[1];

        String msg = "Customer " + customerId + " send order id " + id;

        LOG.info(msg);
    }
}
