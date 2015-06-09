package universalcoins.util;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import universalcoins.UniversalCoins;

public class RecipeAdvancedSign implements IRecipe {

	private ItemStack newStack;
	private int plankIndex;

	@Override
	public boolean matches(InventoryCrafting inventorycrafting, World world) {
		this.newStack = null;
		boolean hasSign = false;
		boolean hasPlank = false;
		for (int j = 0; j < inventorycrafting.getSizeInventory(); j++) {
			if (inventorycrafting.getStackInSlot(j) != null && !hasPlank && 
					isWoodPlank(inventorycrafting.getStackInSlot(j))) {
					hasPlank = true;
					plankIndex = j;
					continue;
			}
			if (inventorycrafting.getStackInSlot(j) != null && !hasSign && 
					inventorycrafting.getStackInSlot(j).getItem() == UniversalCoins.proxy.itemUCSign) {
				hasSign = true;
				continue;
			}
			if (inventorycrafting.getStackInSlot(j) != null) {
				return false;
			}
		}
		newStack = new ItemStack(UniversalCoins.proxy.itemUCSign);
		if (hasPlank) {
			String blockIcon = inventorycrafting.getStackInSlot(plankIndex)
					.getIconIndex().getIconName();

			// the iconIndex function does not work with BOP so we have to do a
			// bit of a hack here
			if (blockIcon.startsWith("biomesoplenty")) {
				String[] iconInfo = blockIcon.split(":");
				String[] blockName = inventorycrafting.getStackInSlot(plankIndex)
						.getUnlocalizedName().split("\\.", 3);
				String woodType = blockName[2].replace("Plank", "");
				// hellbark does not follow the same naming convention
				if (woodType.contains("hell"))
					woodType = "hell_bark";
				blockIcon = iconInfo[0] + ":" + "plank_" + woodType;
				// bamboo needs a hack too
				if (blockIcon.contains("bamboo"))
					blockIcon = blockIcon.replace("plank_bambooThatching",
							"bamboothatching");
				// I feel dirty now :(
			}
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("blockIcon", blockIcon);
			this.newStack.setTagCompound(tag);
		}
		if (!hasPlank || !hasSign)
			return false;
		else
			return true;
	}

	private boolean isWoodPlank(ItemStack stack) {
		for (ItemStack oreStack : OreDictionary.getOres("plankWood")) {
			if (OreDictionary.itemMatches(oreStack, stack, false)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1) {
		return newStack;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return newStack;
	}

}
