package universalcoins.blocks;

import universalcoins.tile.TileUCSign;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockUCSign extends BlockSign {
	
	 private Class field_149968_a;
	 private boolean field_149967_b;

	public BlockUCSign(Class p_i45426_1_, boolean p_i45426_2_) {
		super(p_i45426_1_, p_i45426_2_);
        this.field_149967_b = p_i45426_2_;
        this.field_149968_a = p_i45426_1_;
        float f = 0.25F;
        float f1 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
	}
	
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        try
        {
            return (TileUCSign)this.field_149968_a.newInstance();
        }
        catch (Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

}
