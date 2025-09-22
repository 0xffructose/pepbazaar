package pepgames.plugins.pepbazaar.managers;

import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import pepgames.plugins.pepbazaar.PepBazaar;
import pepgames.plugins.pepbazaar.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BazaarManager {

    private PepBazaar plugin;
    @Getter private PaginatedGui bazaarGui;

    @Getter private Map<UUID , List<Product>> bazaarProducts;
    @Getter private Map<UUID , PaginatedGui> bazaarVendors;

    public BazaarManager(PepBazaar plugin) {

        this.plugin = plugin;
        bazaarProducts = new HashMap<>();
        bazaarVendors = new HashMap<>();

        bazaarGui = plugin.getGuiManager().parseBazaar( plugin.getConfigManager().getConfig("gui") , "bazaar-gui" );
        
    }

}
