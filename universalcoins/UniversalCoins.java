package universalcoins;

import universalcoins.net.UCButtonMessage;
import universalcoins.net.UCTileEntityMessage;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

/**
 * UniversalCoins, Sell all your extra blocks and buy more!!! Create a trading economy, jobs, whatever.
 * 
 * @author ted_996, notabadminer
 * 
 **/

@Mod(modid = UniversalCoins.modid, name = UniversalCoins.name, version = UniversalCoins.version)

public class UniversalCoins {
	@Instance("universalcoins")
	public static UniversalCoins instance;
	public static final String modid = "universalcoins";
	public static final String name = "Universal Coins";
	public static final String version = "1.7.2-1.5.3";
	
	public static Item itemCoin;
	public static Item itemSmallCoinStack;
	public static Item itemLargeCoinStack;
	public static Item itemCoinHeap; //TODO removal in 1.5.4
	public static Item itemSmallCoinBag;
	public static Item itemLargeCoinBag;
	public static Item itemSeller;
	//public static Item itemCard;
	public static Item itemWrench;
	
	public static Block blockTradeStation;
	
	public static Boolean autoModeEnabled;
	public static Boolean updateCheck;
	public static Boolean recipesEnabled;
	public static Boolean wrenchEnabled;
	
	public static SimpleNetworkWrapper snw; 
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		autoModeEnabled = config.get(config.CATEGORY_GENERAL, "Auto mode enabled", true).getBoolean(true);
		updateCheck = config.get(config.CATEGORY_GENERAL, "Update Check", true).getBoolean(true);
		recipesEnabled = config.get(config.CATEGORY_GENERAL, "CraftingRecipes enabled", true).getBoolean(true);
		wrenchEnabled = config.get(config.CATEGORY_GENERAL, "Wrench enabled", true).getBoolean(true);
		config.save();
	    FMLCommonHandler.instance().bus().register(new  UCEventHandler());
	    snw = NetworkRegistry.INSTANCE.newSimpleChannel(modid); 
	    snw.registerMessage(UCButtonMessage.class, UCButtonMessage.class, 0, Side.SERVER);
	    snw.registerMessage(UCTileEntityMessage.class, UCTileEntityMessage.class, 1, Side.CLIENT);
	}
	
	@EventHandler
	public void postInitialise(FMLPostInitializationEvent event) {
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		itemCoin = new ItemCoin().setUnlocalizedName("itemCoin");
		itemSmallCoinStack = new ItemSmallCoinStack().setUnlocalizedName("itemSmallCoinStack");
		itemLargeCoinStack = new ItemLargeCoinStack().setUnlocalizedName("itemLargeCoinStack");
		itemCoinHeap = new ItemCoinHeap().setUnlocalizedName("itemCoinHeap"); //TODO removal in 1.5.4
		itemSmallCoinBag = new ItemSmallCoinBag().setUnlocalizedName("itemSmallCoinBag");
		itemLargeCoinBag = new ItemLargeCoinBag().setUnlocalizedName("itemLargeCoinBag");
		//itemCard = new ItemUCCard().setUnlocalizedName("itemUCCard");
		itemSeller = new ItemSeller().setUnlocalizedName("itemSeller");
		itemWrench = new ItemWrench().setUnlocalizedName("itemWrench");
		blockTradeStation = new BlockTradeStation().setBlockName("blockTradeStation");
		
		GameRegistry.registerItem(itemCoin, itemCoin.getUnlocalizedName());
		GameRegistry.registerItem(itemSmallCoinStack, itemSmallCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemLargeCoinStack, itemLargeCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemCoinHeap, itemCoinHeap.getUnlocalizedName()); //TODO removal in 1.5.4
		GameRegistry.registerItem(itemSmallCoinBag, itemSmallCoinBag.getUnlocalizedName());
		GameRegistry.registerItem(itemLargeCoinBag, itemLargeCoinBag.getUnlocalizedName());
		//GameRegistry.registerItem(itemCard, itemCard.getUnlocalizedName());
		GameRegistry.registerItem(itemSeller, itemSeller.getUnlocalizedName());
		if (wrenchEnabled) GameRegistry.registerItem(itemWrench, itemWrench.getUnlocalizedName());
		GameRegistry.registerBlock(blockTradeStation, "blockTradeStation").getUnlocalizedName();
		
		UCRecipeHelper.addCoinRecipes();
		if (recipesEnabled) {
			UCRecipeHelper.addTradeStationRecipe();
		}
		if (wrenchEnabled) {
			UCRecipeHelper.addWrenchRecipe();
		}
		
		GameRegistry.registerTileEntity(UCTileEntity.class, "UCTileEntity");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new UCGuiHandler());		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	    UCItemPricer.initializeConfigs();
	    UCItemPricer.loadConfigs();	
	}
}
