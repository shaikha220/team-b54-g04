package pearlmart.core;
import java.time.Instant;
public record InventoryMovement(String reference, String itemId, int delta, int balance, Instant time) {}
