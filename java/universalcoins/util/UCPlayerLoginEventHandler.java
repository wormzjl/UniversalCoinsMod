package universalcoins.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import universalcoins.UniversalCoins;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class UCPlayerLoginEventHandler {
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		//FMLLog.info("UC: Player logged on");
		if (UniversalCoins.updateCheck) {
			if (UpdateCheck.isUpdateAvailable()) {
				event.player.addChatComponentMessage(new ChatComponentText(
				"Universal Coins: An update is available " + UpdateCheck.onlineVersion + " is the latest. See http://goo.gl/Fot7wW for details."));
			}
		}
	}
}
