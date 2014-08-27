package universalcoins.items;

import java.util.List;

import universalcoins.UniversalCoins;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemUCCard extends Item {
	
	public ItemUCCard() {
		super();
		this.maxStackSize = 1;
		this.setCreativeTab(UniversalCoins.tabUniversalCoins);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		this.itemIcon = par1IconRegister.registerIcon(UniversalCoins.modid + ":" + this.getUnlocalizedName().substring(5));
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		 if( stack.stackTagCompound == null ) {
			 stack.stackTagCompound = new NBTTagCompound();
			 stack.stackTagCompound.setString("Owner", player.getDisplayName());
			 stack.stackTagCompound.setInteger("CoinSum", 0);
		 }
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
		if( stack.stackTagCompound == null ) {
			 stack.stackTagCompound = new NBTTagCompound();
			 stack.stackTagCompound.setString("Owner", player.getDisplayName());
			 stack.stackTagCompound.setInteger("CoinSum", 0);
		} else {
			list.add("Owner: " + stack.stackTagCompound.getString("Owner"));
			list.add("Balance: " + stack.stackTagCompound.getInteger("CoinSum"));
		}
	}
}
