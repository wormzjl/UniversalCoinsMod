package universalcoins.util;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import universalcoins.UniversalCoins;
import universalcoins.items.ItemEnderCard;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class UCCraftingEventHandler {
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		
		IInventory craftMatrix = event.craftMatrix;
		
		if (event.crafting.getItem() instanceof ItemEnderCard) {
			if (craftMatrix.getStackInSlot(4).stackTagCompound != null) {
				event.crafting.stackTagCompound = (NBTTagCompound) craftMatrix.getStackInSlot(4).stackTagCompound.copy();
			}
		}
		
		//we can't compare the itemblock to the block class, so we just compare the unlocalized names here
		if (event.crafting.getItem().getUnlocalizedName().contentEquals(UniversalCoins.proxy.blockVendorFrame.getUnlocalizedName())) {
			ItemStack textureStack = craftMatrix.getStackInSlot(4);
			NBTTagList itemList = new NBTTagList();
			NBTTagCompound tag = new NBTTagCompound();
			if (textureStack != null) {
				tag.setByte("Slot", (byte) 0);
				textureStack.writeToNBT(tag);
			}
			tag.setTag("Texture", itemList);
			event.crafting.setTagCompound(tag);
		}		
	}
}
