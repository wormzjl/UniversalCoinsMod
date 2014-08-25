package universalcoins.tile;

import universalcoins.UniversalCoins;
import universalcoins.net.UCButtonMessage;
import universalcoins.net.UCTileStationMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public class TileCardStation extends TileEntity implements IInventory{
	private ItemStack[] inventory;
	private final int invSize = 3;
	public static final int itemInputSlot = 0;
	public static final int itemCoinSlot = 1;
	public static final int itemOutputSlot = 2;
	private static final int[] multiplier = new int[] {1, 9, 81, 729, 6561};
	private static final Item[] coins = new Item[] { UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack, UniversalCoins.proxy.itemLargeCoinStack, 
			UniversalCoins.proxy.itemSmallCoinBag, UniversalCoins.proxy.itemLargeCoinBag };
	public int coinSum = 0;
    public String customName;

    @Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);

		NBTTagList tagList = tagCompound.getTagList("Inventory",
				Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < inventory.length) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		try {
			coinSum = tagCompound.getInteger("CoinsLeft");
		} catch (Throwable ex2) {
			coinSum = 0;
		}
		try {
			customName = tagCompound.getString("CustomName");
		} catch (Throwable ex2) {
			customName = null;
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			ItemStack stack = inventory[i];
			if (stack != null) {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot", (byte) i);
				stack.writeToNBT(tag);
				itemList.appendTag(tag);
			}
		}
		tagCompound.removeTag("CoinsLeft");
		tagCompound.setTag("Inventory", itemList);
		tagCompound.setInteger("CoinsLeft", coinSum);
		tagCompound.setString("CustomName", getInventoryName());
	}
	
    public void updateTE() {
		 worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
   public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
   }
	
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) { 
        readFromNBT(pkt.func_148857_g());
    }

	public void sendPacket(int button, boolean shiftPressed) {
		UniversalCoins.snw.sendToServer(new UCButtonMessage(xCoord, yCoord,
				zCoord, button, shiftPressed));
	}

    @Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
	
	@Override
	public int getSizeInventory() {
		return inventory.length;
	}
	
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : UniversalCoins.proxy.blockTradeStation.getLocalizedName();
	}
	
	public void setInventoryName(String name) {
		customName = name;
	}

	public boolean isInventoryNameLocalized() {
		return false;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && this.customName.length() > 0;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= invSize) {
			return null;
		}
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		// FMLLog.info("Stack Size Decreased in slot " + i);
		ItemStack newStack;
		if (inventory[i] == null) {
			return null;
		}
		if (inventory[i].stackSize <= j) {
			newStack = inventory[i];
			inventory[i] = null;

			return newStack;
		}
		newStack = ItemStack.copyItemStack(inventory[i]);
		newStack.stackSize = j;
		inventory[i].stackSize -= j;
		return newStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemStack) {
		inventory[i] = itemStack;
		if (itemStack != null) {
			if (i == itemCoinSlot || i == itemInputSlot) {
				int coinType = getCoinType(itemStack.getItem());
				if (coinType != -1) {
					coinSum += itemStack.stackSize * multiplier[coinType];
					inventory[i] = null;
					//FMLLog.info("SetInvSlotContents.. Coin Sum: " + coinSum);
				}
			}
		}
	}

	private int getCoinType(Item item) {
		for (int i = 0; i < 5; i++) {
			if (item == coins[i]) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
				&& entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		// TODO Auto-generated method stub
		return false;
	}
}
