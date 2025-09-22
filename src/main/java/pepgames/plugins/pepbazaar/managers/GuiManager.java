package pepgames.plugins.pepbazaar.managers;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import pepgames.plugins.TextUtil;
import pepgames.plugins.pepbazaar.PepBazaar;
import pepgames.plugins.pepbazaar.Product;
import pepgames.plugins.pepbazaar.events.ProductAddEvent;
import pepgames.plugins.pepbazaar.events.ProductEditEvent;
import pepgames.plugins.pepbazaar.events.ProductRemoveEvent;
import pepgames.plugins.pepbazaar.events.ProductSoldEvent;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GuiManager {

    private PepBazaar plugin;

    private final String TITLE = "title" , ROWS = "rows" , PAGE_SIZE = "page-size" , BUTTONS = "buttons" , NAME = "name" , LORE = "lore" , MATERIAL = "material" , CUSTOM_MODEL_DATA = "model_data" , BOTTOM_FILLER = "bottom-filler"
                       , SLOT = "slot" , FROM = "from" , TO = "to";

    public GuiManager(PepBazaar plugin) {

        this.plugin = plugin;

    }

    //region Edit GUI
    public final Gui parseEditGui(FileConfiguration config , String path , Product product) {

        Component title = TextUtil.format( config.getString(path + "." + TITLE) );
        Integer rows = config.getInt(path + "." + ROWS);

        Gui editGui = Gui.gui().title( title ).rows( rows ).create();
        editGui.disableItemPlace(); editGui.disableItemSwap();

        Object[] backBtn = parseButton(config , path + "." + BUTTONS + ".back-btn" , null);
        GuiItem backItem = ((GuiItem) backBtn[0]); backItem.setAction(event -> {
            event.setCancelled(true);
            plugin.getBazaarManager().getBazaarVendors().get( event.getWhoClicked().getUniqueId() ).open( event.getWhoClicked() );
        });
        editGui.setItem( ((Integer) backBtn[1]) , backItem );

        Integer placeholderSlot = config.getInt(path + "." + BUTTONS + ".item-placeholder." + SLOT);
        GuiItem placeholderItem = ItemBuilder.from( product.productGuiItem.getItemStack() ).asGuiItem(event -> event.setCancelled(true));
        editGui.setItem( placeholderSlot , placeholderItem );

        // MINUS
        /*
        Object[] minusFiveBtn = parseButton(config , path + "." + BUTTONS + ".minus-five-btn" , null);
        GuiItem minusFiveItem = ((GuiItem) minusFiveBtn[0]); minusFiveItem.setAction(event -> {
            event.setCancelled(true);

            if ( !(product.productPrice - 5 < 0) ) {

                product.productPrice -= 5;

                ItemMeta bazaarItemMeta = placeholderItem.getItemStack().getItemMeta();
                bazaarItemMeta.setLore( Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + product.productPrice) ) );
                placeholderItem.getItemStack().setItemMeta(bazaarItemMeta);

                SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.decreaseSound);

                editGui.update();
                return;
            }

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.unsuccessfulSound);

        });
        editGui.setItem( ((Integer) minusFiveBtn[1]) , minusFiveItem );
        */

        Object[] minusOneBtn = parseButton(config , path + "." + BUTTONS + ".minus-one-btn" , null);
        GuiItem minusOneItem = ((GuiItem) minusOneBtn[0]); minusOneItem.setAction(event -> {
            event.setCancelled(true);

            if ( !(product.productPrice - 1 < 0) ) {

                product.productPrice -= 1;

                ItemMeta bazaarItemMeta = placeholderItem.getItemStack().getItemMeta();
                bazaarItemMeta.setLore( Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + product.productPrice) ) );
                placeholderItem.getItemStack().setItemMeta(bazaarItemMeta);

                SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.decreaseSound);

                editGui.update();
                return;
            }

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.unsuccessfulSound);

        });
        editGui.setItem( ((Integer) minusOneBtn[1]) , minusOneItem );

        // PLUS
        Object[] plusOneBtn = parseButton(config , path + "." + BUTTONS + ".plus-one-btn" , null);
        GuiItem plusOneItem = ((GuiItem) plusOneBtn[0]); plusOneItem.setAction(event -> {
            event.setCancelled(true);

            product.productPrice += 1;

            ItemMeta bazaarItemMeta = placeholderItem.getItemStack().getItemMeta();
            bazaarItemMeta.setLore( Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + product.productPrice) ) );
            placeholderItem.getItemStack().setItemMeta(bazaarItemMeta);

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.increaseSound);

            editGui.update();
        });
        editGui.setItem( ((Integer) plusOneBtn[1]) , plusOneItem );

        /*
        Object[] plusFiveBtn = parseButton(config , path + "." + BUTTONS + ".plus-five-btn" , null);
        GuiItem plusFiveItem = ((GuiItem) plusFiveBtn[0]); plusFiveItem.setAction(event -> {
            event.setCancelled(true);

            product.productPrice += 5;

            ItemMeta bazaarItemMeta = placeholderItem.getItemStack().getItemMeta();
            bazaarItemMeta.setLore( Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + product.productPrice) ) );
            placeholderItem.getItemStack().setItemMeta(bazaarItemMeta);

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.increaseSound);

            editGui.update();
        });
        editGui.setItem( ((Integer) plusFiveBtn[1]) , plusFiveItem );
        */

        // DELETE
        Object[] deleteBtn = parseButton(config , path + "." + BUTTONS + ".delete-btn" , null);
        GuiItem deleteItem = ((GuiItem) deleteBtn[0]); deleteItem.setAction(event -> {
            event.setCancelled(true);

            Bukkit.getPluginManager().callEvent(new ProductRemoveEvent( (Player) event.getWhoClicked() , product ));

            editGui.update();
        });
        editGui.setItem( ((Integer) deleteBtn[1]) , deleteItem );

        // ACCEPT
        Object[] acceptBtn = parseButton(config , path + "." + BUTTONS + ".accept-btn" , null);
        GuiItem acceptItem = ((GuiItem) acceptBtn[0]); acceptItem.setAction(event -> {
            event.setCancelled(true);

            if (product.productPrice == 0) return;

            Bukkit.getPluginManager().callEvent(new ProductEditEvent((Player) event.getWhoClicked() , product));

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.successfulSound);

            event.getWhoClicked().closeInventory();
        });
        editGui.setItem( ((Integer) acceptBtn[1]) , acceptItem );

        return editGui;
    }
    //endregion

    public final PaginatedGui parseBazaar(FileConfiguration config , String path) {

        Component title = TextUtil.format( config.getString(path + "." + TITLE) );
        Integer rows = config.getInt(path + "." + ROWS );
        Integer pageSize = config.getInt(path + "." + PAGE_SIZE);

        Material bottomMaterial = Material.valueOf( config.getString(path + "." + BOTTOM_FILLER) );

        PaginatedGui bazaarGui = Gui.paginated().title( title ).rows( rows ).pageSize( pageSize ).create();
        bazaarGui.getFiller().fillBottom( ItemBuilder.from(bottomMaterial).asGuiItem( event -> event.setCancelled(true)) );
        bazaarGui.disableItemPlace(); bazaarGui.disableItemTake(); bazaarGui.disableItemSwap();

        Object[] previousBtn = parseButton(config , path + "." + BUTTONS + ".previous-btn" , null);
        GuiItem previousItem = ((GuiItem) previousBtn[0]); previousItem.setAction(event -> { event.setCancelled(true); bazaarGui.previous(); });
        bazaarGui.setItem( ((Integer) previousBtn[1]) , previousItem );

        Object[] nextBtn = parseButton(config , path + "." + BUTTONS + ".next-btn" , null);
        GuiItem nextItem = ((GuiItem) nextBtn[0]); nextItem.setAction(event -> { event.setCancelled(true); bazaarGui.next(); });
        bazaarGui.setItem( ((Integer) nextBtn[1]) , nextItem );

        Object[] sellBtn = parseButton(config , path + "." + BUTTONS + ".sell-btn" , null);
        GuiItem sellItem = ((GuiItem) sellBtn[0]); sellItem.setAction(event -> {
            event.setCancelled(true);

            if (!event.getWhoClicked().hasPermission("pep.world.premium")) {

                plugin.adventure().player((Player) event.getWhoClicked()).sendMessage(
                    TextUtil.format( plugin.getConfigManager().getConfig("messages").getString("messages.only-premium") )
                );

                return;
            }

            parseSellGui( config , "sell-gui").open( event.getWhoClicked() );
        });
        bazaarGui.setItem( ((Integer) sellBtn[1]) , sellItem );

        Object[] profileBtn = parseButton(config , path + "." + BUTTONS + ".profile-btn" , null);
        GuiItem profileItem = ((GuiItem) profileBtn[0]); profileItem.setAction(event -> {
            event.setCancelled(true);

            if ( plugin.getBazaarManager().getBazaarVendors().containsKey(event.getWhoClicked().getUniqueId()) )
                plugin.getBazaarManager().getBazaarVendors().get( event.getWhoClicked().getUniqueId() ).open( event.getWhoClicked() );
            else {

                PaginatedGui profileGui = parseProfileGui(config , "profile-gui" , event.getWhoClicked().getUniqueId());

                if ( plugin.getBazaarManager().getBazaarProducts().get( event.getWhoClicked().getUniqueId() ) != null && !plugin.getBazaarManager().getBazaarProducts().get( event.getWhoClicked().getUniqueId() ).isEmpty() && !plugin.getBazaarManager().getBazaarProducts().containsKey( event.getWhoClicked().getUniqueId() ) ) {
                    for (Product product : plugin.getBazaarManager().getBazaarProducts().get(event.getWhoClicked().getUniqueId())) {

                        product.profileGuiItem = ItemBuilder.from(product.productGuiItem.getItemStack()).asGuiItem(e -> {
                            e.setCancelled(true);

                            plugin.getGuiManager().parseEditGui( plugin.getConfigManager().getConfig("gui") , "edit-gui" , product ).open( e.getWhoClicked() );
                        });
                        profileGui.addItem( product.profileGuiItem );
                    }
                }

                plugin.getBazaarManager().getBazaarVendors().put( event.getWhoClicked().getUniqueId() , profileGui );
                profileGui.open( event.getWhoClicked() );
            }

        });
        bazaarGui.setItem( ((Integer) profileBtn[1]) , profileItem );

        return bazaarGui;
    }

    public final PaginatedGui parseProfileGui(FileConfiguration config , String path , UUID playerUUID) {

        Component title = TextUtil.format( config.getString(path + "." + TITLE) );
        Integer rows = config.getInt(path + "." + ROWS );
        Integer pageSize = config.getInt(path + "." + PAGE_SIZE);

        Material bottomMaterial = Material.valueOf( config.getString(path + "." + BOTTOM_FILLER) );

        PaginatedGui profileGui = Gui.paginated().title( title ).rows( rows ).pageSize( pageSize ).create();
        profileGui.getFiller().fillBottom( ItemBuilder.from(bottomMaterial).asGuiItem( event -> event.setCancelled(true)) );
        profileGui.disableItemPlace(); profileGui.disableItemTake(); profileGui.disableItemSwap();

        Object[] backBtn = parseButton(config , path + "." + BUTTONS + ".back-btn" , null);
        GuiItem backItem = ((GuiItem) backBtn[0]); backItem.setAction(event -> {
            event.setCancelled(true);

            plugin.getBazaarManager().getBazaarGui().open( event.getWhoClicked() );
        });
        profileGui.setItem( ((Integer) backBtn[1]) , backItem );

        Object[] previousBtn = parseButton(config , path + "." + BUTTONS + ".previous-btn" , null);
        GuiItem previousItem = ((GuiItem) previousBtn[0]); previousItem.setAction(event -> { event.setCancelled(true); profileGui.previous(); });
        profileGui.setItem( ((Integer) previousBtn[1]) , previousItem );

        Object[] nextBtn = parseButton(config , path + "." + BUTTONS + ".next-btn" , null);
        GuiItem nextItem = ((GuiItem) nextBtn[0]); nextItem.setAction(event -> { event.setCancelled(true); profileGui.next(); });
        profileGui.setItem( ((Integer) nextBtn[1]) , nextItem );

        return profileGui;
    }

    public final Gui parseSellGui(FileConfiguration config , String path) {

        Component title = TextUtil.format( config.getString(path + "." + TITLE) );
        Integer rows = config.getInt(path + "." + ROWS);

        Gui sellGui = Gui.gui().title( title ).rows( rows ).create();
        sellGui.disableItemPlace(); sellGui.disableItemSwap();

        Product product = new Product();

        sellGui.setOpenGuiAction(event -> {

            plugin.getBazaarManager().getBazaarProducts().computeIfAbsent(event.getPlayer().getUniqueId(), k -> new ArrayList<>());
            product.productSeller = event.getPlayer().getUniqueId();

            plugin.getBazaarManager().getBazaarProducts().get(event.getPlayer().getUniqueId()).add(product);

        });

        sellGui.setCloseGuiAction(event -> {

            if (!product.isAccepted && product.productItem != null)
                event.getPlayer().getInventory().addItem( product.productItem );

            plugin.getBazaarManager().getBazaarProducts().get(event.getPlayer().getUniqueId()).remove(product);

        });

        Object[] backBtn = parseButton(config , path + "." + BUTTONS + ".back-btn" , null);
        GuiItem backItem = ((GuiItem) backBtn[0]); backItem.setAction(event -> {
            event.setCancelled(true);
            plugin.getBazaarManager().getBazaarGui().open( event.getWhoClicked() );
        });
        sellGui.setItem( ((Integer) backBtn[1]) , backItem );

        Object[] placeholderBtn = parseButton(config , path + "." + BUTTONS + ".item-placeholder" , null);
        Material initialMaterial = ((GuiItem) placeholderBtn[0]).getItemStack().getType();
        GuiItem placeholderItem = ((GuiItem) placeholderBtn[0]); placeholderItem.setAction(event -> {
            event.setCancelled(true);

            if ( !event.getCursor().getType().equals( Material.AIR ) ) {

                if ( !sellGui.getGuiItem( event.getSlot() ).getItemStack().getType().equals( initialMaterial ) )
                    event.getWhoClicked().getInventory().addItem( product.productItem );

                product.productItem = event.getCursor().clone();
                product.productGuiItem = ItemBuilder.from( event.getCursor().clone() ).lore( Arrays.asList( TextUtil.format("<gray>Fiyat : <white>" + product.productPrice) , TextUtil.format("<gray>Satıcı : <white>" + Bukkit.getOfflinePlayer(product.productSeller).getName()) ) ).asGuiItem();

                sellGui.getGuiItem( event.getSlot() ).setItemStack( product.productGuiItem.getItemStack() );
                event.getWhoClicked().setItemOnCursor( null );
                
                sellGui.update();
            }
            
        });
        sellGui.setItem( ((Integer) placeholderBtn[1]) , placeholderItem );

        // MINUS
        Object[] minusOneBtn = parseButton(config , path + "." + BUTTONS + ".minus-one-btn" , null);
        GuiItem minusOneItem = ((GuiItem) minusOneBtn[0]); minusOneItem.setAction(event -> {
            event.setCancelled(true);

            if (placeholderItem.getItemStack().getType().equals( initialMaterial )) return;

            if ( !(product.productPrice - 1 < 0) ) {

                product.productPrice -= 1;

                ItemMeta bazaarItemMeta = placeholderItem.getItemStack().getItemMeta();
                bazaarItemMeta.setLore(  Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + product.productPrice) , TextUtil.formatWithLegacy("&7Satıcı : &f" + Bukkit.getOfflinePlayer(product.productSeller).getName()) ));
                placeholderItem.getItemStack().setItemMeta(bazaarItemMeta);

                SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.decreaseSound);

                sellGui.update();
                return;
            }

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.unsuccessfulSound);

        });
        sellGui.setItem( ((Integer) minusOneBtn[1]) , minusOneItem );

        // PLUS
        Object[] plusOneBtn = parseButton(config , path + "." + BUTTONS + ".plus-one-btn" , null);
        GuiItem plusOneItem = ((GuiItem) plusOneBtn[0]); plusOneItem.setAction(event -> {
            event.setCancelled(true);

            if (placeholderItem.getItemStack().getType().equals( initialMaterial )) return;

            product.productPrice += 1;

            ItemMeta bazaarItemMeta = placeholderItem.getItemStack().getItemMeta();
            bazaarItemMeta.setLore(  Arrays.asList( TextUtil.formatWithLegacy("&7Fiyat : &f" + product.productPrice) , TextUtil.formatWithLegacy("&7Satıcı : &f" + Bukkit.getOfflinePlayer(product.productSeller).getName()) ));
            placeholderItem.getItemStack().setItemMeta(bazaarItemMeta);

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.increaseSound);

            sellGui.update();
        });
        sellGui.setItem( ((Integer) plusOneBtn[1]) , plusOneItem );

        // ACCEPT
        Object[] acceptBtn = parseButton(config , path + "." + BUTTONS + ".accept-btn" , null);
        GuiItem acceptItem = ((GuiItem) acceptBtn[0]); acceptItem.setAction(event -> {
            event.setCancelled(true);

            if (product.productPrice == 0) return;
            if (placeholderItem.getItemStack().getType().equals( initialMaterial )) return;

            product.productGuiItem = placeholderItem;

            Bukkit.getPluginManager().callEvent(new ProductAddEvent((Player) event.getWhoClicked() , product));

            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.successfulSound);
            plugin.adventure().player( (Player) event.getWhoClicked() ).sendMessage(
                    TextUtil.format( plugin.getConfigManager().getConfig("messages").getString("messages.successfuly-listed") )
            );

        });
        sellGui.setItem( ((Integer) acceptBtn[1]) , acceptItem );

        return sellGui;
    }

    public final Gui parseBuyGui(FileConfiguration config , String path , Product product) {

        Component title = TextUtil.format( config.getString(path + "." + TITLE) );
        Integer rows = config.getInt(path + "." + ROWS);

        Gui buyGui = Gui.gui().title( title ).rows( rows ).create();
        buyGui.disableItemPlace(); buyGui.disableItemSwap();

        Object[] backBtn = parseButton(config , path + "." + BUTTONS + ".back-btn" , null);
        GuiItem backItem = ((GuiItem) backBtn[0]); backItem.setAction(event -> {
            event.setCancelled(true);
            plugin.getBazaarManager().getBazaarGui().open( event.getWhoClicked() );
        });
        buyGui.setItem( ((Integer) backBtn[1]) , backItem );

        Integer placeholderSlot = config.getInt(path + "." + BUTTONS + ".item-placeholder." + SLOT);
        GuiItem placeholderItem = ItemBuilder.from( product.productGuiItem.getItemStack() ).asGuiItem(event -> event.setCancelled(true));
        buyGui.setItem( placeholderSlot , placeholderItem );

        // ACCEPT
        Object[] acceptBtn = parseButton(config , path + "." + BUTTONS + ".accept-btn" , null);
        GuiItem acceptItem = ((GuiItem) acceptBtn[0]); acceptItem.setAction(event -> {
            event.setCancelled(true);

            Bukkit.getPluginManager().callEvent(new ProductSoldEvent((Player) event.getWhoClicked() , product.productSeller , product));
            SoundManager.playSound((Player) event.getWhoClicked() , SoundManager.successfulSound);
        });
        buyGui.setItem( ((Integer) acceptBtn[1]) , acceptItem );

        return buyGui;
    }

    public final Gui parseAuthGui(FileConfiguration config , String path , BaseGui backGui , Product product , Consumer<Product> verifyProcess) {

        Component title = TextUtil.format( config.getString(path + "." + TITLE) );
        Integer rows = config.getInt(path + "." + ROWS );

        Gui authGui = Gui.gui().title( title ).rows( rows ).create();
        authGui.disableItemPlace(); authGui.disableItemTake(); authGui.disableItemSwap();

        Object[] noBtn = parseButton(config , path + "." + BUTTONS + ".no-btn" , null);
        GuiItem noItem = ((GuiItem) noBtn[0]); noItem.setAction(event -> {
            event.setCancelled(true);

            backGui.open( event.getWhoClicked() );
        });
        authGui.setItem( ((Integer) noBtn[1]) , noItem );

        Object[] yesBtn = parseButton(config , path + "." + BUTTONS + ".yes-btn" , null);
        GuiItem yesItem = ((GuiItem) yesBtn[0]); yesItem.setAction(event -> {
            event.setCancelled(true);

            verifyProcess.accept( product );
            backGui.open( event.getWhoClicked() );
        });
        authGui.setItem( ((Integer) yesBtn[1]) , yesItem );

        return authGui;
    }

    public final Object[] parseButton(FileConfiguration config , String path , @Nullable Player player) {

        Component name = TextUtil.format( config.getString(path + "." + NAME) );
        List<Component> lore = config.getStringList(path + "." + LORE).stream().filter(Objects::nonNull).map(TextUtil::format).collect(Collectors.toList());
        Material material = Material.valueOf( config.getString(path + "." + MATERIAL) );
        Integer modelData = config.getInt(path + "." + CUSTOM_MODEL_DATA);

        if ( config.get(path + "." + SLOT) != null ) {

            if ( material.equals(Material.PLAYER_HEAD) ) {
                GuiItem button = ItemBuilder.skull().owner(player).name( name ).lore( lore ).model(modelData).asGuiItem();

                Integer slot = config.getInt(path + "." + SLOT);
                Object[] objects = new Object[2];
                objects[0] = button; objects[1] = slot;

                return objects;

            } else {
                GuiItem button = ItemBuilder.from( material ).name( name ).lore( lore ).model(modelData).asGuiItem();

                Integer slot = config.getInt(path + "." + SLOT);
                Object[] objects = new Object[2];
                objects[0] = button; objects[1] = slot;

                return objects;
            }

        }
        else
        {

            if ( material.equals(Material.PLAYER_HEAD) ) {
                GuiItem button = ItemBuilder.skull().owner(player).name( name ).lore( lore ).model(modelData).asGuiItem();

                Integer from = config.getInt(path + "." + FROM) , to = config.getInt(path + "." + TO);
                Object[] objects = new Object[3];
                objects[0] = button; objects[1] = from; objects[2] = to;

                return objects;
            } else {
                GuiItem button = ItemBuilder.from( material ).name( name ).lore( lore ).model(modelData).asGuiItem();

                Integer from = config.getInt(path + "." + FROM) , to = config.getInt(path + "." + TO);
                Object[] objects = new Object[3];
                objects[0] = button; objects[1] = from; objects[2] = to;

                return objects;
            }

        }

    }



}
