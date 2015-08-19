package universalcoins.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBook;
import universalcoins.UniversalCoins;

public class ItemUCGuide extends ItemBook {
	
	public ItemUCGuide() {
		super();
		setCreativeTab(UniversalCoins.tabUniversalCoins);
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon(UniversalCoins.MODID + ":"
				+ this.getUnlocalizedName().substring(5));
	}

}
