package universalcoins;

import universalcoins.gui.VendorGUI;
import universalcoins.gui.VendorSaleGUI;
import universalcoins.inventory.ContainerTradeStation;
import universalcoins.inventory.ContainerVendor;
import universalcoins.inventory.ContainerVendorSale;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IGuiHandler;

class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity instanceof TileTradeStation){
                return new ContainerTradeStation(player.inventory, (TileTradeStation) tileEntity);
        }
        if(tileEntity instanceof TileVendor){
        	if(((TileVendor) tileEntity).blockOwner == null || 
        			((TileVendor) tileEntity).blockOwner.contentEquals(player.getDisplayName())) {
            return new ContainerVendor(player.inventory, (TileVendor) tileEntity);
        	} else return new ContainerVendorSale(player.inventory, (TileVendor) tileEntity);
    }
        return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity instanceof TileTradeStation){
                return new TradeStationGUI(player.inventory, (TileTradeStation) tileEntity);
        }
        if(tileEntity instanceof TileVendor){
        	if(((TileVendor) tileEntity).blockOwner == null || 
        			((TileVendor) tileEntity).blockOwner.contentEquals(player.getDisplayName())) {
        		return new VendorGUI(player.inventory, (TileVendor) tileEntity);
        	} else return new VendorSaleGUI(player.inventory, (TileVendor) tileEntity);
        }
        return null;
		}	
}
