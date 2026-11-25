package pearlmart.core;
import java.util.*;
/** Single immutable snapshot. Commit publishes stock and records together. */
public final class Ledger {
    public record State(Map<String,InventoryItem> items,List<InventoryMovement> movements,Map<String,Sale> sales) {
        public State {items=Collections.unmodifiableMap(new LinkedHashMap<>(items)); movements=List.copyOf(movements); sales=Collections.unmodifiableMap(new LinkedHashMap<>(sales));}
    }
    private State state=new State(Map.of(),List.of(),Map.of());
    public State snapshot(){return state;}
    public void commit(State expected,State next){if(state!=expected)throw new IllegalStateException("Inventory changed; retry");state=next;}
}
