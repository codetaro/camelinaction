package camelinaction.chapter12.notifier;

import java.util.Date;

public interface RiderFailurePublisher {

    public void publish(String appId, String eventId, Date timestamp, String message);
}
