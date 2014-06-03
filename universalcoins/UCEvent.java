package universalcoins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class UCEvent {
	
	public static String curVersion = null;
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		// FMLLog.info("Player logged in");
		if (UniversalCoins.updateCheck) {
			try {
				isUpdateAvailable();
			} catch (MalformedURLException e) {
				FMLLog.warning("Universal Coins: Malformed update URL in update check");
				e.printStackTrace();
			} catch (IOException e) {
				FMLLog.warning("Universal Coins: IO exception during update check");
				e.printStackTrace();
			}
			event.player.addChatComponentMessage(new ChatComponentText(
					"Universal Coins " + curVersion + " update available. See http://www.minecraftforum.net/topic/2653714-172-universal-coins for details"));
		}
	}

	public static boolean isUpdateAvailable() throws IOException,
			MalformedURLException {
		BufferedReader versionFile = new BufferedReader(
				new InputStreamReader(
						new URL(
								"https://raw.githubusercontent.com/notabadminer/UniversalCoinsMod/master/version.txt")
								.openStream()));
		curVersion = versionFile.readLine();

		if (!curVersion.contains(UniversalCoins.version)) {
			return true;
		}

		versionFile.close();

		return false;
	}
}
