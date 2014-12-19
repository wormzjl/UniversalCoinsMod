package universalcoins.commands;

import java.text.DecimalFormat;

import cpw.mods.fml.common.FMLLog;
import universalcoins.UniversalCoins;
import universalcoins.util.UCWorldData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
			int accountCoins = getAccountBalance((EntityPlayerMP) sender);
			DecimalFormat formatter = new DecimalFormat("###,###,###");
			sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(
					"command.balance.result.inventory") + formatter.format(playerCoins)));
			if (accountCoins != -1) {
				sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(
						"command.balance.result.account") + formatter.format(accountCoins)));
			}
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
	
	public int getAccountBalance(EntityPlayerMP player) {
		if (getWorldString(player, player.getDisplayName()) != "") {
			String accountNumber = getWorldString(player, player.getDisplayName());
			if (getWorldString(player, accountNumber) != "") {
				return getWorldInt(player, accountNumber);
			}
		} return -1;
	}
	
	private int getWorldInt(EntityPlayerMP player, String tag) {
		UCWorldData wData = UCWorldData.get(player.worldObj);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getInteger(tag);
	}
	
	private String getWorldString(EntityPlayerMP player, String tag) {
		UCWorldData wData = UCWorldData.get(player.worldObj);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getString(tag);
	}
}
