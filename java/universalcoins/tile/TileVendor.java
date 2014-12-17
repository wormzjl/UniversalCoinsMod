package universalcoins.tile;

import ibxm.Player;
import universalcoins.UniversalCoins;
import universalcoins.gui.VendorGUI;
import universalcoins.gui.VendorSellGUI;
import universalcoins.net.UCButtonMessage;
import universalcoins.net.UCTileTradeStationMessage;
import universalcoins.net.UCTileVendorMessage;
import universalcoins.net.UCVendorServerMessage;
import universalcoins.util.UCItemPricer;
import universalcoins.util.UCWorldData;
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

public class TileVendor extends TileEntity implements IInventory, ISidedInventory {
	
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
	public static final int itemTradeSlot = 9;
	//sale slots
	public static final int itemOutputSlot = 10;
	public static final int itemCoinOutputSlot = 11;
	public static final int itemCoinInputSlot = 12;
	public static final int itemUserCoinInputSlot = 13;
	//card slot
	public static final int itemCardSlot = 14;
	
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
		if ((userCoinSum >= itemPrice && coinSum + itemPrice < Integer.MAX_VALUE 
				&& (hasSellingInventory() || infiniteSell)) || 
				(inventory[itemCardSlot] != null && getAccountBalance() > itemPrice)) {
			if (inventory[itemOutputSlot] != null) {
				if (inventory[itemOutputSlot].getMaxStackSize() == inventory[itemOutputSlot].stackSize) {
					buyButtonActive = false;
					return;
				}
			}
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
			coinButtonActive = inventory[itemCoinOutputSlot] == null
			|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemCoin && inventory[itemCoinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 9) {
			isSStackButtonActive = inventory[itemCoinOutputSlot] == null
			|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemSmallCoinStack && inventory[itemCoinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 81) {
			isLStackButtonActive = inventory[itemCoinOutputSlot] == null
					|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemLargeCoinStack && inventory[itemCoinOutputSlot].stackSize != 64);		}
		if (coinSum >= 729) {
			isSBagButtonActive = inventory[itemCoinOutputSlot] == null
					|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemSmallCoinBag && inventory[itemCoinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 6561) {
			isLBagButtonActive = inventory[itemCoinOutputSlot] == null
					|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemLargeCoinBag && inventory[itemCoinOutputSlot].stackSize != 64);
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
				- VendorGUI.idCoinButton : buttonClickedID - VendorSellGUI.idCoinButton);
		int multiplier = 1;
		for (int i = 0; i < absoluteButton; i++) {
			multiplier *= 9;
		}
		Item itemOnButton = coins[absoluteButton];
		if (coinField < multiplier
				|| (inventory[itemCoinOutputSlot] != null && inventory[itemCoinOutputSlot]
						.getItem() != itemOnButton)
				|| (inventory[itemCoinOutputSlot] != null && inventory[itemCoinOutputSlot].stackSize == 64)) {
			return coinField;
		}
		if (shiftPressed) {
			if (inventory[itemCoinOutputSlot] == null) {
				int amount = coinField / multiplier;
				if (amount >= 64) {
					coinField -= multiplier * 64;
					inventory[itemCoinOutputSlot] = new ItemStack(itemOnButton);
					inventory[itemCoinOutputSlot].stackSize = 64;
				} else {
					coinField -= multiplier * amount;
					inventory[itemCoinOutputSlot] = new ItemStack(itemOnButton);
					inventory[itemCoinOutputSlot].stackSize = amount;
				}
			} else {
				int amount = Math.min(coinField / multiplier, inventory[itemCoinOutputSlot].getMaxStackSize() - inventory[itemCoinOutputSlot].stackSize);
				inventory[itemCoinOutputSlot].stackSize += amount;
				coinField -= multiplier * amount;
			}
		} else {
			coinField -= multiplier;
			if (inventory[itemCoinOutputSlot] == null) {
				inventory[itemCoinOutputSlot] = new ItemStack(itemOnButton);
			} else {
				inventory[itemCoinOutputSlot].stackSize++;
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
			uCoinButtonActive = inventory[itemCoinOutputSlot] == null
			|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemCoin && inventory[itemCoinOutputSlot].stackSize != 64);
		}
		if (userCoinSum >= 9) {
			uSStackButtonActive = inventory[itemCoinOutputSlot] == null
			|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemSmallCoinStack && inventory[itemCoinOutputSlot].stackSize != 64);
		}
		if (userCoinSum >= 81) {
			uLStackButtonActive = inventory[itemCoinOutputSlot] == null
					|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemLargeCoinStack && inventory[itemCoinOutputSlot].stackSize != 64);
		}
		if (userCoinSum >= 729) {
			uSBagButtonActive = inventory[itemCoinOutputSlot] == null
					|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemSmallCoinBag && inventory[itemCoinOutputSlot].stackSize != 64);
		}
		if (userCoinSum >= 6561) {
			uLBagButtonActive = inventory[itemCoinOutputSlot] == null
					|| (inventory[itemCoinOutputSlot].getItem() == UniversalCoins.proxy.itemLargeCoinBag && inventory[itemCoinOutputSlot].stackSize != 64);
		}
	}
	
	public void onBuyPressed() {
		onBuyPressed(1);
	}
	
	public void onBuyPressed(int amount) {
		boolean useCard = false;
		//use the card if we have it
		if (inventory[itemCardSlot] != null && getAccountBalance() > itemPrice * amount) {
			useCard = true;
		}
		if (inventory[itemTradeSlot] == null || userCoinSum < itemPrice * amount && !useCard) {
			buyButtonActive = false;
			return;
		}
		int totalSale = inventory[itemTradeSlot].stackSize * amount;
		if (inventory[itemOutputSlot] != null && inventory[itemOutputSlot].stackSize 
				+ totalSale > inventory[itemTradeSlot].getMaxStackSize()) {
			buyButtonActive = false;
			return;
		}
		if (infiniteSell) {
			if (inventory[itemOutputSlot] == null) {
				inventory[itemOutputSlot] = inventory[itemTradeSlot].copy();
				inventory[itemOutputSlot].stackSize = totalSale;
				if (useCard && inventory[itemCardSlot] == null) { 
					debitAccount(itemPrice * amount);
				}	else {
					userCoinSum -= itemPrice * amount;
				}
			} else {
				totalSale = Math.min(inventory[itemTradeSlot].stackSize
						* amount, inventory[itemTradeSlot].getMaxStackSize()
						- inventory[itemOutputSlot].stackSize);
				inventory[itemOutputSlot].stackSize += totalSale;
				if (useCard && inventory[itemCardSlot] == null) { 
					debitAccount(itemPrice * amount);
				} else {
					userCoinSum -= itemPrice * amount;
				}
			}
			if (!UniversalCoins.collectCoinsInInfinite) {
				coinSum = 0;
			} else {
				coinSum += itemPrice * amount;
			}
		} else {
			// find matching item in inventory
			// we need to match the item, damage, and tags to make sure the
			// stacks are equal
			for (int i = itemStorageSlot1; i <= itemStorageSlot9; i++) {
				if (inventory[i] != null
						&& inventory[i].getItem() == inventory[itemTradeSlot].getItem()
						&& inventory[i].getItemDamage() == inventory[itemTradeSlot].getItemDamage()
						&& ItemStack.areItemStackTagsEqual(inventory[i],
								inventory[itemTradeSlot])) {
					// copy itemstack if null. We'll set the amount to 0 to
					// start.
					if (inventory[itemOutputSlot] == null) {
						inventory[itemOutputSlot] = inventory[i].copy();
						inventory[itemOutputSlot].stackSize = 0;
					}
					int thisSale = Math.min(inventory[i].stackSize, totalSale);
					inventory[itemOutputSlot].stackSize += thisSale;
					inventory[i].stackSize -= thisSale;
					totalSale -= thisSale;
					if (useCard && inventory[itemCardSlot] == null) { 
						debitAccount(itemPrice * thisSale	/ inventory[itemTradeSlot].stackSize);
					}	else {
						userCoinSum -= itemPrice * thisSale	/ inventory[itemTradeSlot].stackSize;
					}
					if (!UniversalCoins.collectCoinsInInfinite && infiniteSell) {
						coinSum = 0;
					} else {
						coinSum += itemPrice * thisSale
								/ inventory[itemTradeSlot].stackSize;
					}
				}
				// cleanup empty stacks
				if (inventory[i] == null || inventory[i].stackSize == 0) {
					inventory[i] = null;
				}
			}
		}
	}
	
	public void onBuyMaxPressed() {
		boolean useCard = false;
		int amount = 0;
		if (inventory[itemTradeSlot] == null) {
			buyButtonActive = false;
			return;
		}
		// use the card if we have it
		if (inventory[itemCardSlot] != null && getAccountBalance() > itemPrice) {
			useCard = true;
		}
		if (userCoinSum < itemPrice && !useCard) { // can't buy even one
			buyButtonActive = false;
			return;
		}
		if (inventory[itemOutputSlot] == null) { // empty stack
			if (inventory[itemTradeSlot].getMaxStackSize() * itemPrice / inventory[itemTradeSlot].stackSize <= (
					useCard ? getAccountBalance() : userCoinSum)) {
				// buy as many as will fit in a stack
				amount = inventory[itemTradeSlot].getMaxStackSize() / inventory[itemTradeSlot].stackSize;
			} else {
				// buy as many as i have coins for.
				amount = (useCard ? getAccountBalance() : userCoinSum) / itemPrice;
			}
		} else if (inventory[itemOutputSlot].getItem() == inventory[itemTradeSlot].getItem()
				&& inventory[itemOutputSlot].getItemDamage() == inventory[itemTradeSlot].getItemDamage()
				&& ItemStack.areItemStackTagsEqual(inventory[itemOutputSlot], inventory[itemTradeSlot])
				&& inventory[itemOutputSlot].stackSize < inventory[itemTradeSlot].getMaxStackSize()) {
			if ((inventory[itemOutputSlot].getMaxStackSize() - inventory[itemOutputSlot].stackSize)
					* itemPrice <= userCoinSum) {
				// buy as much as i can fit in a stack since we have enough coins
				amount = (inventory[itemTradeSlot].getMaxStackSize()
						- inventory[itemOutputSlot].stackSize) / inventory[itemOutputSlot].stackSize;
			} else {
				amount = (useCard ? getAccountBalance() : userCoinSum) / itemPrice; // buy as many as i can with available coins.
			}
		} else {
			buyButtonActive = false;
		}
		onBuyPressed(amount);
	}
	
	public boolean hasSellingInventory() {
		for (int i = itemStorageSlot1; i <= itemStorageSlot9; i++) {
			if (inventory[i] != null && inventory[itemTradeSlot] != null && inventory[i].getItem() == inventory[itemTradeSlot].getItem()) {
				return true;
			}
		}
		return false;
	}
	
	public ItemStack getSellItem() {
		return inventory[itemTradeSlot];
	}
	
	public void setSellItem(ItemStack stack) {
		inventory[itemTradeSlot] = stack;
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
	public ItemStack decrStackSize(int slot, int stackSize) {
		ItemStack newStack;
		if (inventory[slot] == null) {
			return null;
		}
		if (inventory[slot].stackSize <= stackSize) {
			newStack = inventory[slot];
			inventory[slot] = null;

			return newStack;
		}
		newStack = ItemStack.copyItemStack(inventory[slot]);
		newStack.stackSize = stackSize;
		inventory[slot].stackSize -= stackSize;
		return newStack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return getStackInSlot(i);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;
		if (stack != null) {
			if (slot == itemCoinInputSlot || slot == itemUserCoinInputSlot) {
				int coinType = getCoinType(stack.getItem());
				if (coinType != -1) {
					int itemValue = multiplier[coinType];
					int depositAmount = 0;
					if (slot == itemCoinInputSlot) {
						depositAmount = Math.min(stack.stackSize, (Integer.MAX_VALUE - coinSum) / itemValue);
						coinSum += depositAmount * itemValue;
					} else {
						depositAmount = Math.min(stack.stackSize, (Integer.MAX_VALUE - userCoinSum) / itemValue);
						userCoinSum += depositAmount * itemValue;
					}
					inventory[slot].stackSize -= depositAmount;
					if (inventory[slot].stackSize == 0) {
						inventory[slot] = null;
					}
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (slot == itemTradeSlot) {
			inventory[itemTradeSlot] = stack.copy();
			return false;
		}else return true;
	}
	
	@Override
    public Packet getDescriptionPacket() {
		return UniversalCoins.snw.getPacketFrom(new UCTileVendorMessage(this));
    }
    
	public void updateTE() {
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
		try {
			infiniteSell = tagCompound.getBoolean("Infinite");
		} catch (Throwable ex2) {
			infiniteSell = false;
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
		tagCompound.setBoolean("Infinite", infiniteSell);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 11 };
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		// put everything in the item storage slots
		if (var1 >= itemStorageSlot1 && var1 <= itemStorageSlot9) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		// allow pulling items from output slot only
		if (var1 == itemCoinOutputSlot) {
			return true;
		} else {
			return false;
		}
	}
	
	public int getAccountBalance() {
		if (inventory[itemCardSlot] != null) {
			String accountNumber = inventory[itemCardSlot].stackTagCompound.getString("Account");
			if (getWorldString(accountNumber) != "") {
				return getWorldInt(accountNumber);
			}
		} return -1;
	}
	
	public boolean debitAccount(int amount) {
		if (inventory[itemCardSlot] != null) {
			String accountNumber = inventory[itemCardSlot].stackTagCompound.getString("Account");
			if (getWorldString(accountNumber) != "") {
				int balance = getWorldInt(accountNumber);
				balance -= amount;
				setWorldData(accountNumber, balance);
				return true;
			}
		} return false;
	}
	
	private void setWorldData(String tag, int data) {
		UCWorldData wData = UCWorldData.get(super.worldObj);
		NBTTagCompound wdTag = wData.getData();
		wdTag.setInteger(tag, data);
		wData.markDirty();
	}
	
	private int getWorldInt(String tag) {
		UCWorldData wData = UCWorldData.get(super.worldObj);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getInteger(tag);
	}
	
	private String getWorldString(String tag) {
		UCWorldData wData = UCWorldData.get(super.worldObj);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getString(tag);
	}
}
