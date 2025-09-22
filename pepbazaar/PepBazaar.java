package pepgames.plugins.pepbazaar;

import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import pepgames.plugins.ModernConfigManager;
import pepgames.plugins.PepWalletAPI;
import pepgames.plugins.commands.CommandHandler;
import pepgames.plugins.pepbazaar.commands.Commands;
import pepgames.plugins.pepbazaar.listeners.DefaultListener;
import pepgames.plugins.pepbazaar.managers.BazaarManager;
import pepgames.plugins.pepbazaar.managers.GuiManager;
import pepgames.plugins.pepbazaar.managers.SaveLoadManager;

public final class PepBazaar extends JavaPlugin {

    @Getter private static PepBazaar instance; BukkitAudiences adventure;
    @Getter private PepWalletAPI walletAPI;

    @Getter private ModernConfigManager configManager; @Getter private SaveLoadManager saveLoadManager;
    @Getter private GuiManager guiManager; @Getter private BazaarManager bazaarManager;

    DefaultListener defaultListener;

    @Override
    public void onEnable() {
        instance = this; adventure = BukkitAudiences.create(this);

        if (!setupWallet() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no PepWallet dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        configManager = new ModernConfigManager(this);
        guiManager = new GuiManager(this); bazaarManager = new BazaarManager(this);
        saveLoadManager = new SaveLoadManager(this);

        saveLoadManager.loadBazaar();

        CommandHandler.registerCommands( Commands.class , this );

        defaultListener = new DefaultListener(this);
    }

    @Override @SneakyThrows
    public void onDisable() {
        instance = null;

        saveLoadManager.saveBazaar();
    }

    private boolean setupWallet() {
        if (Bukkit.getPluginManager().getPlugin("pepWallet") == null) {
            return false;
        }

        RegisteredServiceProvider<PepWalletAPI> rsp = getServer().getServicesManager().getRegistration(PepWalletAPI.class);
        if (rsp == null) return false;

        walletAPI = rsp.getProvider();
        return walletAPI != null;
    }

    public @NonNull BukkitAudiences adventure() {
        if (adventure == null) throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        return adventure;
    }
}
