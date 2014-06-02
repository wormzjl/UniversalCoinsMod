package universalcoins;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.lwjgl.input.Keyboard;

import universalcoins.net.PacketUpdateTE;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;

public class UCTileEntity extends TileEntity implements IInventory,
		ISidedInventory {
	private ItemStack[] inventory;
	private final int invSize = 5;
	public static final int revenueSlot = 2;
	public static final int tradedItemSlot = 3;
	public static final int boughtItemsSlot = 4;
	public static final int coinInputSlot = 0;
	public static final int coinOutputSlot = 1;
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
	public boolean bypassActive = false;
	public boolean autoModeButtonActive = UniversalCoins.autoModeEnabled;
	private static final int[] slots_top = new int[] { 0, 1, 2, 3, 4 };
	private static final int[] slots_bottom = new int[] { 0, 1, 2, 3, 4 };
	private static final int[] slots_sides = new int[] { 0, 1, 2, 3, 4 };

	public int autoMode = 0;
	public boolean needCoinSumUpdate = false;

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
			if (i == coinInputSlot) {
				int coinType = getCoinType(itemStack.getItem());
				if (coinType != -1) {
					coinSum += itemStack.stackSize * multiplier[coinType];
					inventory[i] = null;
					//FMLLog.info("SetInvSlotContents.. Coin Sum: " + coinSum);
					sendTEPacket(coinSum);
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

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		Item stackItem = itemstack.getItem();
		if (i == coinInputSlot) {
			return stackItem == UniversalCoins.itemCoin
					|| stackItem == UniversalCoins.itemSmallCoinStack
					|| stackItem == UniversalCoins.itemLargeCoinStack
					|| stackItem == UniversalCoins.itemCoinHeap;
		} else { // noinspection RedundantIfStatement
			if (i == tradedItemSlot) {
				return true;
			} else {
				return false;
			}
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
			bypassActive = tagCompound.getBoolean("Bypass");
		} catch (Throwable ex) {
			bypassActive = false;
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
		tagCompound.setBoolean("Bypass", bypassActive);
		tagCompound.setInteger("AutoMode", autoMode);
		// FMLLog.info("UC: Writing NBT - Coinsum: " + coinSum);
	}

	public void updateEntity() {
		super.updateEntity();
		activateBuySellButtons();
		activateRetrieveButtons();
		runAutoMode();
	}

	private void activateBuySellButtons() {
		if (inventory[tradedItemSlot] == null) {
			itemPrice = 0;
			buyButtonActive = false;
			sellButtonActive = false;
		} else {
			itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
			if (itemPrice == -1) {
				itemPrice = 0;
				buyButtonActive = false;
				sellButtonActive = false;
			} else {
				ItemStack revenueStack = UCItemPricer
						.getRevenueStack(itemPrice);
				sellButtonActive = bypassActive
						|| inventory[revenueSlot] == null
						|| (inventory[revenueSlot].getItem() == revenueStack
								.getItem() && inventory[revenueSlot].stackSize
								+ revenueStack.stackSize <= 64);
				buyButtonActive = (inventory[boughtItemsSlot] == null || (inventory[boughtItemsSlot])
						.getItem() == inventory[tradedItemSlot].getItem()
						&& inventory[boughtItemsSlot].stackSize < inventory[tradedItemSlot]
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
			coinButtonActive = inventory[coinOutputSlot] == null
					|| (inventory[coinOutputSlot].getItem() == UniversalCoins.itemCoin && inventory[coinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 9) {
			sStackButtonActive = inventory[coinOutputSlot] == null
					|| (inventory[coinOutputSlot].getItem() == UniversalCoins.itemSmallCoinStack && inventory[coinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 81) {
			isStackButtonActive = inventory[coinOutputSlot] == null
					|| (inventory[coinOutputSlot].getItem() == UniversalCoins.itemLargeCoinStack && inventory[coinOutputSlot].stackSize != 64);
		}
		if (coinSum >= 729) {
			heapButtonActive = inventory[coinOutputSlot] == null
					|| (inventory[coinOutputSlot].getItem() == UniversalCoins.itemCoinHeap && inventory[coinOutputSlot].stackSize != 64);
		}
	}

	public void setBypass(boolean newValue) {
		bypassActive = newValue;
		activateBuySellButtons();
	}

	public void onSellPressed() {
		onSellPressed(1);
	}

	public void onSellPressed(int amount) {
		if (inventory[tradedItemSlot] == null) {
			sellButtonActive = false;
			return;
		}
		if (amount > inventory[tradedItemSlot].stackSize) {
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1) {
			sellButtonActive = false;
			return;
		}
		ItemStack revenueStack = UCItemPricer.getRevenueStack(itemPrice
				* amount);
		// FMLLog.info("Universal Coins: Revenue stack: " + revenueStack);
		if (!bypassActive) {
			if (inventory[revenueSlot] == null) {
				inventory[revenueSlot] = revenueStack;
				inventory[tradedItemSlot].stackSize -= amount;
				if (inventory[tradedItemSlot].stackSize <= 0) {
					inventory[tradedItemSlot] = null;
				}
			} else if (inventory[revenueSlot].getItem() == revenueStack
					.getItem()
					&& inventory[revenueSlot].stackSize
							+ revenueStack.stackSize <= revenueStack
								.getMaxStackSize()) {
				inventory[revenueSlot].stackSize += revenueStack.stackSize;

				inventory[tradedItemSlot].stackSize -= amount;
				if (inventory[tradedItemSlot].stackSize <= 0) {
					inventory[tradedItemSlot] = null;
				}
			} else {
				sellButtonActive = false;
			}
		} else {
			inventory[tradedItemSlot].stackSize -= amount;
			if (inventory[tradedItemSlot].stackSize <= 0) {
				inventory[tradedItemSlot] = null;
			}
			coinSum += itemPrice * amount;
			needCoinSumUpdate = true;
		}

	}

	public void onSellMaxPressed() {
		int amount = 0;
		if (inventory[tradedItemSlot] == null) {
			if (inventory[tradedItemSlot] == null) {
				sellButtonActive = false;
				return;
			}
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1) {
			sellButtonActive = false;
			return;
		}

		ItemStack revenueStack;
		if (!bypassActive) {
			if (inventory[revenueSlot] == null) {
				Integer totalRevenue = inventory[tradedItemSlot].stackSize
						* itemPrice;
				revenueStack = UCItemPricer
						.getRevenueStack(inventory[tradedItemSlot].stackSize
								* itemPrice);
				if (totalRevenue <= 64 * 729) {
					amount = inventory[tradedItemSlot].stackSize;
				} else {
					amount = (64 * 729) / itemPrice;
				}
			} else if (inventory[revenueSlot].stackSize < 64) {
				int lastOK = 0;
				for (int i = 1; i <= inventory[tradedItemSlot].stackSize; i++) {
					revenueStack = UCItemPricer.getRevenueStack(i * itemPrice);
					if (revenueStack.getItem() == inventory[revenueSlot]
							.getItem()
							&& inventory[revenueSlot].stackSize
									+ revenueStack.stackSize <= 64) {
						lastOK = i;
					}
				}
				amount = lastOK;
			}
		} else {
			amount = inventory[tradedItemSlot].stackSize;
		}
		if (amount != 0) {
			onSellPressed(amount);
		}
	}

	public void onBuyPressed() {
		onBuyPressed(1);
	}

	public void onBuyPressed(int amount) {
		if (inventory[tradedItemSlot] == null) {
			buyButtonActive = false;
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1 || coinSum < itemPrice * amount) {
			buyButtonActive = false;
			return;
		}
		if (inventory[boughtItemsSlot] == null
				&& inventory[tradedItemSlot].getMaxStackSize() >= amount) {
			coinSum -= itemPrice * amount;
			inventory[boughtItemsSlot] = ItemStack
					.copyItemStack(inventory[tradedItemSlot]);
			inventory[boughtItemsSlot].stackSize = amount;
			needCoinSumUpdate = true;
		} else if (inventory[boughtItemsSlot].getItem() == inventory[tradedItemSlot]
				.getItem()
				&& inventory[boughtItemsSlot].getItemDamage() == inventory[tradedItemSlot]
						.getItemDamage()
				&& inventory[boughtItemsSlot].stackSize + amount <= inventory[tradedItemSlot]
						.getMaxStackSize()) {
			coinSum -= itemPrice * amount;
			inventory[boughtItemsSlot].stackSize += amount;
			needCoinSumUpdate = true;
		} else {
			buyButtonActive = false;
		}
	}

	public void onBuyMaxPressed() {
		int amount = 0;
		if (inventory[tradedItemSlot] == null) {
			buyButtonActive = false;
			return;
		}
		itemPrice = UCItemPricer.getItemPrice(inventory[tradedItemSlot]);
		if (itemPrice == -1 || coinSum < itemPrice) { // can't buy even one
			buyButtonActive = false;
			return;
		}

		if (inventory[boughtItemsSlot] == null) { // empty stack
			if (inventory[tradedItemSlot].getMaxStackSize() * itemPrice <= coinSum) {
				amount = inventory[tradedItemSlot].getMaxStackSize(); // buy one
																		// stack
			} else {
				amount = coinSum / itemPrice; // buy as many as i can.
			}
		} else if (inventory[boughtItemsSlot].getItem() == inventory[tradedItemSlot]
				.getItem()
				&& inventory[boughtItemsSlot].getItemDamage() == inventory[tradedItemSlot]
						.getItemDamage()
				&& inventory[boughtItemsSlot].stackSize < inventory[tradedItemSlot]
						.getItem().getItemStackLimit()) {

			if ((inventory[boughtItemsSlot].getMaxStackSize() - inventory[boughtItemsSlot].stackSize)
					* itemPrice <= coinSum) {
				amount = inventory[boughtItemsSlot].getMaxStackSize()
						- inventory[boughtItemsSlot].stackSize;
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
		} else if (autoMode == 2) {
			autoMode = 0;
		}
	}

	public void runAutoMode() {
		if (autoMode == 0) {
			return;
		} else if (autoMode == 1) {
			onBuyMaxPressed();
			if (needCoinSumUpdate && !this.worldObj.isRemote) {
				sendTEPacket(coinSum);
			}
			needCoinSumUpdate = false;
		} else if (autoMode == 2) {
			onSellMaxPressed();
			if (needCoinSumUpdate && !this.worldObj.isRemote) {
				sendTEPacket(coinSum);
			}
			needCoinSumUpdate = false;
			// FMLLog.info("UC: coins = " + coinSum);
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
				|| (inventory[coinOutputSlot] != null && inventory[coinOutputSlot]
						.getItem() != itemOnButton)
				|| (inventory[coinOutputSlot] != null && inventory[coinOutputSlot].stackSize == 64)) {
			return;
		}
		if (shiftPressed) {
			if (inventory[coinOutputSlot] == null) {
				int amount = coinSum / multiplier;
				if (amount >= 64) {
					coinSum -= multiplier * 64;
					inventory[coinOutputSlot] = new ItemStack(itemOnButton);
					inventory[coinOutputSlot].stackSize = 64;
				} else {
					coinSum -= multiplier * amount;
					inventory[coinOutputSlot] = new ItemStack(itemOnButton);
					inventory[coinOutputSlot].stackSize = amount;
				}
			}
		} else {
			coinSum -= multiplier;
			if (inventory[coinOutputSlot] == null) {
				inventory[coinOutputSlot] = new ItemStack(itemOnButton);
			} else {
				inventory[coinOutputSlot].stackSize++;
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
				zCoord, button, shiftPressed, bypassActive);
		UniversalCoins.packetPipeline.sendToServer(packet);
	}

	public void sendTEPacket(int coinsum) {
		PacketUpdateTE packet = new PacketUpdateTE(xCoord, yCoord, zCoord,
				coinsum, autoMode);
		UniversalCoins.packetPipeline.sendToAll(packet);
	}

	@Override
	public void openInventory() {
		// mark block dirty to update on open
		this.markDirty();
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub

	}

	public void onInventoryChanged() {
		//FMLLog.info("UniversalCoins: Inventory change requested");

	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return var1 == 0 ? slots_bottom : (var1 == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int var1, ItemStack var2, int var3) {
		//first check if items inserted are coins. put them in the coin input slot if they are.
		if (var1 == 0
				&& (var2.getItem() == (UniversalCoins.itemCoin)
						|| var2.getItem() == (UniversalCoins.itemSmallCoinStack)
						|| var2.getItem() == (UniversalCoins.itemLargeCoinStack) || var2
						.getItem() == (UniversalCoins.itemCoinHeap))) {
			return true;
			//put everything else in the item input slot
		} else if (var1 == 3) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean canExtractItem(int var1, ItemStack var2, int var3) {
		//allow pulling items from traded item slot and coin out slots
		if (var1 == 1 || var1 == 2 || var1 == 4) {
			return true;
		} else {
			return false;
		}
	}
}
