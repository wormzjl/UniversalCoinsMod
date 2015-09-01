package universalcoins.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import universalcoins.Achievements;
import universalcoins.blocks.BlockBandit;
import universalcoins.blocks.BlockBase;
import universalcoins.blocks.BlockCardStation;
import universalcoins.blocks.BlockPackager;
import universalcoins.blocks.BlockPowerBase;
import universalcoins.blocks.BlockSafe;
import universalcoins.blocks.BlockSignal;
import universalcoins.blocks.BlockTradeStation;
import universalcoins.blocks.BlockUCSign;
import universalcoins.blocks.BlockVendor;
import universalcoins.blocks.BlockVendorFrame;
import universalcoins.items.ItemBlockVendor;
import universalcoins.items.ItemCoin;
import universalcoins.items.ItemEnderCard;
import universalcoins.items.ItemLargeCoinBag;
import universalcoins.items.ItemLargeCoinStack;
import universalcoins.items.ItemLinkCard;
import universalcoins.items.ItemPackage;
import universalcoins.items.ItemSeller;
import universalcoins.items.ItemSmallCoinBag;
import universalcoins.items.ItemSmallCoinStack;
import universalcoins.items.ItemUCCard;
import universalcoins.items.ItemUCGuide;
import universalcoins.items.ItemUCSign;
import universalcoins.items.ItemVendorWrench;
import universalcoins.tile.TileUCSign;
import universalcoins.util.Vending;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	public static Item itemCoin;
	public static Item itemSmallCoinStack;
	public static Item itemLargeCoinStack;
	public static Item itemSmallCoinBag;
	public static Item itemLargeCoinBag;
	public static Item itemSeller;
	public static Item itemUCGuide;
	public static Item itemUCCard;
	public static Item itemEnderCard;
	public static Item itemVendorWrench;
	public static Item itemUCSign;
	public static Item itemLinkCard;
	public static Item itemPackage;

	public static Block blockTradeStation;
	public static Block blockVendor;
	public static Block blockVendorFrame;
	public static Block blockCardStation;
	public static Block blockBase;
	public static Block blockSafe;
	public static Block standing_ucsign;
	public static Block wall_ucsign;
	public static Block blockBandit;
	public static Block blockSignal;
	public static Block blockPackager;
	public static Block blockPowerBase;

	public void registerBlocks() {
		blockTradeStation = new BlockTradeStation().setBlockName("blockTradeStation");
		blockVendor = new BlockVendor(Vending.supports).setBlockName("blockVendor");
		blockVendorFrame = new BlockVendorFrame().setBlockName("blockVendorFrame");
		blockCardStation = new BlockCardStation().setBlockName("blockCardStation");
		blockBase = new BlockBase().setBlockName("blockBase");
		blockSafe = new BlockSafe().setBlockName("blockSafe");
		standing_ucsign = new BlockUCSign(TileUCSign.class, true).setBlockName("standing_ucsign");
		wall_ucsign = new BlockUCSign(TileUCSign.class, false).setBlockName("wall_ucsign");
		blockBandit = new BlockBandit().setBlockName("blockBandit");
		blockSignal = new BlockSignal().setBlockName("blockSignal");
		blockPackager = new BlockPackager().setBlockName("blockPackager");
		blockPowerBase = new BlockPowerBase().setBlockName("blockPowerBase");

		GameRegistry.registerBlock(blockTradeStation, "blockTradeStation").getUnlocalizedName();
		GameRegistry.registerBlock(blockVendor, ItemBlockVendor.class, "blockVendor");
		GameRegistry.registerBlock(blockVendorFrame, "blockVendorFrame").getUnlocalizedName();
		GameRegistry.registerBlock(blockCardStation, "blockCardStation").getUnlocalizedName();
		GameRegistry.registerBlock(blockBase, "blockBase").getUnlocalizedName();
		GameRegistry.registerBlock(blockSafe, "blockSafe").getUnlocalizedName();
		GameRegistry.registerBlock(standing_ucsign, "standing_ucsign").getUnlocalizedName();
		GameRegistry.registerBlock(wall_ucsign, "wall_ucsign").getUnlocalizedName();
		GameRegistry.registerBlock(blockBandit, "blockBandit").getUnlocalizedName();
		GameRegistry.registerBlock(blockSignal, "blockSignal").getUnlocalizedName();
		GameRegistry.registerBlock(blockPackager, "blockPackager").getUnlocalizedName();
		GameRegistry.registerBlock(blockPowerBase, "blockPowerBase").getUnlocalizedName();
	}

	public void registerItems() {
		itemCoin = new ItemCoin().setUnlocalizedName("itemCoin");
		itemSmallCoinStack = new ItemSmallCoinStack().setUnlocalizedName("itemSmallCoinStack");
		itemLargeCoinStack = new ItemLargeCoinStack().setUnlocalizedName("itemLargeCoinStack");
		itemSmallCoinBag = new ItemSmallCoinBag().setUnlocalizedName("itemSmallCoinBag");
		itemLargeCoinBag = new ItemLargeCoinBag().setUnlocalizedName("itemLargeCoinBag");
		itemUCCard = new ItemUCCard().setUnlocalizedName("itemUCCard");
		itemEnderCard = new ItemEnderCard().setUnlocalizedName("itemEnderCard");
		itemSeller = new ItemSeller().setUnlocalizedName("itemSeller");
		itemUCGuide = new ItemUCGuide().setUnlocalizedName("itemGuide");
		itemVendorWrench = new ItemVendorWrench().setUnlocalizedName("itemVendorWrench");
		itemUCSign = new ItemUCSign().setUnlocalizedName("itemUCSign");
		itemLinkCard = new ItemLinkCard().setUnlocalizedName("itemLinkCard");
		itemPackage = new ItemPackage().setUnlocalizedName("itemPackage");

		GameRegistry.registerItem(itemCoin, itemCoin.getUnlocalizedName());
		GameRegistry.registerItem(itemSmallCoinStack, itemSmallCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemLargeCoinStack, itemLargeCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemSmallCoinBag, itemSmallCoinBag.getUnlocalizedName());
		GameRegistry.registerItem(itemLargeCoinBag, itemLargeCoinBag.getUnlocalizedName());
		GameRegistry.registerItem(itemUCCard, itemUCCard.getUnlocalizedName());
		GameRegistry.registerItem(itemEnderCard, itemEnderCard.getUnlocalizedName());
		GameRegistry.registerItem(itemSeller, itemSeller.getUnlocalizedName());
		GameRegistry.registerItem(itemVendorWrench, itemVendorWrench.getUnlocalizedName());
		GameRegistry.registerItem(itemUCSign, itemUCSign.getUnlocalizedName());
		GameRegistry.registerItem(itemLinkCard, itemLinkCard.getUnlocalizedName());
		GameRegistry.registerItem(itemPackage, itemPackage.getUnlocalizedName());
		
		//only register guide if enchiridion2 is loaded
		if (Loader.isModLoaded("Enchiridion2")) {
			GameRegistry.registerItem(itemUCGuide, itemUCGuide.getUnlocalizedName());
		}
	}

	public void registerRenderers() {
		// blank since we don't do anything on the server
	}
	
	public void registerAchievements() {
		Achievements.init();
		Achievements.achCoin.registerStat(); 
		Achievements.achThousand.registerStat(); 
		Achievements.achMillion.registerStat(); 
		Achievements.achBillion.registerStat(); 
		Achievements.achMaxed.registerStat(); 
	}

}
