package universalcoins.integration.craftguide;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import universalcoins.UniversalCoins;
import uristqwerty.CraftGuide.api.CraftGuideAPIObject;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

public class CGRecipeEnderCard extends CraftGuideAPIObject implements RecipeProvider {
	static int SLOT_SIZE = 16;

	@Override
	public void generateRecipes(RecipeGenerator generator) {
		ItemStack craftingTable = new ItemStack(Blocks.crafting_table);

		RecipeTemplate template = generator.createRecipeTemplate(CRAFTING_SLOTS, craftingTable);

		generator.addRecipe(template, new Object[] { null, Items.ender_pearl, null, Items.ender_pearl,
				UniversalCoins.proxy.itemUCCard, Items.ender_pearl, null, Items.ender_pearl, null,
				UniversalCoins.proxy.itemEnderCard });
	}

	private static final Slot[] CRAFTING_SLOTS = new ItemSlot[] { new ItemSlot(3, 3, SLOT_SIZE, SLOT_SIZE),
			new ItemSlot(21, 3, SLOT_SIZE, SLOT_SIZE), new ItemSlot(39, 3, SLOT_SIZE, SLOT_SIZE),
			new ItemSlot(3, 21, SLOT_SIZE, SLOT_SIZE), new ItemSlot(21, 21, SLOT_SIZE, SLOT_SIZE),
			new ItemSlot(39, 21, SLOT_SIZE, SLOT_SIZE), new ItemSlot(3, 39, SLOT_SIZE, SLOT_SIZE),
			new ItemSlot(21, 39, SLOT_SIZE, SLOT_SIZE), new ItemSlot(39, 39, SLOT_SIZE, SLOT_SIZE),
			new ItemSlot(59, 21, SLOT_SIZE, SLOT_SIZE, true).setSlotType(SlotType.OUTPUT_SLOT), };
}