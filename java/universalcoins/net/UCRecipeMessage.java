package universalcoins.net;

import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import universalcoins.UniversalCoins;
import universalcoins.util.Vending;

public class UCRecipeMessage implements IMessage, IMessageHandler<UCRecipeMessage, IMessage> {
	private boolean tradeStationRecipesEnabled, vendorRecipesEnabled, vendorFrameRecipesEnabled, atmRecipeEnabled,
			enderCardRecipeEnabled, banditRecipeEnabled, signalRecipeEnabled, linkCardRecipeEnabled,
			packagerRecipeEnabled, powerBaseRecipeEnabled, powerReceiverRecipeEnabled;

	public UCRecipeMessage() {
		this.tradeStationRecipesEnabled = UniversalCoins.tradeStationRecipesEnabled;
		this.vendorRecipesEnabled = UniversalCoins.vendorRecipesEnabled;
		this.vendorFrameRecipesEnabled = UniversalCoins.vendorFrameRecipesEnabled;
		this.atmRecipeEnabled = UniversalCoins.atmRecipeEnabled;
		this.enderCardRecipeEnabled = UniversalCoins.enderCardRecipeEnabled;
		this.banditRecipeEnabled = UniversalCoins.banditRecipeEnabled;
		this.signalRecipeEnabled = UniversalCoins.signalRecipeEnabled;
		this.linkCardRecipeEnabled = UniversalCoins.linkCardRecipeEnabled;
		this.packagerRecipeEnabled = UniversalCoins.packagerRecipeEnabled;
		this.powerBaseRecipeEnabled = UniversalCoins.powerBaseRecipeEnabled;
		this.powerReceiverRecipeEnabled = UniversalCoins.powerReceiverRecipeEnabled;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.tradeStationRecipesEnabled = buf.readBoolean();
		this.vendorRecipesEnabled = buf.readBoolean();
		this.vendorFrameRecipesEnabled = buf.readBoolean();
		this.atmRecipeEnabled = buf.readBoolean();
		this.enderCardRecipeEnabled = buf.readBoolean();
		this.banditRecipeEnabled = buf.readBoolean();
		this.signalRecipeEnabled = buf.readBoolean();
		this.linkCardRecipeEnabled = buf.readBoolean();
		this.packagerRecipeEnabled = buf.readBoolean();
		this.powerBaseRecipeEnabled = buf.readBoolean();
		this.powerReceiverRecipeEnabled = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(tradeStationRecipesEnabled);
		buf.writeBoolean(vendorRecipesEnabled);
		buf.writeBoolean(vendorFrameRecipesEnabled);
		buf.writeBoolean(atmRecipeEnabled);
		buf.writeBoolean(enderCardRecipeEnabled);
		buf.writeBoolean(banditRecipeEnabled);
		buf.writeBoolean(signalRecipeEnabled);
		buf.writeBoolean(linkCardRecipeEnabled);
		buf.writeBoolean(packagerRecipeEnabled);
		buf.writeBoolean(powerBaseRecipeEnabled);
		buf.writeBoolean(powerReceiverRecipeEnabled);
	}

	@Override
	public IMessage onMessage(UCRecipeMessage message, MessageContext ctx) {
		if (!message.tradeStationRecipesEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockTradeStation));
			removeRecipe(new ItemStack(UniversalCoins.proxy.itemSeller));
		}
		if (!message.vendorRecipesEnabled) {
			for (int i = 0; i < Vending.supports.length; i++) {
				removeRecipe(new ItemStack(UniversalCoins.proxy.blockVendor, 1, i));
			}
		}
		if (!message.vendorFrameRecipesEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockVendorFrame));
		}
		if (!message.atmRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockCardStation));
		}
		if (!message.enderCardRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.itemEnderCard));
		}
		if (!message.banditRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockBandit));
		}
		if (!message.signalRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockSignal));
		}
		if (!message.linkCardRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.itemLinkCard));
		}
		if (!message.packagerRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockPackager));
		}
		if (!message.powerBaseRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockPowerBase));
		}
		if (!message.powerReceiverRecipeEnabled) {
			removeRecipe(new ItemStack(UniversalCoins.proxy.blockPowerReceiver));
		}

		return null;
	}

	private void removeRecipe(ItemStack stack) {
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		Iterator<IRecipe> recipeIterator = recipeList.iterator();

		while (recipeIterator.hasNext()) {
			ItemStack recipeStack = recipeIterator.next().getRecipeOutput();
			if (recipeStack != null) {
				if (recipeStack.areItemStacksEqual(recipeStack, stack)) {
					recipeIterator.remove();
				}
			}
		}
	}
}