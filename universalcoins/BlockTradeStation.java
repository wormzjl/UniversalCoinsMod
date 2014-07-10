package universalcoins;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;

class BlockTradeStation extends BlockContainer {
	
	private IIcon[] icons;

	public BlockTradeStation() {
		super(new Material(MapColor.stoneColor));
		setHardness(3.0f);
		setCreativeTab(CreativeTabs.tabMisc);
		setHarvestLevel("pickaxe", 1);	
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register){
		icons = new IIcon[2];
		
		for (int i = 0; i < icons.length; i++){
			icons[i] = register.registerIcon(UniversalCoins.modid + ":" +
													  		this.getUnlocalizedName().substring(5) + i);
		}
	}
	
	public IIcon getIcon(int par1, int par2){
		if (par1 == 0 || par1 == 1){
			return icons[1];
		}
		return icons[0];
	}
	
	@Override
    public boolean onBlockActivated(World world, int x, int y, int z,
    								EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
				return false;
		}
		player.openGui(UniversalCoins.instance, 0, world, x, y, z);
		return true;
    }
	
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		dropItems(world, x, y, z);
		throwCoins(world, x, y, z);
        super.breakBlock(world, x, y, z, par5, par6);
	}

	private void throwCoins(World world, int x, int y, int z) {
		Random rand = new Random();

		UCTileEntity tileEntity = (UCTileEntity) world.getTileEntity(x, y, z);
		if (tileEntity == null) {
			return;
		}
		int sumLeft = tileEntity.coinSum;
		while (sumLeft > 0){
			if (sumLeft <= 729 * 64){
				dropStack(world, x, y, z, UCItemPricer.getRevenueStack(sumLeft), rand);
				sumLeft = 0;
			}
			else{
				dropStack(world, x, y, z, new ItemStack(UniversalCoins.itemCoinHeap, 64), rand);
				sumLeft -= 729 * 64;
			}
		}
	}

	private void dropStack(World world, int x, int y, int z, ItemStack item, Random rand) {
		float rx = rand.nextFloat() * 0.8F + 0.1F;
		float ry = rand.nextFloat() * 0.8F + 0.1F;
		float rz = rand.nextFloat() * 0.8F + 0.1F;

		EntityItem entityItem = new EntityItem(world,
				x + rx, y + ry, z + rz,
				new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

		if (item.hasTagCompound()) {
			entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
		}


		float factor = 0.05F;
		entityItem.motionX = rand.nextGaussian() * factor;
		entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
		entityItem.motionZ = rand.nextGaussian() * factor;
		world.spawnEntityInWorld(entityItem);
		item.stackSize = 0;
	}

	private void dropItems(World world, int x, int y, int z){
		Random rand = new Random();

		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (!(tileEntity instanceof IInventory)) {
			return;
		}
		IInventory inventory = (IInventory) tileEntity;

		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack item = inventory.getStackInSlot(i);

			if (item != null && item.stackSize > 0) {
				dropStack(world, x, y, z, item, rand);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new UCTileEntity();
	}
}
