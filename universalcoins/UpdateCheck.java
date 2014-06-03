package universalcoins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import cpw.mods.fml.common.FMLLog;

public class UpdateCheck {

	public static String latestVersion = null;

	public static boolean isUpdateAvailable() {
		try {
			BufferedReader versionFile = new BufferedReader(
					new InputStreamReader(
							new URL("https://dl.dropboxusercontent.com/s/h4hy30i1c8qsh2n/version.txt").openStream()));
			latestVersion = versionFile.readLine();
			versionFile.close();
		} catch (MalformedURLException e) {
			FMLLog.warning("Universal Coins: Malformed update URL in update check");
		} catch (IOException e) {
			FMLLog.warning("Universal Coins: IO exception during update check");
		}
		
		if (latestVersion != null && !latestVersion.endsWith(UniversalCoins.version)) {
			return true;
		}
		return false;
	}
}
