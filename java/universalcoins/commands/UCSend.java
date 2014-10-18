package universalcoins.commands;

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

public class UCSend extends CommandBase {
	private static final int[] multiplier = new int[] { 1, 9, 81, 729, 6561 };
	private static final Item[] coins = new Item[] {
			UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack,
			UniversalCoins.proxy.itemLargeCoinStack,
			UniversalCoins.proxy.itemSmallCoinBag,
			UniversalCoins.proxy.itemLargeCoinBag };

	@Override
	public String getCommandName() {
		return "ucsend";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/ucsend <playerName> <amount> : Send another player coins.";
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if (sender instanceof EntityPlayerMP) {
			if (astring.length == 2) {
				// check for player
				EntityPlayerMP recipient = null;
				WorldServer[] ws = MinecraftServer.getServer().worldServers;
				for (WorldServer w : ws) {
					if (w.playerEntities.contains(w.getPlayerEntityByName(astring[0]))) { 
						recipient = (EntityPlayerMP) w.getPlayerEntityByName(astring[0]);
					}
				}
				if (recipient == null) {
					sender.addChatMessage(new ChatComponentText("Player " + astring[0] + " not found."));
					return;
				}
				int requestedSendAmount = 0;
				try {
					requestedSendAmount = Integer.parseInt(astring[1]);
				} catch (NumberFormatException e) {
					sender.addChatMessage(new ChatComponentText("Please specify a valid coin amount."));
					return;
				}
				if (getPlayerCoins((EntityPlayerMP) sender) < requestedSendAmount) {
					sender.addChatMessage(new ChatComponentText("Insufficent coins."));
					return;
				}
				// get coins from player inventory
				int coinsFromSender = 0;
				EntityPlayerMP player = (EntityPlayerMP) sender;
				for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
					ItemStack stack = player.inventory.getStackInSlot(i);
					for (int j = 0; j < coins.length; j++) {
						if (stack != null && stack.getItem() == coins[j]
								&& coinsFromSender < requestedSendAmount) {
							coinsFromSender += stack.stackSize * multiplier[j];
							player.inventory.setInventorySlotContents(i, null);
						}
					}
				}
				// subtract coins to send from player coins
				coinsFromSender -= requestedSendAmount;
				// send coins to recipient
				int coinChange = givePlayerCoins(recipient, requestedSendAmount);
				sender.addChatMessage(new ChatComponentText((requestedSendAmount - coinChange) + " coins sent to " + astring[0]));
				recipient.addChatMessage(new ChatComponentText("Recieved " + (requestedSendAmount - coinChange) + " coins from " + sender.getCommandSenderName()));
				// add change back to sender coins
				coinsFromSender += coinChange;
				// give sender back change
				int leftOvers = givePlayerCoins(player, coinsFromSender);
			} else
				sender.addChatMessage(new ChatComponentText("Please include player name and amount to send."));
		}
	}

	private int getCoinMultiplier(Item item) {
		for (int i = 0; i < 5; i++) {
			if (item == coins[i]) {
				return multiplier[i];
			}
		}
		return -1;
	}

	private int givePlayerCoins(EntityPlayerMP recipient, int coinsLeft) {
		while (coinsLeft > 0) {
			// use logarithm to find largest cointype for coins being sent
			int logVal = Math.min((int) (Math.log(coinsLeft) / Math.log(9)), 4);
			int stackSize = Math.min((int) (coinsLeft / Math.pow(9, logVal)), 64);
			// add a stack to the recipients inventory
			Boolean coinsAdded = recipient.inventory.addItemStackToInventory(new ItemStack(coins[logVal], stackSize));
			if (coinsAdded) {
				coinsLeft -= (stackSize * Math.pow(9, logVal));
			} else {
				return coinsLeft; // return change
			}
		}
		return 0;
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
