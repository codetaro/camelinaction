package camelinaction.chapter3;

import org.apache.camel.Converter;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConverter;

import java.math.BigDecimal;

@Converter
public class PurchaseOrderConverter {

    @Converter
    public static PurchaseOrderCsv toPurchaseOrder(byte[] data, Exchange exchange) {
        // Grabs TypeConverter to reuse
        TypeConverter converter = exchange.getContext().getTypeConverter();
        String s = converter.convertTo(String.class, data);
        if (s == null || s.length() < 30) {
            throw new IllegalArgumentException("data is invalid");
        }

        // Converts from String to PurchaseOrder
        s = s.replaceAll("##START##", "");
        s = s.replaceAll("##END##", "");

        String name = s.substring(0, 9).trim();

        String s2 = s.substring(10, 19).trim();
        BigDecimal price = new BigDecimal(s2);
        price.setScale(2);

        String s3 = s.substring(20).trim();
        Integer amount = converter.convertTo(Integer.class, s3);

        return new PurchaseOrderCsv(name, price, amount);
    }
}
