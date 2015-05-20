package universalcoins.tile;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;

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
}
