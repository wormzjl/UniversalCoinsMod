package universalcoins.tile;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import universalcoins.UniversalCoins;
import universalcoins.inventory.ContainerBandit;
import universalcoins.net.UCButtonMessage;

public class TileBandit extends TileEntity implements IInventory {
	
	private ItemStack[] inventory = new ItemStack[3];
	public static final int itemCardSlot = 0;
	public static final int itemCoinSlot = 1;
	public static final int itemOutputSlot = 2;
	private static final int[] multiplier = new int[] {1, 9, 81, 729, 6561};
	private static final Item[] coins = new Item[] { UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack, UniversalCoins.proxy.itemLargeCoinStack, 
			UniversalCoins.proxy.itemSmallCoinBag, UniversalCoins.proxy.itemLargeCoinBag };
	public int coinSum = 0;
	public String customName = "";
	public String playerName = "";
	public boolean inUse = false;
	public int[] reelPos = {0, 0, 0, 0};
	private int[] reelStops = {0, 26, 54, 80, 108, 134, 162, 188};
	
	public TileBandit() {
		super();
	}
	
	public void onButtonPressed(int buttonId) {
		if (buttonId == 0) {
			inventory[itemOutputSlot] = null;
			coinSum--;
			leverPull();
		}
		if (buttonId == 1) {
			fillOutputSlot();
		}
		if (buttonId == 2) {
			int matchCount = 0;
			for (int i = 0; i < reelStops.length; i++) {
				matchCount = 0;
				for (int j = 0; j < reelPos.length; j++) {
					if (reelStops[i] == reelPos[j]) {
						matchCount++;
					}
				}
				if (matchCount == 4) {
					coinSum += UniversalCoins.fourMatchPayout;
				}
				if (matchCount == 3) {
					coinSum += UniversalCoins.threeMatchPayout;
				}
				if (matchCount == 2) {
					coinSum += UniversalCoins.twoMatchPayout;
				}
			}
		}
	}
	
	private void leverPull() {
		Random random = new Random();
		
		for (int i = 0; i < reelPos.length; i++) {
			int rnd = random.nextInt(reelStops.length);
			reelPos[i] = reelStops[rnd];
		}
	}
	
	private void updateInUse() {
		if (worldObj.isRemote) return;
		EntityPlayer playerTest = this.worldObj.getPlayerEntityByName(playerName);
		if (playerTest != null && playerTest.openContainer != null &&
				this.worldObj.getPlayerEntityByName(playerName).openContainer instanceof ContainerBandit) {
			inUse = true;
		} else {
			inUse = false;
		}
	}
	
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : UniversalCoins.proxy.blockBandit.getLocalizedName();
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
	
	public void sendPacket(int button, boolean shiftPressed) {
		UniversalCoins.snw.sendToServer(new UCButtonMessage(xCoord, yCoord,
				zCoord, button, shiftPressed));
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
	
	public void updateTE() {
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		tagCompound.setTag("Inventory", itemList);
		tagCompound.setInteger("coinSum", coinSum);
		tagCompound.setString("customName", customName);
		tagCompound.setBoolean("inUse", inUse);
		tagCompound.setInteger("reelPos0", reelPos[0]);
		tagCompound.setInteger("reelPos1", reelPos[1]);
		tagCompound.setInteger("reelPos2", reelPos[2]);
		tagCompound.setInteger("reelPos3", reelPos[3]);
	}
	
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
			coinSum = tagCompound.getInteger("coinSum");
		} catch (Throwable ex2) {
			coinSum = 0;
		}
		try {
			customName = tagCompound.getString("customName");
		} catch (Throwable ex2) {
			customName = "";
		}
		try {
			reelPos[0] = tagCompound.getInteger("reelPos0");
		} catch (Throwable ex2) {
			reelPos[0] = 0;
		}
		try {
			reelPos[1] = tagCompound.getInteger("reelPos1");
		} catch (Throwable ex2) {
			reelPos[1] = 0;
		}
		try {
			reelPos[2] = tagCompound.getInteger("reelPos2");
		} catch (Throwable ex2) {
			reelPos[2] = 0;
		}
		try {
			reelPos[3] = tagCompound.getInteger("reelPos3");
		} catch (Throwable ex2) {
			reelPos[3] = 0;
		}
	}

	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i >= inventory.length) {
			return null;
		}
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= size) {
				inventory[slot] = null;
			} else {
				stack = stack.splitStack(size);
				if (stack.stackSize == 0) {
					inventory[slot] = null;
				}
			}
		}
		coinsTaken(stack);
		return stack;
	}
	
	public void coinsTaken(ItemStack stack) {
		int coinType = getCoinType(stack.getItem());
		if (coinType != -1) {
			int itemValue = multiplier[coinType];
			int debitAmount = 0;
			debitAmount = Math.min(stack.stackSize, (Integer.MAX_VALUE - coinSum) / itemValue);
			if(!worldObj.isRemote) {
				coinSum -= debitAmount * itemValue;
				//debitAccount(debitAmount * itemValue);
				//updateAccountBalance();
			}
		}
	}
	
	public void fillOutputSlot() {
		inventory[itemOutputSlot] = null;
		if (coinSum > 0) {
			// use logarithm to find largest cointype for the balance
			int logVal = Math.min((int) (Math.log(coinSum) / Math.log(9)), 4);
			int stackSize = Math.min((int) (coinSum / Math.pow(9, logVal)), 64);
			// add a stack to the slot
			inventory[itemOutputSlot] = new ItemStack(coins[logVal], stackSize);
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		inUse = false;
		return getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		if (stack != null) {
			if (slot == itemCoinSlot) {
				int coinType = getCoinType(stack.getItem());
				if (coinType != -1) {
					int itemValue = multiplier[coinType];
					int depositAmount = Math.min(stack.stackSize, (Integer.MAX_VALUE - coinSum) / itemValue);
					coinSum += depositAmount * itemValue;
					inventory[slot].stackSize -= depositAmount;
					if (inventory[slot].stackSize == 0) {
						inventory[slot] = null;
					}
				}
			}
		}		
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void openInventory() {		
	}

	@Override
	public void closeInventory() {		
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return false;
	}


}
