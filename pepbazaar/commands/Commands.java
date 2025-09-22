package pepgames.plugins.pepbazaar.commands;

import org.bukkit.entity.Player;
import pepgames.plugins.commands.Command;
import pepgames.plugins.commands.parameter.Param;
import pepgames.plugins.pepbazaar.PepBazaar;

public class Commands {

    @Command(names={"bazaar","pazar"} , permission="pep.bazaar.default")
    public final void bazaarCommand(Player player) {

        PepBazaar.getInstance().getBazaarManager().getBazaarGui().open( player );

    }

}
