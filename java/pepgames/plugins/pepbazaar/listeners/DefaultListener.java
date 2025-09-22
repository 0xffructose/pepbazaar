package pepgames.plugins.pepbazaar.listeners;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.ItemMeta;
import pepgames.plugins.TextUtil;
import pepgames.plugins.pepbazaar.PepBazaar;
import pepgames.plugins.pepbazaar.Product;
import pepgames.plugins.pepbazaar.events.ProductAddEvent;
import pepgames.plugins.pepbazaar.events.ProductEditEvent;
import pepgames.plugins.pepbazaar.events.ProductRemoveEvent;
import pepgames.plugins.pepbazaar.events.ProductSoldEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

public final class DefaultListener implements Listener {

    private PepBazaar plugin;

    public DefaultListener(PepBazaar plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this , plugin);
    }

    @EventHandler
    public void onProductAdded(ProductAddEvent event) {

        event.getProduct().isAccepted = true;

        event.getProduct().productGuiItem.setAction(e -> {
            e.setCancelled(true);

            if ( event.getSeller().getUniqueId().equals( e.getWhoClicked().getUniqueId() )) return;
            if ( plugin.getWalletAPI().getCoins( e.getWhoClicked().getUniqueId() ) < event.getProduct().productPrice ) return;

            plugin.getGuiManager().parseBuyGui( plugin.getConfigManager().getConfig("gui") , "buy-gui" , event.getProduct() ).open( e.getWhoClicked() );
        });

        if ( plugin.getBazaarManager().getBazaarVendors().containsKey( event.getSeller().getUniqueId() )) {

            event.getProduct().profileGuiItem = ItemBuilder.from(event.getProduct().productGuiItem.getItemStack()).asGuiItem(e -> {
                e.setCancelled(true);

                plugin.getGuiManager().parseEditGui( plugin.getConfigManager().getConfig("gui") , "edit-gui" , event.getProduct() ).open( e.getWhoClicked() );
            });
            plugin.getBazaarManager().getBazaarVendors().get( event.getSeller().getUniqueId() ).addItem( event.getProduct().profileGuiItem );

        } else {

            PaginatedGui profileGui = plugin.getGuiManager().parseProfileGui( plugin.getConfigManager().getConfig("gui")  , "profile-gui" , event.getSeller().getUniqueId() );

            if ( !plugin.getBazaarManager().getBazaarProducts().get( event.getSeller().getUniqueId() ).isEmpty() ) {
                for (Product product : plugin.getBazaarManager().getBazaarProducts().get( event.getSeller().getUniqueId() )) {

                    event.getProduct().profileGuiItem = ItemBuilder.from(product.productGuiItem.getItemStack()).asGuiItem(e -> {
                        e.setCancelled(true);

                        plugin.getGuiManager().parseEditGui( plugin.getConfigManager().getConfig("gui") , "edit-gui" , product ).open( e.getWhoClicked() );
                    });
                    profileGui.addItem(event.getProduct().profileGuiItem);
                }
            }

            plugin.getBazaarManager().getBazaarVendors().put( event.getSeller().getUniqueId() , profileGui );

        }

        if (plugin.getBazaarManager().getBazaarProducts().containsKey( event.getSeller().getUniqueId() )) plugin.getBazaarManager().getBazaarProducts().get( event.getSeller().getUniqueId() ).add( event.getProduct() );
        else plugin.getBazaarManager().getBazaarProducts().put( event.getSeller().getUniqueId() , new ArrayList<>() {{ add( event.getProduct() ); }} );
        
        plugin.getBazaarManager().getBazaarGui().addItem( event.getProduct().productGuiItem );
        event.getSeller().closeInventory();

    }

    @EventHandler
    public void onProductRemoved(ProductRemoveEvent event) {

        Consumer<Product> consumer = product -> {

            event.getRemover().getInventory().addItem( event.getProduct().productItem );
            plugin.getBazaarManager().getBazaarVendors().get( event.getRemover().getUniqueId() ).removePageItem( event.getProduct().profileGuiItem );
            plugin.getBazaarManager().getBazaarGui().removePageItem( event.getProduct().productGuiItem );
            plugin.getBazaarManager().getBazaarProducts().get( event.getRemover().getUniqueId() ).remove( event.getProduct() );

        };

        plugin.getGuiManager().parseAuthGui( plugin.getConfigManager().getConfig("gui") , "auth-gui" , plugin.getBazaarManager().getBazaarVendors().get( event.getRemover().getUniqueId() ) , event.getProduct() , consumer).open( event.getRemover() );

    }

    @EventHandler
    public void onProductSold(ProductSoldEvent event) {

        plugin.getWalletAPI().removeFrom( event.getBuyer().getUniqueId() , -event.getProduct().productPrice );
        plugin.getWalletAPI().addTo( event.getVendor() , event.getProduct().productPrice );

        event.getBuyer().getInventory().addItem( event.getProduct().productItem );

        // NOTIFICATION
        /*
        plugin.adventure().player( event.getVendor() ).sendMessage(
                TextUtil.format( plugin.getConfigManager().getConfig("messages").getString("messages.product-sold").replace("%product_name%" , event.getProduct().getProductItem().getItemMeta().getLocalizedName()) )
        );
        */

        plugin.getBazaarManager().getBazaarGui().removePageItem( event.getProduct().productGuiItem );
        plugin.getBazaarManager().getBazaarVendors().get( event.getVendor() ).removePageItem( event.getProduct().profileGuiItem );

        plugin.getBazaarManager().getBazaarGui().update();

        plugin.getBazaarManager().getBazaarProducts().get( event.getVendor() ).remove( event.getProduct() );

        event.getBuyer().closeInventory();

    }

    @EventHandler
    public void onProductEdited(ProductEditEvent event) {

        ItemMeta guiItemMeta = event.getProduct().productGuiItem.getItemStack().getItemMeta();
        guiItemMeta.setLore( Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + event.getProduct().productPrice) ) );
        event.getProduct().productGuiItem.getItemStack().setItemMeta(guiItemMeta);

        ItemMeta profileGuiItemMeta = event.getProduct().profileGuiItem.getItemStack().getItemMeta();
        profileGuiItemMeta.setLore( Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + event.getProduct().productPrice) ) );
        event.getProduct().profileGuiItem.getItemStack().setItemMeta(profileGuiItemMeta);

    }

}
