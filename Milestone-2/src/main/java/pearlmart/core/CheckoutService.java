package pearlmart.core;
import java.time.Instant;
import java.util.*;
public final class CheckoutService {
    private final Ledger ledger;private final StockService stock;private final PricingPolicy policy;
    public CheckoutService(Ledger ledger,StockService stock,PricingPolicy policy){this.ledger=ledger;this.stock=stock;this.policy=Objects.requireNonNull(policy);}
    public Sale start(){Sale s=new Sale(UUID.randomUUID().toString(),Sale.Status.PENDING,List.of(),null);return store(s);}
    public Sale get(String id){Sale s=ledger.snapshot().sales().get(id);if(s==null)throw new IllegalArgumentException("Sale not found");return s;}
    private Sale pending(String id){Sale s=get(id);if(s.status()!=Sale.Status.PENDING)throw new IllegalStateException("Sale is already "+s.status());return s;}
    public Sale addLine(String id,String key,int q){Sale s=pending(id);if(q<=0)throw new IllegalArgumentException("Quantity must be positive");InventoryItem item=stock.find(key).orElseThrow(()->new IllegalArgumentException("Unknown item"));List<SaleLine> lines=new ArrayList<>(s.lines());int index=-1;for(int j=0;j<lines.size();j++)if(lines.get(j).itemId().equals(item.id()))index=j;int total=index<0?q:Math.addExact(q,lines.get(index).quantity());if(total>item.quantity())throw new IllegalArgumentException("Insufficient stock: "+item.quantity()+" available");SaleLine l=new SaleLine(item.id(),item.name(),index<0?item.price():lines.get(index).unitPrice(),total);if(index<0)lines.add(l);else lines.set(index,l);return store(new Sale(id,s.status(),lines,null));}
    public Sale removeLine(String id,String itemId){Sale s=pending(id);return store(new Sale(id,s.status(),s.lines().stream().filter(l->!l.itemId().equals(itemId)).toList(),null));}
    public Totals quote(String id){return policy.calculate(pending(id).lines());}
    public SalesReceipt confirm(String id){Sale s=pending(id);Totals totals=quote(id);Ledger.State old=ledger.snapshot();Map<String,InventoryItem> items=new LinkedHashMap<>(old.items());List<InventoryMovement> moves=new ArrayList<>(old.movements());Instant now=Instant.now();for(SaleLine l:s.lines()){InventoryItem i=items.get(l.itemId());if(i==null||i.quantity()<l.quantity())throw new IllegalStateException("Stock changed for "+l.name());InventoryItem changed=i.withQuantity(i.quantity()-l.quantity());items.put(i.id(),changed);moves.add(new InventoryMovement(id,i.id(),-l.quantity(),changed.quantity(),now));}SalesReceipt receipt=new SalesReceipt(id,now,s.lines(),totals);Map<String,Sale> sales=new LinkedHashMap<>(old.sales());sales.put(id,new Sale(id,Sale.Status.COMPLETED,s.lines(),receipt));ledger.commit(old,new Ledger.State(items,moves,sales));return receipt;}
    public Sale cancel(String id){Sale s=pending(id);return store(new Sale(id,Sale.Status.CANCELLED,s.lines(),null));}
    private Sale store(Sale s){Ledger.State old=ledger.snapshot();Map<String,Sale> sales=new LinkedHashMap<>(old.sales());sales.put(s.id(),s);ledger.commit(old,new Ledger.State(old.items(),old.movements(),sales));return s;}
}
