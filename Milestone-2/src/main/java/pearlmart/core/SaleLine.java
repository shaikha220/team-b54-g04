package pearlmart.core;
import java.math.BigDecimal;
public record SaleLine(String itemId,String name,BigDecimal unitPrice,int quantity) {
    public SaleLine { if(quantity<=0 || unitPrice==null || unitPrice.signum()<=0) throw new IllegalArgumentException("Invalid sale line"); }
    public BigDecimal amount(){return unitPrice.multiply(BigDecimal.valueOf(quantity));}
}
