package universalcoins.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import universalcoins.tile.TileVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVendorWrench extends Container {
	private TileVendor tileEntity;
	private int lastUserCoinSum;
	private int lastItemPrice;
	
	public ContainerVendorWrench(InventoryPlayer inventoryPlayer, TileVendor tEntity) {
		tileEntity = tEntity;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	
	/**
     * Looks for changes made in the container, sends them to every listener.
     */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.lastUserCoinSum != this.tileEntity.userCoinSum 
            		|| this.lastItemPrice != this.tileEntity.itemPrice) {
                //update
            	tileEntity.updateTE();
            }

		this.lastUserCoinSum = this.tileEntity.userCoinSum;
		this.lastItemPrice = this.tileEntity.itemPrice;
        }
	}
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            //this.tileEntity.autoMode = par2;
        }
    }

}
