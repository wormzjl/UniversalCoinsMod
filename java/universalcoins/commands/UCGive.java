package universalcoins.commands;

import universalcoins.UniversalCoins;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.WorldServer;

public class UCGive extends CommandBase {
	private static final Item[] coins = new Item[] {
			UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack,
			UniversalCoins.proxy.itemLargeCoinStack,
			UniversalCoins.proxy.itemSmallCoinBag,
			UniversalCoins.proxy.itemLargeCoinBag };

	@Override
	public String getCommandName() {
		return "ucgive";
	}

	@Override
	public String getCommandUsage(ICommandSender var1) {
		return "/ucgive <playerName> <amount> : Give another player coins.";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] astring) {
		if (astring.length == 2) {
			EntityPlayer recipient = null;
			WorldServer[] ws= MinecraftServer.getServer().worldServers;
			for(WorldServer w : ws) {
				if(w.playerEntities.contains(w.getPlayerEntityByName(astring[0]))) {
					recipient = (EntityPlayer) w.getPlayerEntityByName(astring[0]);
				}
			}
			int coinsToSend = 0;
			if (recipient == null) {
				sender.addChatMessage(new ChatComponentText("Player " + astring[0] + " not found."));
				return;
			}
			try {
				coinsToSend = Integer.parseInt(astring[1]);
			} catch (NumberFormatException e) {
				sender.addChatMessage(new ChatComponentText("Please specify a valid coin amount."));
				return;
			}
			givePlayerCoins(recipient, coinsToSend);
			sender.addChatMessage(new ChatComponentText("Gave " + astring[0] + " " + astring[1] + " coins."));
			recipient.addChatMessage(new ChatComponentText( sender.getCommandSenderName() + " gave you " + astring[1] + " coins."));
		} else
			sender.addChatMessage(new ChatComponentText("Please include player name and amount to give."));
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
}
