package universalcoins.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntitySign;
import universalcoins.UniversalCoins;
import universalcoins.net.UCTileSignMessage;

public class TileUCSign extends TileEntitySign {
		
	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
        //this.field_145916_j = false;
        super.readFromNBT(p_145839_1_);

        for (int i = 0; i < 4; ++i) {
            this.signText[i] = p_145839_1_.getString("Text" + (i + 1));
        }
    }
	
	public void updateSign() {
		super.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	@Override
	public Packet getDescriptionPacket() {
        String[] astring = new String[4];
        System.arraycopy(this.signText, 0, astring, 0, 4);
        return UniversalCoins.snw.getPacketFrom(new UCTileSignMessage(this.xCoord, this.yCoord, this.zCoord, astring));
    }
}
