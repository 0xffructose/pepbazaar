package pepgames.plugins.pepbazaar.managers;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pepgames.plugins.pepbazaar.PepBazaar;

public class SoundManager {

    private static PepBazaar plugin = PepBazaar.getInstance();
    public static final SoundHolder increaseSound = new SoundHolder( Sound.valueOf( plugin.getConfigManager().getConfig("sounds").getString("sounds.increase").toUpperCase() ) , 1F , 1F );
    public static final SoundHolder decreaseSound = new SoundHolder( Sound.valueOf( plugin.getConfigManager().getConfig("sounds").getString("sounds.decrease").toUpperCase() ) , 1F , 1F );
    public static final SoundHolder successfulSound = new SoundHolder( Sound.valueOf( plugin.getConfigManager().getConfig("sounds").getString("sounds.successful").toUpperCase() ) , 1F , 1F );
    public static final SoundHolder unsuccessfulSound = new SoundHolder( Sound.valueOf( plugin.getConfigManager().getConfig("sounds").getString("sounds.unsuccessful").toUpperCase() ) , 1F , 1F );

    public static void playSound(Player player , SoundHolder soundHolder) {
        player.playSound(player.getLocation() , soundHolder.sound , soundHolder.pitch , soundHolder.volume);
    }

    public static final class SoundHolder {

        @Getter Sound sound; @Getter Float pitch , volume;

        public SoundHolder(Sound sound , Float pitch , Float volume) {
            this.sound = sound; this.pitch = pitch; this.volume = volume;
        }

    }
}
