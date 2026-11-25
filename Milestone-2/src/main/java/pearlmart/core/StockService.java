package pearlmart.core;
import java.time.Instant;
import java.util.*;
public final class StockService {
    private final Ledger ledger;
    public StockService(Ledger ledger){this.ledger=ledger;}
    public Optional<InventoryItem> find(String key){String k=InventoryItem.required(key,"Identifier");return ledger.snapshot().items().values().stream().filter(i->i.id().equalsIgnoreCase(k)||i.barcode().equalsIgnoreCase(k)).findFirst();}
    public InventoryItem register(InventoryItem item){
        if(item.quantity()<=0)throw new IllegalArgumentException("Quantity must be positive");
        if(find(item.id()).isPresent()||find(item.barcode()).isPresent())throw new IllegalArgumentException("Duplicate ID or barcode");
        return save(item,item.quantity());
    }
    public InventoryItem restock(String key,int quantity){if(quantity<=0)throw new IllegalArgumentException("Quantity must be positive"); InventoryItem i=find(key).orElseThrow(()->new IllegalArgumentException("Item not found"));return save(i.withQuantity(Math.addExact(i.quantity(),quantity)),quantity);}
    private InventoryItem save(InventoryItem item,int delta){Ledger.State old=ledger.snapshot();Map<String,InventoryItem> items=new LinkedHashMap<>(old.items());items.put(item.id(),item);List<InventoryMovement> moves=new ArrayList<>(old.movements());moves.add(new InventoryMovement(UUID.randomUUID().toString(),item.id(),delta,item.quantity(),Instant.now()));ledger.commit(old,new Ledger.State(items,moves,old.sales()));return item;}
}
