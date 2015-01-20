package universalcoins.util;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import universalcoins.UniversalCoins;

public class RecipeEnderCard implements IRecipe {
	
	private ItemStack newStack;
	
	@Override
	public boolean matches(InventoryCrafting var1, World var2) {
		this.newStack = null;
		for (int j = 0; j < var1.getSizeInventory(); j++) {
			if (var1.getStackInSlot(j) != null) {
				if (var1.getStackInSlot(j).getItem() == Items.ender_pearl
						|| (j == 4 && var1.getStackInSlot(j).getItem() == UniversalCoins.proxy.itemUCCard)) {
					// continue
				} else
					return false;
			} else
				return false;
		}
		newStack = new ItemStack(UniversalCoins.proxy.itemEnderCard);
		this.newStack.setTagCompound(var1.getStackInSlot(4).getTagCompound());
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		return newStack;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return newStack;
	}

}
