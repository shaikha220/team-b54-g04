package pearlmart.core;
import java.util.List;
public interface PricingPolicy { Totals calculate(List<SaleLine> lines); }
