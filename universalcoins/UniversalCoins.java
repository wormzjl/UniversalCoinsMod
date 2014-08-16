package universalcoins;

import universalcoins.gui.HintGuiRenderer;
import universalcoins.items.ItemCoin;
import universalcoins.items.ItemCoinHeap;
import universalcoins.items.ItemLargeCoinBag;
import universalcoins.items.ItemLargeCoinStack;
import universalcoins.items.ItemSeller;
import universalcoins.items.ItemSmallCoinBag;
import universalcoins.items.ItemSmallCoinStack;
import universalcoins.items.ItemWrench;
import universalcoins.net.UCButtonMessage;
import universalcoins.net.UCTileStationMessage;
import universalcoins.net.UCTileVendorMessage;
import universalcoins.net.UCVendorServerMessage;
import universalcoins.proxy.CommonProxy;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import universalcoins.util.UCCommand;
import universalcoins.util.UCEventHandler;
import universalcoins.util.UCItemPricer;
import universalcoins.util.UCRecipeHelper;
import universalcoins.util.Vending;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
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
	public static final String version = "1.7.2-1.5.4";
	
	public static Boolean autoModeEnabled;
	public static Boolean updateCheck;
	public static Boolean recipesEnabled;
	public static Boolean wrenchEnabled;
	public static Boolean vendorRecipesEnabled;
	public static Boolean dropCoinsInInfinite;
	
	public static SimpleNetworkWrapper snw;
	
	public static CreativeTabs tabUniversalCoins = new UCTab("tabUniversalCoins");
	
	@SidedProxy(clientSide="universalcoins.proxy.ClientProxy", serverSide="universalcoins.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		Property autoMode = config.get(config.CATEGORY_GENERAL, "Auto mode enabled", true);
		autoMode.comment = "Set to false to disable the ability to automatically buy or sell items.";
		autoModeEnabled = autoMode.getBoolean(true);
		Property modUpdate = config.get(config.CATEGORY_GENERAL, "Update Check", true);
		modUpdate.comment = "Set to false to remove chat notification of updates.";
		updateCheck = modUpdate.getBoolean(true);
		Property recipes = config.get(config.CATEGORY_GENERAL, "CraftingRecipes enabled", true);
		recipes.comment = "Set to false to disable crafting recipes for selling catalog and trade station.";
		recipesEnabled = recipes.getBoolean(true);
		Property wrench = config.get(config.CATEGORY_GENERAL, "Wrench enabled", true);
		wrench.comment = "Set to false to disable wrench. Use this if your world already has too many wrenches.";
		wrenchEnabled = wrench.getBoolean(true);
		Property vendorRecipes = config.get(config.CATEGORY_GENERAL, "Vending Block Recipes", true);
		vendorRecipes.comment = "Set to false to disable crafting recipes for vending blocks.";
		vendorRecipesEnabled = vendorRecipes.getBoolean(true);
		Property dropInfinite = config.get(config.CATEGORY_GENERAL, "Do not collect coins in Infinite", false);
		dropInfinite.comment = "Set to true to disable collecting coins when blocks are set to infinite mode.";
		dropCoinsInInfinite = dropInfinite.getBoolean(false);
		config.save();
	    FMLCommonHandler.instance().bus().register(new  UCEventHandler());
	    snw = NetworkRegistry.INSTANCE.newSimpleChannel(modid); 
	    snw.registerMessage(UCButtonMessage.class, UCButtonMessage.class, 0, Side.SERVER);
	    snw.registerMessage(UCVendorServerMessage.class, UCVendorServerMessage.class, 1, Side.SERVER);
	    snw.registerMessage(UCTileVendorMessage.class, UCTileVendorMessage.class, 2, Side.CLIENT);
	    snw.registerMessage(UCTileStationMessage.class, UCTileStationMessage.class, 3, Side.CLIENT);
	}
	
	@EventHandler
	public void postInitialise(FMLPostInitializationEvent event) {
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.registerBlocks();
		proxy.registerItems();
		proxy.registerRenderers();
		
		UCRecipeHelper.addCoinRecipes();
		if (recipesEnabled) {
			UCRecipeHelper.addTradeStationRecipe();
		}
		if (wrenchEnabled) {
			UCRecipeHelper.addWrenchRecipe();
		}
		
		GameRegistry.registerTileEntity(TileTradeStation.class, "TileTradeStation");
		GameRegistry.registerTileEntity(TileVendor.class, "TileVendor");
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
	    UCItemPricer.initializeConfigs();
	    UCItemPricer.loadConfigs();	
	}
	
	@EventHandler
    public void serverStart(FMLServerStartingEvent event) {
		MinecraftServer server = MinecraftServer.getServer();
		ICommandManager command = server.getCommandManager();
		ServerCommandManager manager = (ServerCommandManager) command;
		manager.registerCommand(new UCCommand());
	}

}
