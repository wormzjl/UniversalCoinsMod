package universalcoins;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.registry.GameData;

public class UCItemPricer {

	private static String ItemsList = GameData.getItemRegistry().getKeys().toString();
	private static String BlocksList = GameData.getBlockRegistry().getKeys().toString();
	private static Map<String, Integer> UCPriceMap = new HashMap<String, Integer>(0);
	private static String configPath = "config/universalcoins/";
	
	public static void initializeConfigs(){
		if (!new File(configPath).exists()) {
			//FMLLog.info("Universal Coins: Building Pricelists");
			buildInitialPricelistHashMap();
			loadDefaults();
			writePriceLists();
		}
	}
	
	public static void loadConfigs() {
		try {
			UCItemPricer.loadPricelists();
		} catch (IOException e) {
			FMLLog.warning("Failed to load config files");
			e.printStackTrace();
		}
	}
	
	public static void loadDefaults() {
		InputStream priceResource = UCItemPricer.class.getResourceAsStream("defaultConfigs/minecraft.cfg");
		if (priceResource == null){
			return;
		}
		String priceString = convertStreamToString(priceResource);
		updateInitialPricelistHashMap(priceString);
	}
	
	private static String convertStreamToString(java.io.InputStream is) {
		//Thanks to Pavel Repin on StackOverflow.
		java.util.Scanner scanner = new java.util.Scanner(is);
		java.util.Scanner s = scanner.useDelimiter("\\A");
		String result =  s.hasNext() ? s.next() : "";
		scanner.close();
		return result;
	}
	
	private static void updateInitialPricelistHashMap(String priceString) {
		StringTokenizer tokenizer = new StringTokenizer(priceString, "\n\r", false);
		while (tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			String[] tempData = token.split("=");
			//FMLLog.info("Universal Coins: Updating UCPricelist: " + tempData[0] + "=" + Integer.valueOf(tempData[1]));
			UCPriceMap.put(tempData[0], Integer.valueOf(tempData[1]));
		}
	}

	public static void buildInitialPricelistHashMap() {
		// Fill UCPriceMap with initial set of blocks and items from current mods
		//call only if config folder is empty
		StringTokenizer tokenizer = new StringTokenizer(ItemsList, ", ", false);
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();
			token = token.replaceAll("\\[","").replaceAll("\\]","");
			UCPriceMap.put(token, new Integer(-1));
		}
		StringTokenizer tokenizer1 = new StringTokenizer(BlocksList, ", ",
				false);
		while (tokenizer1.hasMoreElements()) {
			String token = tokenizer1.nextToken();
			UCPriceMap.put(token, new Integer(-1));
		}
	}
	
public static void loadPricelists() throws IOException {
	//search config file folder for files
	File folder = new File(configPath);
	File[] configList = folder.listFiles();	
	//load those files into hashmap(UCPriceMap)
	for (int i = 0; i < configList.length; i++) {
	      if (configList[i].isFile()) {
	    	  //FMLLog.info("Universal Coins: Loading Pricelist " + configList[i]);
	    	  BufferedReader br = new BufferedReader(new FileReader(configList[i]));
	    	  String tempString = "";
	    	  while ((tempString = br.readLine()) != null) {
	    		  String[] tempData = tempString.split("=");
	    		  UCPriceMap.put(tempData[0], Integer.valueOf(tempData[1]));
	    	  }
	    	  br.close();
	      }  
		}
	}

	public static void writePriceLists() {
		//write config set from item hashmap
		Set set = UCPriceMap.entrySet();
		Iterator i = set.iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			String keyname = (String) me.getKey();
			String[] tempmodname = keyname.split("\\W", 2);
			String modname = tempmodname[0] + ".cfg";
			Path pathToFile = Paths.get(configPath + modname);
			try {
				Files.createDirectories(pathToFile.getParent());
			} catch (IOException e) {
				FMLLog.warning("Failed to create config file folders");
			}
			File modconfigfile = new File(configPath + modname);
			if(!modconfigfile.exists()) {
				try {
					modconfigfile.createNewFile();
					} 
				catch (IOException e) {
					FMLLog.warning("Failed to create config file");
			}
			}
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(modconfigfile, true)));
				out.println(me.getKey() + "=" + me.getValue());
				out.close();
				}
			catch (IOException e) {
				FMLLog.warning("Failed to append to config file");
			}
			
		}
	}
	
	public static int getItemPrice(ItemStack itemStack) {
		if (itemStack == null) {
			//FMLLog.warning("itemstack is null");
			return -1;
		}
		return getItemPrice(itemStack.getItem());

	}

	private static int getItemPrice(Item item) {
		Integer ItemPrice = -1;
		String itemName = GameData.getItemRegistry().getNameForObject(item);
		if (UCPriceMap.get(itemName) != null) {
			ItemPrice = UCPriceMap.get(itemName);
			//FMLLog.info("ItemPrice: " + ItemPrice);
		}
		return ItemPrice;
	}

	public static ItemStack getRevenueStack(int itemPrice) {
		if (itemPrice <= 64) {
			return new ItemStack(UniversalCoins.itemCoin, itemPrice);
		} else if (itemPrice <= 9 * 64) {
			return new ItemStack(UniversalCoins.itemSmallCoinStack,
					itemPrice / 9);
		} else if (itemPrice <= 81 * 64) {
			return new ItemStack(UniversalCoins.itemLargeCoinStack,
					itemPrice / 81);
		} else {
			return new ItemStack(UniversalCoins.itemCoinHeap, Math.min(
					itemPrice / 729, 64));
		}
	}

}
