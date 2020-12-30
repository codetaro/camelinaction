package camelinaction.chapter9.uow;

import org.apache.camel.Exchange;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileRollback implements Synchronization {

    private static Logger LOG = LoggerFactory.getLogger(FileRollback.class);

    @Override
    public void onComplete(Exchange exchange) {

    }

    @Override
    public void onFailure(Exchange exchange) {
        String name = exchange.getIn().getHeader(
                Exchange.FILE_NAME_PRODUCED, String.class);
        LOG.warn("Failure occurred so deleting backup file: " + name);

        FileUtil.deleteFile(new File(name));
    }
}
