package pearlmart.core;
import java.util.List;
public record Sale(String id, Status status, List<SaleLine> lines, SalesReceipt receipt) {
    public enum Status { PENDING, COMPLETED, CANCELLED }
    public Sale {lines=List.copyOf(lines);}
}
