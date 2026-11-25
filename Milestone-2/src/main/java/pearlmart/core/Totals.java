package pearlmart.core;
import java.math.BigDecimal;
public record Totals(BigDecimal subtotal,BigDecimal discount,BigDecimal tax,BigDecimal payable) {}
