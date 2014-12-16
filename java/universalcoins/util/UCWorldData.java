package universalcoins.util;

import cpw.mods.fml.common.FMLLog;
import universalcoins.UniversalCoins;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class UCWorldData extends WorldSavedData {
	
	
	final static String key = UniversalCoins.modid;
	   
	   public static UCWorldData get(World world) {
	      MapStorage storage = world.mapStorage;
	      UCWorldData result = (UCWorldData)storage.loadData(UCWorldData.class, key);
	      if (result == null) {
	    	  result = new UCWorldData(key);
	    	  storage.setData(key, result);
	      } else {
	      }
	      return result;
	   }
	   
	   private NBTTagCompound data = new NBTTagCompound();

	   public UCWorldData(String tagName) {
	       super(tagName);
	   }

	   @Override
	   public void readFromNBT(NBTTagCompound compound) {
			data = compound.getCompoundTag(key);
	   }

	   @Override
	   public void writeToNBT(NBTTagCompound compound) {
	       compound.setTag(key, data);
	   }

	   public NBTTagCompound getData() {
	       return data;
	   }
}
