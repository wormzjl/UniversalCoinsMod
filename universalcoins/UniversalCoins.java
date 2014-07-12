package universalcoins;

import universalcoins.net.GuiButtonMessage;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
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
	public static final String version = "1.5.3";
	
	public static Item itemCoin;
	public static Item itemSmallCoinStack;
	public static Item itemLargeCoinStack;
	public static Item itemCoinHeap;
	public static Item itemSeller;
	
	public static Block blockTradeStation;
	
	public static Boolean autoModeEnabled;
	public static Boolean updateCheck;
	
	public static SimpleNetworkWrapper snw; 
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		autoModeEnabled = config.get(config.CATEGORY_GENERAL, "Auto mode enabled", true).getBoolean(false);
		updateCheck = config.get(config.CATEGORY_GENERAL, "Update Check", true).getBoolean(false);
		config.save();
	    FMLCommonHandler.instance().bus().register(new  UCEventHandler());
	    snw = NetworkRegistry.INSTANCE.newSimpleChannel(modid); 
	    snw.registerMessage(GuiButtonMessage.Handler.class, GuiButtonMessage.class, 0, Side.SERVER);
	    //snw.registerMessage(TEUpdateMessage.Handler.class, TEUpdateMessage.class, 1, Side.CLIENT); 
	}
	
	@EventHandler
	public void postInitialise(FMLPostInitializationEvent event) {
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		itemCoin = new ItemCoin().setUnlocalizedName("itemCoin");
		itemSmallCoinStack = new ItemSmallCoinStack().setUnlocalizedName("itemSmallCoinStack");
		itemLargeCoinStack = new ItemLargeCoinStack().setUnlocalizedName("itemLargeCoinStack");
		itemCoinHeap = new ItemCoinHeap().setUnlocalizedName("itemCoinHeap");
		itemSeller = new ItemSeller().setUnlocalizedName("itemSeller");
		blockTradeStation = new BlockTradeStation().setBlockName("blockTradeStation");
		
		GameRegistry.registerItem(itemCoin, itemCoin.getUnlocalizedName());
		GameRegistry.registerItem(itemSmallCoinStack, itemSmallCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemLargeCoinStack, itemLargeCoinStack.getUnlocalizedName());
		GameRegistry.registerItem(itemCoinHeap, itemCoinHeap.getUnlocalizedName());
		GameRegistry.registerItem(itemSeller, itemSeller.getUnlocalizedName());
		GameRegistry.registerBlock(blockTradeStation, "blockTradeStation").getUnlocalizedName();
		
		UCRecipeHelper.addCoinRecipes();
		UCRecipeHelper.addTradeStationRecipe();
		
		GameRegistry.registerTileEntity(UCTileEntity.class, "UCTileEntity");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new UCGuiHandler());		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	    UCItemPricer.initializeConfigs();
	    UCItemPricer.loadConfigs();	
	}
}
