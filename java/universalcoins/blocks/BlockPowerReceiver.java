package universalcoins.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import universalcoins.UniversalCoins;
import universalcoins.tile.TilePowerReceiver;

public class BlockPowerReceiver extends BlockContainer {

	public BlockPowerReceiver() {
		super(new Material(MapColor.stoneColor));
		setHardness(3.0F);
		setCreativeTab(UniversalCoins.tabUniversalCoins);
		setResistance(30.0F);
		setBlockTextureName("universalcoins:blockPowerReceiver");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7,
			float par8, float par9) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null && te instanceof TilePowerReceiver) {
			TilePowerReceiver tentity = (TilePowerReceiver) te;
			if (player.getCommandSenderName().matches(tentity.blockOwner)) {
				player.openGui(UniversalCoins.instance, 0, world, x, y, z);
			}
		}
		return true;
	}

	@Override
	public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
		world.setBlockToAir(x, y, z);
		onBlockDestroyedByExplosion(world, x, y, z, explosion);
		EntityItem entityItem = new EntityItem(world, x, y, z, new ItemStack(this, 1));
		if (!world.isRemote)
			world.spawnEntityInWorld(entityItem);
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		if (world.isRemote)
			return;
		int rotation = MathHelper.floor_double((double) ((player.rotationYaw * 4.0f) / 360F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rotation, 2);
		TileEntity te = world.getTileEntity(x, y, z);
		if (te != null) {
			((TilePowerReceiver) te).blockOwner = player.getCommandSenderName();
			((TilePowerReceiver) te).resetPowerDirection();
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TilePowerReceiver();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
    		TilePowerReceiver tileEntity = (TilePowerReceiver) world.getTileEntity(x, y, z);
    		tileEntity.resetPowerDirection();
	}
}