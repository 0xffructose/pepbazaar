package pepgames.plugins.pepbazaar.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pepgames.plugins.pepbazaar.Product;

import java.util.UUID;

public class ProductSoldEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter Player buyer;
    @Getter UUID vendor;
    @Getter Product product;

    public ProductSoldEvent(Player buyer , UUID vendor , Product product) {
        this.buyer = buyer; this.vendor = vendor; this.product = product;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
