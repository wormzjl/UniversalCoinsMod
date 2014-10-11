package universalcoins;

import universalcoins.gui.HintGuiRenderer;
import universalcoins.items.ItemCoin;
import universalcoins.items.ItemCoinHeap;
import universalcoins.items.ItemLargeCoinBag;
import universalcoins.items.ItemLargeCoinStack;
import universalcoins.items.ItemSeller;
import universalcoins.items.ItemSmallCoinBag;
import universalcoins.items.ItemSmallCoinStack;
import universalcoins.net.UCButtonMessage;
import universalcoins.net.UCTileCardStationMessage;
import universalcoins.net.UCTileTradeStationMessage;
import universalcoins.net.UCVendorServerMessage;
import universalcoins.proxy.CommonProxy;
import universalcoins.tile.TileCardStation;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import universalcoins.util.UCCommand;
import universalcoins.util.UCMobDropEventHandler;
import universalcoins.util.UCItemPricer;
import universalcoins.util.UCPlayerLoginEventHandler;
import universalcoins.util.UCRecipeHelper;
import universalcoins.util.Vending;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
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
import cpw.mods.fml.common.event.FMLInterModComms;
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
 * @author ted_996, notabadminer, AUTOMATIC_MAIDEN
 * 
 **/

@Mod(modid = UniversalCoins.modid, name = UniversalCoins.name, version = UniversalCoins.version)

public class UniversalCoins {
	@Instance("universalcoins")
	public static UniversalCoins instance;
	public static final String modid = "universalcoins";
	public static final String name = "Universal Coins";
	public static final String version = "1.7.2-1.5.7";
	
	public static Boolean autoModeEnabled;
	public static Boolean updateCheck;
	public static Boolean recipesEnabled;
	public static Boolean vendorRecipesEnabled;
	public static Boolean atmRecipeEnabled;
	public static Boolean cardSecurityEnabled;
	public static Boolean collectCoinsInInfinite;
	public static Boolean mobsDropCoins;
	public static Boolean coinsInMineshaft;
	public static Integer mineshaftCoinChance;
	public static Boolean coinsInDungeon;
	public static Integer dungeonCoinChance;
	
	public static Integer mobDropMax;
	public static Integer mobDropChance;
	public static Double itemSellRatio;
	
	public static SimpleNetworkWrapper snw;
	
	public static CreativeTabs tabUniversalCoins = new UCTab("tabUniversalCoins");
	
	@SidedProxy(clientSide="universalcoins.proxy.ClientProxy", serverSide="universalcoins.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		//update
		Property modUpdate = config.get(config.CATEGORY_GENERAL, "Update Check", true);
		modUpdate.comment = "Set to false to remove chat notification of updates.";
		updateCheck = modUpdate.getBoolean(true);
		
		//recipes
		Property recipes = config.get(config.CATEGORY_GENERAL, "Trade Station Recipes", true);
		recipes.comment = "Set to false to disable crafting recipes for selling catalog and trade station.";
		recipesEnabled = recipes.getBoolean(true);
		Property vendorRecipes = config.get(config.CATEGORY_GENERAL, "Vending Block Recipes", true);
		vendorRecipes.comment = "Set to false to disable crafting recipes for vending blocks.";
		vendorRecipesEnabled = vendorRecipes.getBoolean(true);
		Property atmRecipe = config.get(config.CATEGORY_GENERAL, "ATM Recipe", true);
		atmRecipe.comment = "Set to false to disable crafting recipes for ATM.";
		atmRecipeEnabled = atmRecipe.getBoolean(true);
		
		//loot
		Property mobDrops = config.get(config.CATEGORY_GENERAL, "Mob Drops", true);
		mobDrops.comment = "Set to false to disable mobs dropping coins on death.";
		mobsDropCoins = mobDrops.getBoolean(true);
		Property dropAmount = config.get(config.CATEGORY_GENERAL, "Mob Drop Max", 39);
		dropAmount.comment = "Max mob drop stacksize. Minimum 1. Maximum 64. Default 39.";
		mobDropMax = Math.max(1,Math.min(dropAmount.getInt(39),64));
		Property dropChance = config.get(config.CATEGORY_GENERAL, "Mob Drop Chance", 3);
		dropChance.comment = "Chance of a mob dropping coins. Lower number means higher chance. Minimum 0 (always drop). Default 3 (1 in 4 chance).";
		mobDropChance = Math.max(0,Math.min(dropChance.getInt(3),100));
		Property mineshaftCoins = config.get(config.CATEGORY_GENERAL, "Mineshaft CoinBag", true);
		mineshaftCoins.comment = "Set to false to disable coinbag spawning in mineshaft chests.";
		coinsInMineshaft = mineshaftCoins.getBoolean(true);
		Property mineshaftCoinRate = config.get(config.CATEGORY_GENERAL, "Mineshaft CoinBag Spawnrate", 20);
		mineshaftCoinRate.comment = "Rate of coinbag spawning in mineshaft chests. Higher value equals more common. Default is 20.";
		mineshaftCoinChance = Math.max(1,Math.min(mineshaftCoinRate.getInt(20),100));
		Property dungeonCoins = config.get(config.CATEGORY_GENERAL, "Dungeon CoinBag", true);
		dungeonCoins.comment = "Set to false to disable coinbag spawning in dungeon chests.";
		coinsInDungeon = dungeonCoins.getBoolean(true);
		Property dungeonCoinRate = config.get(config.CATEGORY_GENERAL, "Dungeon CoinBag Spawnrate", 20);
		dungeonCoinRate.comment = "Rate of coinbag spawning in dungeon chests. Higher value equals more common. Default is 20.";
		dungeonCoinChance = Math.max(1,Math.min(dungeonCoinRate.getInt(20),100));
		
		//features		
		Property autoMode = config.get(config.CATEGORY_GENERAL, "Auto mode enabled", true);
		autoMode.comment = "Set to false to disable the ability to automatically buy or sell items.";
		autoModeEnabled = autoMode.getBoolean(true);
		Property collectInfinite = config.get(config.CATEGORY_GENERAL, "Collect infinite", true);
		collectInfinite.comment = "Set to false to disable collecting coins when vending blocks are set to infinite mode.";
		collectCoinsInInfinite = collectInfinite.getBoolean(true);
		Property sellRatio = config.get(config.CATEGORY_GENERAL, "Sell Ratio", 1.0);
		sellRatio.comment = "Ratio of sell price to buy price. Set to less than 1.0 to give players a percentage of the full buy price when selling an item. (Range: 0.1 - 1.0)";
		itemSellRatio = Math.max(0.1,Math.min(sellRatio.getDouble(1.0),1.0));	
		config.save();
		if (mobsDropCoins) {
			MinecraftForge.EVENT_BUS.register(new UCMobDropEventHandler());
		}
		
		if (updateCheck) {
			FMLCommonHandler.instance().bus().register(new  UCPlayerLoginEventHandler());
		}
		
		//network packet handling
	    snw = NetworkRegistry.INSTANCE.newSimpleChannel(modid); 
	    snw.registerMessage(UCButtonMessage.class, UCButtonMessage.class, 0, Side.SERVER);
	    snw.registerMessage(UCVendorServerMessage.class, UCVendorServerMessage.class, 1, Side.SERVER);
	    snw.registerMessage(UCTileTradeStationMessage.class, UCTileTradeStationMessage.class, 2, Side.CLIENT);
	    snw.registerMessage(UCTileCardStationMessage.class, UCTileCardStationMessage.class, 3, Side.CLIENT);
	    
	    //update check using versionchecker
	    //FMLInterModComms.sendRuntimeMessage(modid, "VersionChecker", "addVersionCheck", "https://raw.githubusercontent.com/notabadminer/UniversalCoinsMod/master/version.json");
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
		if (vendorRecipesEnabled){
			UCRecipeHelper.addVendingBlockRecipes();
		}
		if (atmRecipeEnabled){
			UCRecipeHelper.addCardStationRecipes();
		}
		if (coinsInMineshaft) {
			ChestGenHooks.getInfo(ChestGenHooks.MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(new ItemStack(proxy.itemLargeCoinBag), 2, 64, mineshaftCoinChance));
		}
		if (coinsInDungeon) {
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(new ItemStack(proxy.itemLargeCoinBag), 2, 64, dungeonCoinChance));
		}
		
		GameRegistry.registerTileEntity(TileTradeStation.class, "TileTradeStation");
		GameRegistry.registerTileEntity(TileVendor.class, "TileVendor");
		GameRegistry.registerTileEntity(TileCardStation.class, "TileCardStation");
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
