package universalcoins;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import universalcoins.net.PacketPipeline;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = UniversalCoins.modid, name = "Universal Coins", version = "1.5.0")

public class UniversalCoins {
	@Instance("universalcoins")
	public static UniversalCoins instance;
	public static final String modid = "universalcoins";
	
	public static Item itemCoin;
	public static Item itemSmallCoinStack;
	public static Item itemLargeCoinStack;
	public static Item itemCoinHeap;
	public static Item itemSeller;
	
	public static Block blockTradeStation;
	
	// The packet pipeline
    public static final PacketPipeline packetPipeline = new PacketPipeline();

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent evt) {
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	    packetPipeline.initalise();		
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
		packetPipeline.postInitialise();
	    UCItemPricer.initializeConfigs();
	    UCItemPricer.loadConfigs();	
	}
}
