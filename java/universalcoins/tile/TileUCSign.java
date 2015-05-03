package universalcoins.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntitySign;

public class TileUCSign extends TileEntitySign {
	
	@Override
	public void readFromNBT(NBTTagCompound p_145839_1_) {
        super.readFromNBT(p_145839_1_);

        for (int i = 0; i < 4; ++i) {
            this.signText[i] = p_145839_1_.getString("Text" + (i + 1));
        }
    }

}
