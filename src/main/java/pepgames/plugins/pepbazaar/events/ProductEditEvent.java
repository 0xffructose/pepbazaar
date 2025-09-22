package pepgames.plugins.pepbazaar.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pepgames.plugins.pepbazaar.Product;

public class ProductEditEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter Player editor;
    @Getter Product product;

    public ProductEditEvent(Player editor , Product product) {
        this.editor = editor; this.product = product;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
