package universalcoins.util;

import universalcoins.UniversalCoins;
import cpw.mods.fml.common.FMLLog;
import ibxm.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class UCCommand extends CommandBase {

	private boolean firstChange = true;
	private static final int[] multiplier = new int[] { 1, 9, 81, 729, 6561 };
	private static final Item[] coins = new Item[] {
			UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack,
			UniversalCoins.proxy.itemLargeCoinStack,
			UniversalCoins.proxy.itemSmallCoinBag,
			UniversalCoins.proxy.itemLargeCoinBag };

	@Override
	public String getCommandName() {
		return "universalcoins";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "/" + this.getCommandName() + " help";
	}

	// Method called when the command is typed in
	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if (astring.length <= 0) {
			throw new WrongUsageException(this.getCommandUsage(sender));
		} else if (astring[0].matches("help")) {
			sender.addChatMessage(new ChatComponentText("Format: " + this.getCommandName() + " <command> <arguments>"));
			sender.addChatMessage(new ChatComponentText("Available commands:"));
			sender.addChatMessage(new ChatComponentText("- send <playerName> <amount> : Send another player coins."));
			sender.addChatMessage(new ChatComponentText("- give <playerName> <amount> : Give a player coins."));
			sender.addChatMessage(new ChatComponentText("- balance : Retrive coin balance."));
			sender.addChatMessage(new ChatComponentText("- get <itemName> : Get price of item."));
			sender.addChatMessage(new ChatComponentText("- set <itemName> <price> : Set price of item."));
			sender.addChatMessage(new ChatComponentText("- reload : Reload pricelists."));
			sender.addChatMessage(new ChatComponentText("- reset : Reset default prices. Will not override items not priced by default"));
			sender.addChatMessage(new ChatComponentText("- save : Save pricelists."));
			sender.addChatMessage(new ChatComponentText("Hint: Use \"this\" in place of <itemName> to get or set item held by player."));
		} else if (astring[0].matches("reload")) {
			UCItemPricer.loadConfigs();
		} else if (astring[0].matches("send")) {
			if (astring.length > 2) {
				// check for player
				EntityPlayer recipient = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(astring[1]);
				if (recipient == null) {
					sender.addChatMessage(new ChatComponentText("UC: Player " + astring[1] + " not found."));
					return;
				}
				int requestedSendAmount = 0;
				try {
					requestedSendAmount = Integer.parseInt(astring[2]);
				} catch (NumberFormatException e) {
					sender.addChatMessage(new ChatComponentText("UC: Please specify a valid coin amount."));
					return;
				}
				if (getPlayerCoins((EntityPlayer) sender) < requestedSendAmount) {
					sender.addChatMessage(new ChatComponentText("UC: Insufficent coins."));
					return;
				}
				// get coins from player inventory
				int coinsFromSender = 0;
				EntityPlayer player = (EntityPlayer) sender;
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
				sender.addChatMessage(new ChatComponentText("UC: Sent " + (requestedSendAmount - coinChange) + " to " + astring[1]));
				// add change back to sender coins
				coinsFromSender += coinChange;
				// give sender back change
				int leftOvers = givePlayerCoins(player, coinsFromSender);
			} else
				sender.addChatMessage(new ChatComponentText("UC: Please include player name and amount to send."));
		} else if (astring[0].matches("give")) {
			if (astring.length > 2) {
				EntityPlayer recipient = MinecraftServer.getServer()
						.getConfigurationManager()
						.getPlayerForUsername(astring[1]);
				int coinsToSend = 0;
				if (recipient == null) {
					sender.addChatMessage(new ChatComponentText("UC: Player " + astring[1] + " not found."));
					return;
				}
				try {
					coinsToSend = Integer.parseInt(astring[2]);
				} catch (NumberFormatException e) {
					sender.addChatMessage(new ChatComponentText("UC: Please specify a valid coin amount."));
					return;
				}
				givePlayerCoins(recipient, coinsToSend);
			} else
				sender.addChatMessage(new ChatComponentText(
						"UC: Please include player name and amount to give."));
		} else if (astring[0].matches("balance")) {
			int playerCoins = getPlayerCoins((EntityPlayer) sender);
			sender.addChatMessage(new ChatComponentText("Balance: " + playerCoins));
		} else if (astring[0].matches("get")) {
			// get item price
			if (astring.length > 1) {
				int price = -1;
				if (astring[1].matches("this")) {
					ItemStack stack = getPlayerItem(sender);
					if (stack != null) {
						price = UCItemPricer.getItemPrice(stack);
					}
				} else {
					price = UCItemPricer.getItemPrice(astring[1]);
				}
				if (price == -1) {
					sender.addChatMessage(new ChatComponentText("UC: " + astring[1] + " is not set"));
				} else
					sender.addChatMessage(new ChatComponentText("UC: " + astring[1] + " is " + price));
			} else
				sender.addChatMessage(new ChatComponentText("UC: Please specify item"));
		} else if (astring[0].matches("set")) {
			// set item price
			if (astring.length > 2) {
				boolean result = false;
				int price = -1;
				try {
					price = Integer.parseInt(astring[2]);
				} catch (NumberFormatException e) {
					sender.addChatMessage(new ChatComponentText("UC: Please specify a valid price"));
					return;
				}
				if (astring[1].matches("this")) {
					ItemStack stack = getPlayerItem(sender);
					if (stack != null) {
						result = UCItemPricer.setItemPrice(stack, price);
					}
				} else {
					result = UCItemPricer.setItemPrice(astring[1], price);
				}
				if (result == true) {
					sender.addChatMessage(new ChatComponentText("UC: price set to " + price));
					if (firstChange) {
						sender.addChatMessage(new ChatComponentText("UC: changes will not be saved"));
						sender.addChatMessage(new ChatComponentText(
								"UC: run \"universalcoins save\" to save"));
						sender.addChatMessage(new ChatComponentText(
								"UC: run \"universalcoins reload\" to undo"));
						firstChange = false;
					}
				} else {
					sender.addChatMessage(new ChatComponentText(
							"UC: failed to set price"));
					sender.addChatMessage(new ChatComponentText(
							"UC: item may not be priceable"));
				}
			} else
				sender.addChatMessage(new ChatComponentText(
						"UC: Please specify item and price"));
		} else if (astring[0].matches("reload")) {
			UCItemPricer.loadConfigs();
			sender.addChatMessage(new ChatComponentText("UC: Changes reset"));
		} else if (astring[0].matches("reset")) {
			UCItemPricer.resetDefaults();
			sender.addChatMessage(new ChatComponentText("UC: Price defaults reloaded"));
		} else if (astring[0].matches("save")) {
			UCItemPricer.updatePriceLists();
			sender.addChatMessage(new ChatComponentText("UC: Changes saved"));
		}
	}

	private ItemStack getPlayerItem(ICommandSender sender) {
		EntityPlayer player = (EntityPlayer) sender;
		if (player.getHeldItem() != null) {
			ItemStack stack = player.getHeldItem();
			return stack;
		}
		return null;
	}

	private int getCoinMultiplier(Item item) {
		for (int i = 0; i < 5; i++) {
			if (item == coins[i]) {
				return multiplier[i];
			}
		}
		return -1;
	}

	private int givePlayerCoins(EntityPlayer recipient, int coinsLeft) {
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

	private int getPlayerCoins(EntityPlayer player) {
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