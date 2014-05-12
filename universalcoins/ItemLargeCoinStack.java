package universalcoins;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

class ItemLargeCoinStack extends Item {

	public ItemLargeCoinStack() {
		super();
		this.setUnlocalizedName("itemLargeCoinStack");
		this.setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		this.itemIcon = par1IconRegister.registerIcon(UniversalCoins.modid + ":" +
													  this.getUnlocalizedName().substring(5));
	}

}
