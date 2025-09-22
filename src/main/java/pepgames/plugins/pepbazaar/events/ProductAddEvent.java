package pepgames.plugins.pepbazaar.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pepgames.plugins.pepbazaar.Product;

public final class ProductAddEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter Player seller;
    @Getter Product product;

    public ProductAddEvent(Player seller , Product product) {
        this.seller = seller; this.product = product;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
