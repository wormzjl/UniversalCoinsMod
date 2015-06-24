package universalcoins.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import universalcoins.UniversalCoins;
import universalcoins.net.UCRecipeMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class UCPlayerLoginEventHandler {
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
				
		MinecraftServer server = MinecraftServer.getServer();
		if (!server.isSinglePlayer()) {
			//we need to update client with recipes that are enabled
			UniversalCoins.snw.sendTo(new UCRecipeMessage(), (EntityPlayerMP) event.player);
		}
	}
}
