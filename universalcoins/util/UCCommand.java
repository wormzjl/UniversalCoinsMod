package universalcoins.util;

import cpw.mods.fml.common.FMLLog;
import ibxm.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class UCCommand extends CommandBase{
	
	private boolean firstChange = true;

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
				sender.addChatMessage(new ChatComponentText("- get <itemName> : Get price of item."));
				sender.addChatMessage(new ChatComponentText("- set <itemName> <price> : Set price of item."));
				sender.addChatMessage(new ChatComponentText("- reload : Reload pricelists."));
				sender.addChatMessage(new ChatComponentText("- reset : Reset default prices. Will not override items not priced by default"));
				sender.addChatMessage(new ChatComponentText("- save : Save pricelists."));
				sender.addChatMessage(new ChatComponentText("Hint: Use \"this\" in place of <itemName> to get or set item held by player."));
			} else if (astring[0].matches("reload")) {
				UCItemPricer.loadConfigs();
			} else if (astring[0].matches("get")) {
				//get item price
				if (astring.length > 1) {
					int price = -1;
					if (astring[1].matches("this")) {
						ItemStack stack = getPlayerItem(sender);
						if (stack != null) {
							price = UCItemPricer.getItemPrice(stack);
						}
					} else { price = UCItemPricer.getItemPrice(astring[1]); }
					if (price == -1) {
						sender.addChatMessage(new ChatComponentText("UC: " + astring[1] + " is not set"));
					} else
						sender.addChatMessage(new ChatComponentText("UC: " + astring[1] + " is " + price));
				} else sender.addChatMessage(new ChatComponentText("UC: Please specify item"));
			} else if (astring[0].matches("set")) {
				//set item price
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
					} else { result = UCItemPricer.setItemPrice(astring[1], price); }
					if (result = true) {
						sender.addChatMessage(new ChatComponentText("UC: price set to " + price));
						if (firstChange) {
							sender.addChatMessage(new ChatComponentText("UC: changes will not be saved"));
							sender.addChatMessage(new ChatComponentText("UC: run \"universalcoins save\" to save"));
							sender.addChatMessage(new ChatComponentText("UC: run \"universalcoins reload\" to undo"));
							firstChange = false;
						}
					} else {
						sender.addChatMessage(new ChatComponentText("UC: failed to set price"));
						sender.addChatMessage(new ChatComponentText("UC: item may not be priceable"));
					}
				} else sender.addChatMessage(new ChatComponentText("UC: Please specify item and price"));
			} else if (astring[0].matches("reload")) {
				UCItemPricer.loadConfigs();
			} else if (astring[0].matches("reset")) {
					UCItemPricer.loadConfigs();
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

}
