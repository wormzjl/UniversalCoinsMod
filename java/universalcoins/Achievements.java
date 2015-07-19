package universalcoins;

import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class Achievements {
	
	public static void init() {
		AchievementPage page1 = new AchievementPage("Universal Coins", achCoins, achThousands);
		AchievementPage.registerAchievementPage(page1);
	}
	
	public static final Achievement achCoins = new Achievement("achievement.coins", "AchievementCoins", 0, 0, UniversalCoins.proxy.itemCoin, null);
	public static final Achievement achThousands = new Achievement("achievement.thousands", "AchievementThousands", 2, -1, UniversalCoins.proxy.itemLargeCoinStack, achCoins);

}
