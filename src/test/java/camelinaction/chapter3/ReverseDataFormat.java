package camelinaction.chapter3;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;

import java.io.InputStream;
import java.io.OutputStream;

public class ReverseDataFormat implements DataFormat {
    public void marshal(Exchange exchange, Object o, OutputStream outputStream) throws Exception {
        byte[] bytes = exchange.getContext().getTypeConverter()
                .mandatoryConvertTo(byte[].class, o);
        String body = reverseBytes(bytes);
        outputStream.write(body.getBytes());
    }

    public Object unmarshal(Exchange exchange, InputStream inputStream) throws Exception {
        byte[] bytes = exchange.getContext().getTypeConverter()
                .mandatoryConvertTo(byte[].class, inputStream);
        String body = reverseBytes(bytes);
        return body;
    }

    private static String reverseBytes(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length);
        for (int i = data.length - 1; i >= 0; i--) {
            char ch = (char) data[i];
            sb.append(ch);
        }
        return sb.toString();
    }
}
