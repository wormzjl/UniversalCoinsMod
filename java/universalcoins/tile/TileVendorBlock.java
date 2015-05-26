package universalcoins.tile;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.StatCollector;

public class TileVendorBlock extends TileVendor {

String signText[] = {"","","",""};
	
	public void updateSigns() {

		if (inventory[itemTradeSlot] != null) {
			signText[0] = sellMode ? "Selling" : "Buying";
			//add out of stock notification if not infinite and no stock found
			if (!infiniteMode && sellMode && ooStockWarning) {
				signText[0] = (StatCollector.translateToLocal("sign.warning.stock"));
			}
			//add out of coins notification if buying and no funds available
			if (!sellMode && ooCoinsWarning && !infiniteMode) {
				signText[0] = (StatCollector.translateToLocal("sign.warning.coins"));
			}
			//add inventory full notification
			if (!sellMode && inventoryFullWarning) {
				signText[0] = (StatCollector.translateToLocal("sign.warning.inventoryfull"));
			}
			signText[1] = inventory[itemTradeSlot].getDisplayName();
			if (inventory[itemTradeSlot].isItemEnchanted()) {
				signText[2] = "";
				NBTTagList tagList = inventory[itemTradeSlot].getEnchantmentTagList();
				for (int i = 0; i < tagList.tagCount(); i++) {
					NBTTagCompound enchant = ((NBTTagList) tagList).getCompoundTagAt(i);
					signText[2] = signText[2].concat(Enchantment.enchantmentsList[enchant
							.getInteger("id")].getTranslatedName(enchant
							.getInteger("lvl")) + ", ");
				}
			} else signText[2] = "";
			signText[3] = "Price: " + itemPrice;
			
			//find and update all signs
			TileEntity te;
			te = super.worldObj.getTileEntity(xCoord  + 1, yCoord - 1, zCoord);
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				} 
			te = super.worldObj.getTileEntity(xCoord - 1, yCoord - 1, zCoord);
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				}
			te = super.worldObj.getTileEntity(xCoord, yCoord - 1, zCoord - 1);
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				} 
			te = super.worldObj.getTileEntity(xCoord, yCoord - 1, zCoord + 1);
				if (te != null && te instanceof TileUCSign) {
					TileUCSign tesign = (TileUCSign) te;
					tesign.signText = this.signText;
					tesign.updateSign();
					tesign.markDirty();
				}
		}
	}
}
