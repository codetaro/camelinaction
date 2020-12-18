package camelinaction;

import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http.HttpOperationFailedException;

import java.io.IOException;

public class UseCaseRoute extends RouteBuilder {

    @EndpointInject(ref = "fileEndpoint")
    private Endpoint file;

    @EndpointInject(ref = "httpEndpoint")
    private Endpoint http;

    @EndpointInject(ref = "ftpEndpoint")
    private Endpoint ftp;

    @Override
    public void configure() throws Exception {
        getContext().setTracing(true);

        errorHandler(defaultErrorHandler()
                .maximumRedeliveries(5)
                .redeliveryDelay(2000)
                .retryAttemptedLogLevel(LoggingLevel.WARN));

        onException(IOException.class, HttpOperationFailedException.class)
                .maximumRedeliveries(3)
                .handled(true)
                .to(ftp);

        from(file).to(http);
    }
}
