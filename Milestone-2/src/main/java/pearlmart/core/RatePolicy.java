package pearlmart.core;
import java.math.*;
import java.util.List;
public record RatePolicy(BigDecimal threshold, BigDecimal discountRate, BigDecimal taxRate) implements PricingPolicy {
    public RatePolicy { if(threshold==null || threshold.signum()<0 || !valid(discountRate) || !valid(taxRate)) throw new IllegalArgumentException("Invalid pricing policy"); }
    private static boolean valid(BigDecimal n){return n!=null && n.signum()>=0 && n.compareTo(BigDecimal.ONE)<=0;}
    private static BigDecimal money(BigDecimal n){return n.setScale(2,RoundingMode.HALF_UP);}
    public Totals calculate(List<SaleLine> lines){
        if(lines.isEmpty()) throw new IllegalArgumentException("Add at least one item");
        BigDecimal s=money(lines.stream().map(SaleLine::amount).reduce(BigDecimal.ZERO,BigDecimal::add));
        BigDecimal d=money(s.compareTo(threshold)>=0?s.multiply(discountRate):BigDecimal.ZERO);
        BigDecimal t=money(s.subtract(d).multiply(taxRate));return new Totals(s,d,t,s.subtract(d).add(t));
    }
}
