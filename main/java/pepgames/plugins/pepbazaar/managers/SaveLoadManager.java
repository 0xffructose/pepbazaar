package pepgames.plugins.pepbazaar.managers;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import pepgames.plugins.TextUtil;
import pepgames.plugins.pepbazaar.PepBazaar;
import pepgames.plugins.pepbazaar.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class SaveLoadManager {

    private PepBazaar plugin;
    private File bazaarFolder;

    public SaveLoadManager(PepBazaar plugin) {
        this.plugin = plugin;

        bazaarFolder = new File(plugin.getDataFolder() , "./bazaar/");
        if (!bazaarFolder.exists()) { bazaarFolder.mkdirs(); }

    }

    public final void loadBazaar() {

        for (File bazaarProfile : Objects.requireNonNull(bazaarFolder.listFiles())) {

            FileConfiguration bazaarProfileConfig = YamlConfiguration.loadConfiguration( bazaarProfile );
            if (bazaarProfileConfig.getKeys(false).isEmpty()) continue;
            String uuid = bazaarProfileConfig.getKeys(false).iterator().next();

            if (!plugin.getBazaarManager().getBazaarProducts().containsKey( UUID.fromString( uuid ) )) {
                plugin.getBazaarManager().getBazaarProducts().put(UUID.fromString(uuid) , new ArrayList<>());
                plugin.getBazaarManager().getBazaarVendors().put(UUID.fromString(uuid) , plugin.getGuiManager().parseProfileGui( plugin.getConfigManager().getConfig("gui") , "profile-gui" , UUID.fromString(uuid) ));
            }

            for (String productUUID : bazaarProfileConfig.getConfigurationSection(uuid).getKeys(false)) {

                ItemStack productItem = ItemStack.deserialize(bazaarProfileConfig.getConfigurationSection(uuid + "." + productUUID + ".item").getValues(true));
                Product product = new Product(UUID.fromString(uuid) , productItem , bazaarProfileConfig.getInt(uuid + "." + productUUID + ".price" ));
                product.productGuiItem = ItemBuilder.from( productItem.clone() ).lore( TextUtil.format("<gray>Fiyat : <white>" + product.productPrice) ).asGuiItem(event -> {

                    if ( product.productSeller.equals( event.getWhoClicked().getUniqueId() ) ) return;
                    if ( plugin.getWalletAPI().getCoins( event.getWhoClicked().getUniqueId() ) < product.productPrice ) return;

                    plugin.getGuiManager().parseBuyGui( plugin.getConfigManager().getConfig("gui") , "buy-gui" , product ).open( event.getWhoClicked() );

                });
                product.profileGuiItem = ItemBuilder.from( product.productGuiItem.getItemStack() ).asGuiItem(e -> {
                    e.setCancelled(true);

                    plugin.getGuiManager().parseEditGui( plugin.getConfigManager().getConfig("gui") , "edit-gui" , product ).open( e.getWhoClicked() );
                });
                plugin.getBazaarManager().getBazaarVendors().get( UUID.fromString(uuid) ).addItem( product.profileGuiItem );
                plugin.getBazaarManager().getBazaarGui().addItem( product.productGuiItem );
                plugin.getBazaarManager().getBazaarProducts().get( UUID.fromString(uuid) ).add(product);
            }

        }

    }

    public final void saveBazaar() throws IOException {

        for ( UUID uuid : plugin.getBazaarManager().getBazaarProducts().keySet() ) {

            File profileFile = new File(plugin.getDataFolder() , "./bazaar/" + uuid + ".yml");
            profileFile.createNewFile();
            saveToFile(profileFile , YamlConfiguration.loadConfiguration( profileFile ) , uuid);
        }

    }

    private void saveToFile(File configFile , FileConfiguration config , UUID uuid) throws IOException {

        for (int i = 0; i < plugin.getBazaarManager().getBazaarProducts().get( uuid ).size(); i++) {

            Product product = plugin.getBazaarManager().getBazaarProducts().get( uuid ).get(i);
            config.set(uuid + "." + product.getProductUUID() + ".price", product.productPrice );
            config.set(uuid + "." + product.getProductUUID() + ".item" , product.productItem.serialize() );

        }

        config.save(configFile);

    }

}
