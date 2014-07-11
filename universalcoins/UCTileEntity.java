package universalcoins;

import cpw.mods.fml.common.FMLLog;
import universalcoins.net.PacketPipeline;
import universalcoins.net.PacketTradingStation;
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


public class UCTileEntity extends TileEntity implements IInventory, ISidedInventory {
	
	private ItemStack[] inventory;
	private final int invSize = 3;
	public static final int itemInputSlot = 0;
	public static final int itemCoinSlot = 1;
	public static final int itemOutputSlot = 2;
	private static final int[] multiplier = new int[] { 1, 9, 81, 729 };
	private static final Item[] coins = new Item[] { UniversalCoins.itemCoin,
			UniversalCoins.itemSmallCoinStack,
			UniversalCoins.itemLargeCoinStack, UniversalCoins.itemCoinHeap };
	public int coinSum = 0;
	public int itemPrice;
	public boolean buyButtonActive = false;
	public boolean sellButtonActive = false;
	public boolean coinButtonActive = false;
	public boolean sStackButtonActive = false;
	public boolean isStackButtonActive = false;
	public boolean heapButtonActive = false;
	public boolean shiftPressed = false;
	public boolean autoModeButtonActive = UniversalCoins.autoModeEnabled;
	private static final int[] slots_top = new int[] { 0, 1, 2, 3, 4 };
	private static final int[] slots_bottom = new int[] { 0, 1, 2, 3, 4 };
	private static final int[] slots_sides = new int[] { 0, 1, 2, 3, 4 };

	public int autoMode = 0;
	public int coinMode = 0;


	public UCTileEntity() {
		super();
		inventory = new ItemStack[invSize];
	}

	@Override
	public int getSizeInventory() {
		return invSize;
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
		for (int i = 0; i < 4; i++) {
			if (item == coins[i]) {
				return i;
			}
		}
		return -1;
	}

	public String getInventoryName() {
		return "Universal Trade Station";
	}

	public boolean isInventoryNameLocalized() {
		return false;
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


    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		Item stackItem = itemstack.getItem();
		if (slot == itemCoinSlot) {
			return stackItem == UniversalCoins.itemCoin
					|| stackItem == UniversalCoins.itemSmallCoinStack
					|| stackItem == UniversalCoins.itemLargeCoinStack
					|| stackItem == UniversalCoins.itemCoinHeap;
		} else { // noinspection RedundantIfStatement
			return slot == itemInputSlot || slot == itemCoinSlot;
		}
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
			coinSum = tagCompound.getInteger("CoinsLeft");
		} catch (Throwable ex2) {
			coinSum = 0;
		}
		try {
			autoMode = tagCompound.getInteger("AutoMode");
		} catch (Throwable ex2) {
			autoMode = 0;
		}
		try {
			coinMode = tagCompound.getInteger("CoinMode");
		} catch (Throwable ex2) {
			coinMode = 0;
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
		tagCompound.setInteger("AutoMode", autoMode);
		tagCompound.setInteger("CoinMode", coinMode);
	}

	public void updateEntity() {
		super.updateEntity();
		activateBuySellButtons();
		activateRetrieveButtons();
		runAutoMode();
		runCoinMode();
	}

	private void activateBuySellButtons() {
		if (inventory[itemInputSlot] == null) {
			itemPrice = 0;
			buyButtonActive = false;
			sellButtonActive = false;
		} else {
			if (!worldObj.isRemote) itemPrice = UCItemPricer.getItemPrice(inventory[itemInputSlot]);
			if (itemPrice == -1) {
				itemPrice = 0;
				buyButtonActive = false;
				sellButtonActive = false;
			} else {
				sellButtonActive = true;
				//disable sell button if item is enchanted
				//TODO add pricing for selling enchanted items
				if (inventory[itemInputSlot].isItemEnchanted()) sellButtonActive = false;
				buyButtonActive = (inventory[itemOutputSlot] == null || (inventory[itemOutputSlot])
						.getItem() == inventory[itemInputSlot].getItem()
						&& inventory[itemOutputSlot].stackSize < inventory[itemInputSlot]
								.getItem().getItemStackLimit())
						&& coinSum >= itemPrice;
			}
		}
	}

	private void activateRetrieveButtons() {
		coinButtonActive = false;
		sStackButtonActive = false;
		isStackButtonActive = false;
		heapButtonActive = false;
		if (coinSum > 0) {
			coinButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.itemCoin && inventory[itemOutputSlot].stackSize != 64);
		}
		if (coinSum >= 9) {
			sStackButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.itemSmallCoinStack && inventory[itemOutputSlot].stackSize != 64);
		}
		if (coinSum >= 81) {
			isStackButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.itemLargeCoinStack && inventory[itemOutputSlot].stackSize != 64);
		}
		if (coinSum >= 729) {
			heapButtonActive = inventory[itemOutputSlot] == null
					|| (inventory[itemOutputSlot].getItem() == UniversalCoins.itemCoinHeap && inventory[itemOutputSlot].stackSize != 64);
		}
	}

	public void onSellPressed() {
		onSellPressed(1);
	}

	public void onSellPressed(int amount) {
		if (worldObj.isRemote) {
			return;
		}
		if (inventory[itemInputSlot] == null) {
			sellButtonActive = false;
			return;
		}
		if (amount > inventory[itemInputSlot].stackSize) {
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[itemInputSlot]);
		if (itemPrice == -1) {
			sellButtonActive = false;
			return;
		}
		//handle damaged items
		if (inventory[itemInputSlot].isItemDamaged()) {
			itemPrice = itemPrice * (inventory[itemInputSlot].getMaxDamage() - 
					inventory[itemInputSlot].getItemDamage()) / inventory[itemInputSlot].getMaxDamage();
		}
		inventory[itemInputSlot].stackSize -= amount;
		if (inventory[itemInputSlot].stackSize <= 0) {
			inventory[itemInputSlot] = null;
		}
		coinSum += itemPrice * amount;
	}

	public void onSellMaxPressed() {
		int amount = 0;
		if (inventory[itemInputSlot] == null) {
			if (inventory[itemInputSlot] == null) {
				sellButtonActive = false;
				return;
			}
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[itemInputSlot]);
		if (itemPrice == -1) {
			sellButtonActive = false;
			return;
		}

		amount = inventory[itemInputSlot].stackSize;

		if (amount != 0) {
			onSellPressed(amount);
		}
	}

	public void onBuyPressed() {
		onBuyPressed(1);
	}

	public void onBuyPressed(int amount) {
		if (worldObj.isRemote) {
			return;
		}
		if (inventory[itemInputSlot] == null) {
			buyButtonActive = false;
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[itemInputSlot]);
		if (itemPrice == -1 || coinSum < itemPrice * amount) {
			buyButtonActive = false;
			return;
		}
		if (inventory[itemOutputSlot] == null
				&& inventory[itemInputSlot].getMaxStackSize() >= amount) {
			coinSum -= itemPrice * amount;
			if (inventory[itemInputSlot].isItemDamaged() || inventory[itemInputSlot].isItemEnchanted()) {
				inventory[itemOutputSlot] = new ItemStack(inventory[itemInputSlot].getItem(), 1);
			} else inventory[itemOutputSlot] = inventory[itemInputSlot].copy();
			inventory[itemOutputSlot].stackSize = amount;
		} else if (inventory[itemOutputSlot].getItem() == inventory[itemInputSlot]
				.getItem()
				&& inventory[itemOutputSlot].getItemDamage() == inventory[itemInputSlot]
						.getItemDamage()
				&& inventory[itemOutputSlot].stackSize + amount <= inventory[itemInputSlot]
						.getMaxStackSize()) {
			coinSum -= itemPrice * amount;
			inventory[itemOutputSlot].stackSize += amount;
		} else {
			buyButtonActive = false;
		}
	}

	public void onBuyMaxPressed() {
		int amount = 0;
		if (inventory[itemInputSlot] == null) {
			buyButtonActive = false;
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[itemInputSlot]);
		if (itemPrice == -1 || coinSum < itemPrice) { // can't buy even one
			buyButtonActive = false;
			return;
		}

		if (inventory[itemOutputSlot] == null) { // empty stack
			if (inventory[itemInputSlot].getMaxStackSize() * itemPrice <= coinSum) {
				amount = inventory[itemInputSlot].getMaxStackSize(); // buy one
																		// stack
			} else {
				amount = coinSum / itemPrice; // buy as many as i can.
			}
		} else if (inventory[itemOutputSlot].getItem() == inventory[itemInputSlot]
				.getItem()
				&& inventory[itemOutputSlot].getItemDamage() == inventory[itemInputSlot]
						.getItemDamage()
				&& inventory[itemOutputSlot].stackSize < inventory[itemInputSlot]
						.getItem().getItemStackLimit()) {

			if ((inventory[itemOutputSlot].getMaxStackSize() - inventory[itemOutputSlot].stackSize)
					* itemPrice <= coinSum) {
				amount = inventory[itemOutputSlot].getMaxStackSize()
						- inventory[itemOutputSlot].stackSize;
				// buy as much as i can fit in a stack
			} else {
				amount = coinSum / itemPrice; // buy as many as i can.
			}
		} else {
			buyButtonActive = false;
		}
		onBuyPressed(amount);
	}

	public void onAutoModeButtonPressed() {
		if (autoMode == 0) {
			autoMode = 1;
		} else if (autoMode == 1) {
			autoMode = 2;
		} else autoMode = 0;
	}
	
	public void onCoinModeButtonPressed() {
		if (coinMode == 0) {
			coinMode = 1;
		} else if (coinMode == 1) {
			coinMode = 2;
		} else if (coinMode == 2) {
			coinMode = 3;
		} else if (coinMode == 3) {
			coinMode = 4;
		} else coinMode = 0;
	}

	public void runAutoMode() {
		if (autoMode == 0 || this.worldObj.isRemote) {
			return;
		} else if (autoMode == 1) {
			onBuyMaxPressed();
		} else if (autoMode == 2) {
			onSellMaxPressed();
			// FMLLog.info("UC: coins = " + coinSum);
		}
	}
	
	public void runCoinMode() {
		if (coinMode == 0 || this.worldObj.isRemote) {
			return;
		} else if (coinMode == 1) {
			onRetrieveButtonsPressed(coinMode + 1, true);
		} else if (coinMode == 2) {
			onRetrieveButtonsPressed(coinMode + 1, true);
		} else if (coinMode == 3) {
			onRetrieveButtonsPressed(coinMode + 1, true);
		} else if (coinMode == 4) {
			onRetrieveButtonsPressed(coinMode + 1, true);
		}
	}
	
	public void onRetrieveButtonsPressed(int buttonClickedID,
			boolean shiftPressed) {
		int absoluteButton = buttonClickedID - UCTradeStationGUI.idCoinButton;
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

	// Client Server Sync
	@Override
	public void onDataPacket(NetworkManager net,
			S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
		//FMLLog.info("UC: received S35 packet");
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	public void sendPacket(int button, boolean shiftPressed) {
		PacketTradingStation packet = new PacketTradingStation(xCoord, yCoord,
				zCoord, button, shiftPressed);
		UniversalCoins.packetPipeline.sendToServer(packet);
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	public void onInventoryChanged() {
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return var1 == 0 ? slots_bottom : (var1 == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		//first check if items inserted are coins. put them in the coin input slot if they are.
		if (var1 == itemCoinSlot && (var2.getItem() == (UniversalCoins.itemCoin)
						|| var2.getItem() == (UniversalCoins.itemSmallCoinStack)
						|| var2.getItem() == (UniversalCoins.itemLargeCoinStack) || var2
						.getItem() == (UniversalCoins.itemCoinHeap))) {
			return true;
			//put everything else in the item input slot
		} else if (var1 == itemInputSlot) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		//allow pulling items from output slot only
		if (var1 == 2) {
			return true;
		} else {
			return false;
		}
	}
}
