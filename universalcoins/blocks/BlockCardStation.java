package universalcoins.blocks;

import java.util.Random;

import universalcoins.UniversalCoins;
import universalcoins.render.BlockVendorRenderer;
import universalcoins.tile.TileCardStation;
import universalcoins.tile.TileTradeStation;
import buildcraft.api.tools.IToolWrench;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

public class BlockCardStation extends BlockContainer {
	
	IIcon blockIcon;
	
	public BlockCardStation() {
		super(new Material(MapColor.stoneColor));
		setHardness(3.0f);
		setCreativeTab(UniversalCoins.tabUniversalCoins);
		setBlockTextureName("universalcoins:blockTradeStation1"); //fixes missing texture on block break
		setHarvestLevel("pickaxe", 1);	
	}
	
	@Override
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }
	
	@Override
	public boolean isOpaqueCube() {
	   return false;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par6, float par7, float par8, float par9) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity == null || player.isSneaking()) {
			if (player.getCurrentEquippedItem() != null
					&& player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
				IToolWrench wrench = (IToolWrench) player.getCurrentEquippedItem().getItem();
				if (wrench.canWrench(player, x, y, z)) {
					Random rand = new Random();
					if (!world.isRemote) {
						ItemStack stack = getItemStackWithData(world, x, y, z);
						EntityItem entityItem = new EntityItem(world, x, y, z,
								stack);
						world.spawnEntityInWorld(entityItem);
						removedByPlayer(world, player, x, y, z);
						if (player.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
							((IToolWrench) player.getCurrentEquippedItem()
									.getItem()).wrenchUsed(player, x, y, z);
						}
					}
					return true;
				}
			}
		}
		player.openGui(UniversalCoins.instance, 0, world, x, y, z);
		((TileCardStation) tileEntity).player = player.getDisplayName();
		return true;
	}
	
	public ItemStack getItemStackWithData(World world, int x, int y, int z) {
		ItemStack stack = new ItemStack(world.getBlock(x, y, z), 1);
		TileEntity tentity = world.getTileEntity(x, y, z);
		if (tentity instanceof TileCardStation) {
			TileCardStation te = (TileCardStation) tentity;
			NBTTagList itemList = new NBTTagList();
			NBTTagCompound tagCompound = new NBTTagCompound();
			for (int i = 0; i < te.getSizeInventory(); i++) {
				ItemStack invStack = te.getStackInSlot(i);
				if (invStack != null) {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setByte("Slot", (byte) i);
					invStack.writeToNBT(tag);
					itemList.appendTag(tag);
				}
			}
			tagCompound.setTag("Inventory", itemList);
			tagCompound.setInteger("CoinsLeft", te.coinSum);
			tagCompound.setString("CustomName", te.getInventoryName());
			stack.setTagCompound(tagCompound);
			return stack;
		} else
			return stack;
	}
		
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		//set block meta so we can use it later for rotation
		int rotation = MathHelper.floor_double((double)((player.rotationYaw * 4.0f) / 360F) + 2.5D) & 3;
		world.setBlockMetadataWithNotify(x, y, z, rotation, 2);
		
		if (world.isRemote) return;
		if (stack.hasTagCompound()) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof TileCardStation) {
				TileCardStation tentity = (TileCardStation) te;
				NBTTagCompound tagCompound = stack.getTagCompound();
				if (tagCompound == null) {
					return;
				}
				NBTTagList tagList = tagCompound.getTagList("Inventory", Constants.NBT.TAG_COMPOUND);
				for (int i = 0; i < tagList.tagCount(); i++) {
					NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
					byte slot = tag.getByte("Slot");
					if (slot >= 0 && slot < tentity.getSizeInventory()) {
						tentity.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(tag));
					}
				}
				tentity.coinSum = tagCompound.getInteger("CoinsLeft");
				tentity.customName = tagCompound.getString("CustomName");
			}
			world.markBlockForUpdate(x, y, z);
		} else if (stack.hasDisplayName()) {
            ((TileCardStation)world.getTileEntity(x, y, z)).setInventoryName(stack.getDisplayName());
        }
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
        super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileCardStation();
	}
}
