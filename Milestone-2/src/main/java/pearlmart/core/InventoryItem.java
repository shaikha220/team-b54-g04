package pearlmart.core;
import java.math.BigDecimal;
import java.time.LocalDate;
public record InventoryItem(String id, String barcode, String name, BigDecimal price, int quantity,
                            String manufacturer, String origin, LocalDate expiry, String comments) {
    public InventoryItem {
        id = required(id,"Item ID"); barcode = required(barcode,"Barcode"); name = required(name,"Name");
        manufacturer = required(manufacturer,"Manufacturer"); origin = required(origin,"Origin");
        if(price == null || price.signum() <= 0 || price.stripTrailingZeros().scale() > 2) throw new IllegalArgumentException("Price must be positive with at most two decimals");
        price = price.setScale(2); if(quantity < 0) throw new IllegalArgumentException("Stock cannot be negative");
        comments = comments == null ? "" : comments.strip();
    }
    public static String required(String s,String field) { if(s==null || s.isBlank()) throw new IllegalArgumentException(field+" is required"); return s.strip(); }
    public InventoryItem withQuantity(int q) { return new InventoryItem(id,barcode,name,price,q,manufacturer,origin,expiry,comments); }
}
