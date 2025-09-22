package pepgames.plugins.pepbazaar.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pepgames.plugins.pepbazaar.Product;

public class ProductRemoveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter Player remover;
    @Getter Product product;

    public ProductRemoveEvent(Player remover , Product product) {
        this.remover = remover; this.product = product;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
