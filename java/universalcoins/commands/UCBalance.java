package universalcoins.commands;

import java.text.DecimalFormat;

import cpw.mods.fml.common.FMLLog;
import universalcoins.UniversalCoins;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldServer;

public class UCBalance extends CommandBase {
	private static final int[] multiplier = new int[] { 1, 9, 81, 729, 6561 };
	private static final Item[] coins = new Item[] {
			UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack,
			UniversalCoins.proxy.itemLargeCoinStack,
			UniversalCoins.proxy.itemSmallCoinBag,
			UniversalCoins.proxy.itemLargeCoinBag };

	@Override
	public String getCommandName() {
		return StatCollector.translateToLocal("command.balance.name");
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return StatCollector.translateToLocal("command.balance.help");
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if (sender instanceof EntityPlayerMP) {
			int playerCoins = getPlayerCoins((EntityPlayerMP) sender);
			DecimalFormat formatter = new DecimalFormat("###,###,###");
			sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("command.balance.result") + formatter.format(playerCoins)));
		}		
	}

	private int getPlayerCoins(EntityPlayerMP player) {
		int coinsFound = 0;
		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			ItemStack stack = player.inventory.getStackInSlot(i);
			for (int j = 0; j < coins.length; j++) {
				if (stack != null && stack.getItem() == coins[j]) {
					coinsFound += stack.stackSize * multiplier[j];
				}
			}
		}
		return coinsFound;
	}
}
