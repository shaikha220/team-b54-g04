package pearlmart.core;
import java.time.Instant;
import java.util.List;
public record SalesReceipt(String saleId,Instant time,List<SaleLine> lines,Totals totals) {
    public SalesReceipt { lines=List.copyOf(lines); }
}
