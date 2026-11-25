package pearlmart.core;
import java.util.*;
/** Structural Facade: the UI sees workflows, not repository writes. */
public final class PearlMartFacade {
    private final Ledger ledger=new Ledger(); private final StockService stock=new StockService(ledger); private final CheckoutService checkout;
    public PearlMartFacade(PricingPolicy policy){checkout=new CheckoutService(ledger,stock,policy);}
    public Optional<InventoryItem> find(String key){return stock.find(key);}public InventoryItem register(InventoryItem i){return stock.register(i);}public InventoryItem restock(String key,int q){return stock.restock(key,q);}
    public Sale start(){return checkout.start();}public Sale sale(String id){return checkout.get(id);}public Sale addLine(String id,String key,int q){return checkout.addLine(id,key,q);}public Sale removeLine(String id,String itemId){return checkout.removeLine(id,itemId);}public Totals quote(String id){return checkout.quote(id);}public SalesReceipt confirm(String id){return checkout.confirm(id);}public Sale cancel(String id){return checkout.cancel(id);}
    public List<InventoryItem> items(){return List.copyOf(ledger.snapshot().items().values());}public List<InventoryMovement> movements(){return ledger.snapshot().movements();}public List<Sale> sales(){return List.copyOf(ledger.snapshot().sales().values());}
}
