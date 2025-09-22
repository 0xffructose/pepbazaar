package pepgames.plugins.pepbazaar;

import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class Product {

    public boolean isAccepted = false;

    @Getter private UUID productUUID;
    public UUID productSeller;
    @Getter public ItemStack productItem;
    public Integer productPrice = 0;

    public GuiItem productGuiItem , profileGuiItem;

    public Product() {
        productUUID = UUID.randomUUID();
    }

    public Product(UUID productSeller , ItemStack productItem , Integer productPrice) {
        this.productSeller = productSeller; this.productItem = productItem;
        this.productPrice = productPrice;
    }

}
