package universalcoins.integration.craftguide;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import universalcoins.UniversalCoins;
import uristqwerty.CraftGuide.api.CraftGuideAPIObject;
import uristqwerty.CraftGuide.api.ItemSlot;
import uristqwerty.CraftGuide.api.RecipeGenerator;
import uristqwerty.CraftGuide.api.RecipeProvider;
import uristqwerty.CraftGuide.api.RecipeTemplate;
import uristqwerty.CraftGuide.api.Slot;
import uristqwerty.CraftGuide.api.SlotType;

public class CGRecipeVendingFrameTextureChange extends CraftGuideAPIObject implements RecipeProvider {
	private final int SLOT_SIZE = 16;
	private final Slot[] CRAFTING_SLOTS = new ItemSlot[] { new ItemSlot(3, 3, 16, 16), new ItemSlot(21, 3, 16, 16),
			new ItemSlot(39, 3, 16, 16), new ItemSlot(3, 21, 16, 16), new ItemSlot(21, 21, 16, 16),
			new ItemSlot(39, 21, 16, 16), new ItemSlot(3, 39, 16, 16), new ItemSlot(21, 39, 16, 16),
			new ItemSlot(39, 39, 16, 16), new ItemSlot(59, 21, 16, 16, true).setSlotType(SlotType.OUTPUT_SLOT) };

	@Override
	public void generateRecipes(RecipeGenerator generator) {
		ItemStack craftingTable = new ItemStack(Blocks.crafting_table);

		RecipeTemplate template = generator.createRecipeTemplate(CRAFTING_SLOTS, craftingTable,
				"craftguide:textures/gui/CraftGuideRecipe.png", 1, 121, "craftguide:textures/gui/CraftGuideRecipe.png",
				82, 121);

		generator
				.addRecipe(template,
						new Object[] { new ItemStack(UniversalCoins.proxy.vendor_frame),
								new ItemStack(Blocks.planks), null, null, null, null, null, null, null,
								new ItemStack(UniversalCoins.proxy.vendor_frame) });
	}

}