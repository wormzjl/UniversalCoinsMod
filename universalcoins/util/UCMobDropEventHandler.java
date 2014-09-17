package universalcoins.util;

import java.util.Random;

import universalcoins.UniversalCoins;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class UCMobDropEventHandler {
	
	@SubscribeEvent
	public void onEntityDrop(LivingDropsEvent event) {
		if (event.source.getDamageType().equals("player")) {
			Random random = new Random();
			int chance = random.nextInt(3);
			int dropped = random.nextInt(39) + 1;

			if ((event.entity instanceof EntityZombie || event.entity instanceof EntitySkeleton)
					&& !event.entity.worldObj.isRemote && chance == 0) {
				event.entityLiving.entityDropItem(new ItemStack(
						UniversalCoins.proxy.itemCoin, dropped), 0.0F);
			}

			// endermen drop small coin stacks instead of coins
			if ((event.entity instanceof EntityEnderman)
					&& !event.entity.worldObj.isRemote) {
				event.entityLiving.entityDropItem(new ItemStack(
						UniversalCoins.proxy.itemSmallCoinStack, dropped), 0.0F);
			}
		}
	}
}
