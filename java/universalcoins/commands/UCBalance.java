package universalcoins.commands;

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
		return "ucbalance";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/ucbalance : Retrive coin balance.";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
		FMLLog.info("Player called balance command.");
        return true;
    }

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		FMLLog.info("Processing balance command.");
		if (sender instanceof EntityPlayerMP) {
			int playerCoins = getPlayerCoins((EntityPlayerMP) sender);
			sender.addChatMessage(new ChatComponentText("Balance: " + playerCoins));
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
