package universalcoins.tile;

import ibxm.Player;
import universalcoins.TradeStationGUI;
import universalcoins.UniversalCoins;
import universalcoins.gui.VendorGUI;
import universalcoins.gui.VendorSaleGUI;
import universalcoins.net.UCButtonMessage;
import universalcoins.net.UCTileStationMessage;
import universalcoins.net.UCTileVendorMessage;
import universalcoins.net.UCVendorServerMessage;
import cpw.mods.fml.common.FMLLog;
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

public class TileVendor extends TileEntity implements IInventory {
	
	private ItemStack[] inventory = new ItemStack[15];
	//owner slots
	public static final int itemStorageSlot1 = 0;
	public static final int itemStorageSlot2 = 1;
	public static final int itemStorageSlot3 = 2;
	public static final int itemStorageSlot4 = 3;
	public static final int itemStorageSlot5 = 4;
	public static final int itemStorageSlot6 = 5;
	public static final int itemStorageSlot7 = 6;
	public static final int itemStorageSlot8 = 7;
	public static final int itemStorageSlot9 = 8;
	public static final int itemSellingSlot = 9;
	//sale slots
	public static final int itemOutputSlot = 10;
	public static final int itemCoinOutputSlot1 = 11;
	public static final int itemCoinOutputSlot2 = 12;
	public static final int itemCoinInputSlot = 13;
	public static final int itemUserCoinInputSlot = 14;
	
	private static final int[] multiplier = new int[] {1, 9, 81, 729, 6561};
	private static final Item[] coins = new Item[] { UniversalCoins.proxy.itemCoin,
		UniversalCoins.proxy.itemSmallCoinStack, UniversalCoins.proxy.itemLargeCoinStack, 
		UniversalCoins.proxy.itemSmallCoinBag, UniversalCoins.proxy.itemLargeCoinBag };
	public String blockOwner;
	public int coinSum = 0;
	public int userCoinSum = 0;
	public int itemPrice = 0;
	public boolean infiniteSell = false;
	public boolean sellMode = false;
	public boolean buyButtonActive = false;
	public boolean coinButtonActive = false;
	public boolean isSStackButtonActive = false;
	public boolean isLStackButtonActive = false;
	public boolean isSBagButtonActive = false;
	public boolean isLBagButtonActive = false;
	public boolean uCoinButtonActive = false;
	public boolean uSStackButtonActive = false;
	public boolean uLStackButtonActive = false;
	public boolean uSBagButtonActive = false;
	public boolean uLBagButtonActive = false;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		activateRetrieveButtons();
		activateUserRetrieveButtons();
		activateBuyButton();
	}
	
	private void activateBuyButton() {
		if (userCoinSum > itemPrice && (long) coinSum + (long) itemPrice < 2147483647 
				&& (hasSellingInventory() || infiniteSell)) {
			buyButtonActive = true;
		} else buyButtonActive = false;

	}
	
	private void activateRetrieveButtons() {
		coinButtonActive = false;
		isSStackButtonActive = false;
		isLStackButtonActive = false;
		isSBagButtonActive = false;
		isLBagButtonActive = false;
		if (coinSum > 0) {
			coinButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
			|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemCoin && inventory[itemCoinOutputSlot1].stackSize != 64)
			|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemCoin && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (coinSum >= 9) {
			isSStackButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
			|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemSmallCoinStack && inventory[itemCoinOutputSlot1].stackSize != 64)
			|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemSmallCoinStack && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (coinSum >= 81) {
			isLStackButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
					|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemLargeCoinStack && inventory[itemCoinOutputSlot1].stackSize != 64)
					|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemLargeCoinStack && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (coinSum >= 729) {
			isSBagButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
					|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemSmallCoinBag && inventory[itemCoinOutputSlot1].stackSize != 64)
					|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemSmallCoinBag && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (coinSum >= 6561) {
			isLBagButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
					|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemLargeCoinBag && inventory[itemCoinOutputSlot1].stackSize != 64)
					|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemLargeCoinBag && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
	}
	
	public void onRetrieveButtonsPressed(int buttonClickedID, boolean shiftPressed) {
		if (buttonClickedID <= VendorGUI.idLBagButton ) {
			//get owner coins
			coinSum = retrieveCoins(coinSum,buttonClickedID, shiftPressed);
		} else {
			//get buyer coins
			userCoinSum = retrieveCoins(userCoinSum,buttonClickedID, shiftPressed);
		}
	}
	
	public int retrieveCoins(int coinField, int buttonClickedID, boolean shiftPressed) {
		int absoluteButton = (buttonClickedID <= VendorGUI.idLBagButton ? buttonClickedID
				- VendorGUI.idCoinButton : buttonClickedID - VendorSaleGUI.idCoinButton);
		int multiplier = 1;
		for (int i = 0; i < absoluteButton; i++) {
			multiplier *= 9;
		}
		Item itemOnButton = coins[absoluteButton];
		if (coinField < multiplier
				|| (inventory[itemCoinOutputSlot1] != null && inventory[itemCoinOutputSlot1]
						.getItem() != itemOnButton)
				|| (inventory[itemCoinOutputSlot1] != null && inventory[itemCoinOutputSlot1].stackSize == 64)) {
			return coinField;
		}
		if (shiftPressed) {
			if (inventory[itemCoinOutputSlot1] == null) {
				int amount = coinField / multiplier;
				if (amount >= 64) {
					coinField -= multiplier * 64;
					inventory[itemCoinOutputSlot1] = new ItemStack(itemOnButton);
					inventory[itemCoinOutputSlot1].stackSize = 64;
				} else {
					coinField -= multiplier * amount;
					inventory[itemCoinOutputSlot1] = new ItemStack(itemOnButton);
					inventory[itemCoinOutputSlot1].stackSize = amount;
				}
			} else {
				int amount = Math.min(coinField / multiplier, inventory[itemCoinOutputSlot1].getMaxStackSize() - inventory[itemCoinOutputSlot1].stackSize);
				inventory[itemCoinOutputSlot1].stackSize += amount;
				coinField -= multiplier * amount;
			}
		} else {
			coinField -= multiplier;
			if (inventory[itemCoinOutputSlot1] == null) {
				inventory[itemCoinOutputSlot1] = new ItemStack(itemOnButton);
			} else {
				inventory[itemCoinOutputSlot1].stackSize++;
			}
		}
		return coinField;
	}
	
	private void activateUserRetrieveButtons() {
		uCoinButtonActive = false;
		uSStackButtonActive = false;
		uLStackButtonActive = false;
		uSBagButtonActive = false;
		uLBagButtonActive = false;
		if (userCoinSum > 0) {
			uCoinButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
			|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemCoin && inventory[itemCoinOutputSlot1].stackSize != 64)
			|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemCoin && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (userCoinSum >= 9) {
			uSStackButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
			|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemSmallCoinStack && inventory[itemCoinOutputSlot1].stackSize != 64)
			|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemSmallCoinStack && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (userCoinSum >= 81) {
			uLStackButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
					|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemLargeCoinStack && inventory[itemCoinOutputSlot1].stackSize != 64)
					|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemLargeCoinStack && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (userCoinSum >= 729) {
			uSBagButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
					|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemSmallCoinBag && inventory[itemCoinOutputSlot1].stackSize != 64)
					|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemSmallCoinBag && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
		if (userCoinSum >= 6561) {
			uLBagButtonActive = inventory[itemCoinOutputSlot1] == null || inventory[itemCoinOutputSlot2] == null
					|| (inventory[itemCoinOutputSlot1].getItem() == UniversalCoins.proxy.itemLargeCoinBag && inventory[itemCoinOutputSlot1].stackSize != 64)
					|| (inventory[itemCoinOutputSlot2].getItem() == UniversalCoins.proxy.itemLargeCoinBag && inventory[itemCoinOutputSlot2].stackSize != 64);
		}
	}
	
	public void onBuyPressed() {
		boolean itemSold = false;
		if (inventory[itemOutputSlot] == null) {
			if (infiniteSell) {
				inventory[itemOutputSlot] = inventory[itemSellingSlot].copy();
				itemSold = true;
			} else {
				// find matching item in inventory
				for (int i = itemStorageSlot1; i < itemStorageSlot9; i++) {
					if (inventory[i] != null && inventory[i].getItem() == inventory[itemSellingSlot].getItem()
							&& inventory[i].stackSize >= inventory[itemSellingSlot].stackSize) {
						inventory[itemOutputSlot] = inventory[itemSellingSlot]
								.copy();
						inventory[i].stackSize -= inventory[itemSellingSlot].stackSize;
						if (inventory[i].stackSize == 0) {
							inventory[i] = null;
						}
						itemSold = true;
						break;
					}
				}
			}
			if (itemSold) {
				userCoinSum -= itemPrice;
				coinSum += itemPrice;
				itemSold = false;
			}
		} else if (inventory[itemOutputSlot].getItem() == inventory[itemSellingSlot]
				.getItem() && inventory[itemSellingSlot].stackSize
				+ inventory[itemOutputSlot].stackSize < getSellItem().getMaxStackSize()) {
			if (infiniteSell) {
				inventory[itemOutputSlot].stackSize += inventory[itemSellingSlot].stackSize;
				itemSold = true;
			} else {
				for (int i = itemStorageSlot1; i < itemStorageSlot9; i++) {
					if (inventory[i] != null && inventory[i].getItem() == inventory[itemSellingSlot]
							.getItem()
							&& inventory[i].stackSize >= inventory[itemSellingSlot].stackSize) {
						inventory[itemOutputSlot].stackSize += inventory[itemSellingSlot].stackSize;
						inventory[i].stackSize -= inventory[itemSellingSlot].stackSize;
						if (inventory[i].stackSize == 0) {
							inventory[i] = null;
						}
						itemSold = true;
						break;
					}
				}
			}
		}
		if (itemSold) {
			userCoinSum -= itemPrice;
			coinSum += itemPrice;
		}
	}
	
	public boolean hasSellingInventory() {
		for (int i = itemStorageSlot1; i < itemStorageSlot9; i++) {
			if (inventory[i] != null && inventory[i].getItem() == inventory[itemSellingSlot]
			    .getItem() && inventory[i].stackSize >= inventory[itemSellingSlot].stackSize) {
				return true;
			}
		}
		return false;
	}
	
	private int findAvailableOutputSlot(ItemStack item) {
		//this function checks both output slots to find one the itemstack fits
		if (inventory[itemCoinOutputSlot1] == null || item.getItem() == inventory[itemCoinOutputSlot1].getItem() 
				&& inventory[itemCoinOutputSlot1].stackSize < item.getMaxStackSize()) {
			return itemCoinOutputSlot1;
		}
		if (inventory[itemCoinOutputSlot2] == null || item.getItem() == inventory[itemCoinOutputSlot2].getItem() 
				&& inventory[itemCoinOutputSlot2].stackSize < item.getMaxStackSize()) {
			return itemCoinOutputSlot2;
		}
		return -1;
	}
	
	public ItemStack getSellItem() {
		return inventory[itemSellingSlot];
	}
	
	public void setSellItem(ItemStack stack) {
		inventory[itemSellingSlot] = stack;
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
	public ItemStack decrStackSize(int i, int j) {
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
	public void setInventorySlotContents(int i, ItemStack stack) {
		inventory[i] = stack;
		if (stack != null) {
			if (i == itemCoinInputSlot) {
				int coinType = getCoinType(stack.getItem());
				if (coinType != -1) {
					coinSum += stack.stackSize * multiplier[coinType];
					inventory[i] = null;
					//FMLLog.info("SetInvSlotContents.. Coin Sum: " + coinSum);
					updateTE();
				}
			}
			if (i == itemUserCoinInputSlot) {
				int coinType = getCoinType(stack.getItem());
				if (coinType != -1) {
					userCoinSum += stack.stackSize * multiplier[coinType];
					inventory[i] = null;
					//FMLLog.info("SetInvSlotContents.. Coin Sum: " + coinSum);
					updateTE();
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
	public String getInventoryName() {
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
				&& entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5,
						zCoord + 0.5) < 64;
	}

	@Override
	public void openInventory() {	
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) {
		return true;
	}
	
	@Override
    public Packet getDescriptionPacket() {
        return UniversalCoins.snw.getPacketFrom(new UCTileVendorMessage(this));
    }
	
	public void updateTE() {
	//	UniversalCoins.snw.getPacketFrom(new UCTileVendorMessage(this));
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void sendButtonMessage(int button, boolean shiftPressed) {
		UniversalCoins.snw.sendToServer(new UCButtonMessage(xCoord, yCoord, zCoord, button, shiftPressed));
	}
	
	public void sendServerUpdateMessage() {
		UniversalCoins.snw.sendToServer(new UCVendorServerMessage(xCoord, yCoord, zCoord, itemPrice, blockOwner, infiniteSell));
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
			userCoinSum = tagCompound.getInteger("UserCoinSum");
		} catch (Throwable ex2) {
			userCoinSum = 0;
		}
		try {
			itemPrice = tagCompound.getInteger("ItemPrice");
		} catch (Throwable ex2) {
			itemPrice = 0;
		}
		try {
			blockOwner = tagCompound.getString("BlockOwner");
		} catch (Throwable ex2) {
			blockOwner = null;
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
		tagCompound.setTag("Inventory", itemList);
		tagCompound.setInteger("CoinSum", coinSum);
		tagCompound.setInteger("UserCoinSum", userCoinSum);		
		tagCompound.setInteger("ItemPrice", itemPrice);
		tagCompound.setString("BlockOwner", blockOwner);
	}
	

}
