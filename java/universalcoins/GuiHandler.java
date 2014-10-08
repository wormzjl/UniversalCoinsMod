package universalcoins;

import universalcoins.gui.CardStationGUI;
import universalcoins.gui.TradeStationGUI;
import universalcoins.gui.VendorGUI;
import universalcoins.gui.VendorSaleGUI;
import universalcoins.gui.VendorWrenchGUI;
import universalcoins.inventory.ContainerCardStation;
import universalcoins.inventory.ContainerTradeStation;
import universalcoins.inventory.ContainerVendor;
import universalcoins.inventory.ContainerVendorSale;
import universalcoins.inventory.ContainerVendorWrench;
import universalcoins.tile.TileCardStation;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
        	if(player.getHeldItem() != null && player.getHeldItem().getItem() == UniversalCoins.proxy.itemVendorWrench) {
        		return new ContainerVendorWrench(player.inventory, (TileVendor) tileEntity);
        	}
        	if(((TileVendor) tileEntity).blockOwner == null || 
        			((TileVendor) tileEntity).blockOwner.contentEquals(player.getDisplayName())) {
        		return new ContainerVendor(player.inventory, (TileVendor) tileEntity);
        	} else return new ContainerVendorSale(player.inventory, (TileVendor) tileEntity);
        }
        if(tileEntity instanceof TileCardStation){
            return new ContainerCardStation(player.inventory, (TileCardStation) tileEntity);
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
        	if(player.getHeldItem() != null && player.getHeldItem().getItem() == UniversalCoins.proxy.itemVendorWrench) {
        		return new VendorWrenchGUI(player.inventory, (TileVendor) tileEntity);
        	}
        	if(((TileVendor) tileEntity).blockOwner == null || 
        			((TileVendor) tileEntity).blockOwner.contentEquals(player.getDisplayName())) {
        		return new VendorGUI(player.inventory, (TileVendor) tileEntity);
        	} else return new VendorSaleGUI(player.inventory, (TileVendor) tileEntity);
        }
        if(tileEntity instanceof TileCardStation){
            return new CardStationGUI(player.inventory, (TileCardStation) tileEntity);
        }
        return null;
		}	
}
