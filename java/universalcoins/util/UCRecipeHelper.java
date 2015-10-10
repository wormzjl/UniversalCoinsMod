package universalcoins.util;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import universalcoins.UniversalCoins;

public class UCRecipeHelper {

	private static ItemStack oneSeller = new ItemStack(UniversalCoins.proxy.itemSeller);
	private static ItemStack oneCoin = new ItemStack(UniversalCoins.proxy.itemCoin);
	private static ItemStack oneSStack = new ItemStack(UniversalCoins.proxy.itemSmallCoinStack);
	private static ItemStack oneLStack = new ItemStack(UniversalCoins.proxy.itemLargeCoinStack);
	private static ItemStack oneSSack = new ItemStack(UniversalCoins.proxy.itemSmallCoinBag);
	private static ItemStack oneLSack = new ItemStack(UniversalCoins.proxy.itemLargeCoinBag);

	public static void addCoinRecipes() {
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.proxy.itemCoin, 9), new Object[] { oneSStack });
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.proxy.itemSmallCoinStack, 9),
				new Object[] { oneLStack });
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.proxy.itemLargeCoinStack, 9),
				new Object[] { oneSSack });
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.proxy.itemSmallCoinBag, 9),
				new Object[] { oneLSack });

		GameRegistry.addShapelessRecipe(oneSStack,
				new Object[] { oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin, oneCoin });
		GameRegistry.addShapelessRecipe(oneLStack, new Object[] { oneSStack, oneSStack, oneSStack, oneSStack, oneSStack,
				oneSStack, oneSStack, oneSStack, oneSStack });
		GameRegistry.addShapelessRecipe(oneSSack, new Object[] { oneLStack, oneLStack, oneLStack, oneLStack, oneLStack,
				oneLStack, oneLStack, oneLStack, oneLStack });
		GameRegistry.addShapelessRecipe(oneLSack, new Object[] { oneSSack, oneSSack, oneSSack, oneSSack, oneSSack,
				oneSSack, oneSSack, oneSSack, oneSSack });
	}

	public static void addTradeStationRecipe() {
		GameRegistry.addShapedRecipe(oneSeller, new Object[] { "LGE", "PPP", 'L', Items.leather, 'G', Items.gold_ingot,
				'E', Items.ender_pearl, 'P', Items.paper });
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockTradeStation), new Object[] { "IGI", "ICI",
				"III", 'I', Items.iron_ingot, 'G', Items.gold_ingot, 'C', UniversalCoins.proxy.itemSeller });
	}

	public static void addVendingBlockRecipes() {
		for (int i = 0; i < Vending.supports.length; i++) {
			GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockVendor, 1, i),
					new Object[] { "XXX", "XRX", "*G*", 'X', Blocks.glass, 'G', Items.gold_ingot, 'R', Items.redstone,
							'*', Vending.reagents[i] });
		}
	}

	public static void addVendingFrameRecipes() {
		GameRegistry.addRecipe(new RecipeVendingFrame());
		RecipeSorter.register("universalcoins:vendingframe", RecipeVendingFrame.class, RecipeSorter.Category.SHAPED,
				"after:minecraft:shaped");
	}

	public static void addSignRecipes() {
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.proxy.itemUCSign),
				new Object[] { new ItemStack(Items.sign) });
		GameRegistry.addShapelessRecipe(new ItemStack(Items.sign),
				new Object[] { new ItemStack(UniversalCoins.proxy.itemUCSign) });
	}

	public static void addCardStationRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockCardStation), new Object[] { "III", "ICI",
				"III", 'I', Items.iron_ingot, 'C', UniversalCoins.proxy.itemSmallCoinBag });
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockBase),
				new Object[] { "III", "ICI", "III", 'I', Items.iron_ingot, 'C', UniversalCoins.proxy.itemCoin });
	}

	public static void addBlockSafeRecipe() {
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockSafe),
				new Object[] { "III", "IEI", "III", 'I', Items.iron_ingot, 'E', UniversalCoins.proxy.itemEnderCard });
	}

	public static void addEnderCardRecipes() {
		GameRegistry.addRecipe(new RecipeEnderCard());
		RecipeSorter.register("universalcoins:endercard", RecipeEnderCard.class, RecipeSorter.Category.SHAPED,
				"after:minecraft:shaped");
	}

	public static void addBanditRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockBandit), new Object[] { "IGI", "IRI",
				"III", 'I', Items.iron_ingot, 'R', Items.redstone, 'G', Items.gold_ingot });
	}

	public static void addSignalRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockSignal),
				new Object[] { "IXI", "XRX", "IXI", 'I', Items.iron_ingot, 'R', Items.redstone });
	}

	public static void addLinkCardRecipes() {
		GameRegistry.addShapelessRecipe(new ItemStack(UniversalCoins.proxy.itemLinkCard),
				new Object[] { Items.paper, Items.paper, Items.ender_pearl });
	}

	public static void addPackagerRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockPackager), new Object[] { "IPI", "SRS",
				"IRI", 'I', Items.iron_ingot, 'R', Items.redstone, 'S', Items.string, 'P', Items.paper });
	}

	public static void addPlankTextureRecipes() {
		GameRegistry.addRecipe(new RecipePlankTextureChange());
		RecipeSorter.register("universalcoins:plankchange", RecipePlankTextureChange.class,
				RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
	}

	public static void addPowerBaseRecipe() {
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockPowerBase), new Object[] { "III", "MRM",
				"III", 'I', Items.iron_ingot, 'R', Blocks.redstone_block, 'M', Items.redstone });
	}
	public static void addPowerReceiverRecipe() {
		GameRegistry.addShapedRecipe(new ItemStack(UniversalCoins.proxy.blockPowerReceiver), new Object[] { "III",
				"MRM", "III", 'I', Items.iron_ingot, 'R', Blocks.redstone_block, 'M', new ItemStack(Items.dye, 1, 4) });
	}
}