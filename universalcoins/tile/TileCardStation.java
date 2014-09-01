package universalcoins.tile;

import cpw.mods.fml.common.FMLLog;
import universalcoins.UniversalCoins;
import universalcoins.gui.CardStationGUI;
import universalcoins.net.UCButtonMessage;
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
	private ItemStack[] inventory = new ItemStack[4];
	public static final int itemCardSlot = 0;
	public static final int itemCardOutputSlot = 1;	
	public static final int itemCoinSlot = 2;
	public static final int itemOutputSlot = 3;
	private static final int[] multiplier = new int[] {1, 9, 81, 729, 6561};
	private static final Item[] coins = new Item[] { UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack, UniversalCoins.proxy.itemLargeCoinStack, 
			UniversalCoins.proxy.itemSmallCoinBag, UniversalCoins.proxy.itemLargeCoinBag };
	public String cardOwner;
	public int coinSum = 0;
    public String customName;
    public String player;
    public boolean isCardButtonActive = false;
    public boolean isCoinButtonActive = false;
	public boolean isSStackButtonActive = false;
	public boolean isLStackButtonActive = false;
	public boolean isSBagButtonActive = false;
	public boolean isLBagButtonActive = false;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		activateRetrieveButtons();
		activateCardButton();
	}
	
	public void onRetrieveButtonsPressed(int buttonClickedID,
			boolean shiftPressed) {
		int absoluteButton = buttonClickedID - CardStationGUI.idCoinButton;
		int multiplier = 1;
		for (int i = 0; i < absoluteButton; i++) {
			multiplier *= 9;
		}
		Item itemOnButton = coins[absoluteButton];
		if (coinSum < multiplier
				|| (inventory[itemOutputSlot] != null && inventory[itemOutputSlot]
						.getItem() != itemOnButton)
				|| (inventory[itemOutputSlot] != null && inventory[itemOutputSlot].stackSize == 64)) {
			return;
		}
		if (shiftPressed) {
			if (inventory[itemOutputSlot] == null) {
				int amount = coinSum / multiplier;
				if (amount >= 64) {
					coinSum -= multiplier * 64;
					inventory[itemOutputSlot] = new ItemStack(itemOnButton);
					inventory[itemOutputSlot].stackSize = 64;
				} else {
					coinSum -= multiplier * amount;
					inventory[itemOutputSlot] = new ItemStack(itemOnButton);
					inventory[itemOutputSlot].stackSize = amount;
				}
			} else {
				int amount = Math.min(coinSum / multiplier, inventory[itemOutputSlot].getMaxStackSize() - inventory[itemOutputSlot].stackSize);
				inventory[itemOutputSlot].stackSize += amount;
				coinSum -= multiplier * amount;
			}
		} else {
			coinSum -= multiplier;
			if (inventory[itemOutputSlot] == null) {
				inventory[itemOutputSlot] = new ItemStack(itemOnButton);
			} else {
				inventory[itemOutputSlot].stackSize++;
			}
		}
	}
	
	private void activateCardButton() {
		if (coinSum >= 50 || coinSum > 0 && inventory[itemCardOutputSlot] != null) {
			isCardButtonActive = true;
		} else isCardButtonActive = false;
	}
	
	private void activateRetrieveButtons() {
		isCoinButtonActive = false;
		isSStackButtonActive = false;
		isLStackButtonActive = false;
		isSBagButtonActive = false;
		isLBagButtonActive = false;
		if (coinSum > 0) {
			isCoinButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.proxy.itemCoin && inventory[itemOutputSlot].stackSize != 64);
		}
		if (coinSum >= 9) {
			isSStackButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.proxy.itemSmallCoinStack && inventory[itemOutputSlot].stackSize != 64);
		}
		if (coinSum >= 81) {
			isLStackButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.proxy.itemLargeCoinStack && inventory[itemOutputSlot].stackSize != 64);
		}
		if (coinSum >= 729) {
			isSBagButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.proxy.itemSmallCoinBag && inventory[itemOutputSlot].stackSize != 64);
		}
		if (coinSum >= 6561) {
			isLBagButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.proxy.itemLargeCoinBag && inventory[itemOutputSlot].stackSize != 64);
		}
	}
	
	public void onCardButtonPressed() {
		if (inventory[itemCardOutputSlot] == null && coinSum >= 50) {
			inventory[itemCardOutputSlot] = new ItemStack(
					UniversalCoins.proxy.itemUCCard);
			coinSum -= 50;
		}
		if (inventory[itemCardOutputSlot] == null) return;
		if (inventory[itemCardOutputSlot].stackTagCompound == null) {
			inventory[itemCardOutputSlot].stackTagCompound = new NBTTagCompound();
			inventory[itemCardOutputSlot].stackTagCompound.setInteger("CoinSum", 0);
		}
		int cardBalance = inventory[itemCardOutputSlot].stackTagCompound.getInteger("CoinSum");
		inventory[itemCardOutputSlot].stackTagCompound.setString("Owner", player);
		inventory[itemCardOutputSlot].stackTagCompound.setInteger("CoinSum", coinSum + cardBalance);
		coinSum = 0;
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
			coinSum = tagCompound.getInteger("CoinSum");
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
		tagCompound.removeTag("CoinSum");
		tagCompound.setTag("Inventory", itemList);
		tagCompound.setInteger("CoinSum", coinSum);
		tagCompound.setString("CustomName", getInventoryName());
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
		return this.hasCustomInventoryName() ? this.customName : UniversalCoins.proxy.blockCardStation.getLocalizedName();
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
		if (i >= inventory.length) {
			return null;
		}
		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		//FMLLog.info("Stack Size Decreased in slot " + i);
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
		if (this.inventory[i] != null) {
            ItemStack itemstack = this.inventory[i];
            this.inventory[i] = null;
            return itemstack;
        }
        else {
            return null;
        }
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		inventory[slot] = itemStack;
		if (itemStack != null) {
			int coinType = getCoinType(itemStack.getItem());
			if (coinType != -1) {
				coinSum += itemStack.stackSize * multiplier[coinType];
				inventory[slot] = null;
				// FMLLog.info("SetInvSlotContents.. Coin Sum: " + coinSum);
			}
			if (slot == itemCardSlot) {
				NBTTagCompound tag = inventory[itemCardSlot].getTagCompound();
				if (tag == null) return;
				int cardSum = tag.getInteger("CoinSum");
				coinSum += cardSum;
				tag.setInteger("CoinSum", 0);
				tag.setString("Owner", player);
				if (inventory[itemCardOutputSlot] == null) {
					inventory[itemCardOutputSlot] = inventory[itemCardSlot];
					inventory[itemCardSlot] = null;
				}
			}
			updateTE();
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
		return true;
	}
	
	public boolean canWithdraw () {
		if (inventory[itemCardOutputSlot] != null && coinSum > 0 ){
			return true;
		} else return false;
	}
	
	@Override
    public Packet getDescriptionPacket() 
    {
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
	
	public void sendButtonMessage(int button, boolean shiftPressed) {
		UniversalCoins.snw.sendToServer(new UCButtonMessage(xCoord, yCoord, zCoord, button, shiftPressed));
	}
}
