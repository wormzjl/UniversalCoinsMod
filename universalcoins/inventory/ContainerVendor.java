package universalcoins.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVendor extends Container {
	private TileVendor tileEntity;
	private int lastCoinSum;
	private int lastUserCoinSum;
	private int lastItemPrice;
	private int xOffset = 32;
	
	public ContainerVendor(InventoryPlayer inventoryPlayer, TileVendor tEntity) {
		tileEntity = tEntity;
		// the Slot constructor takes the IInventory and the slot number in that
		// it binds to and the x-y coordinates it resides on-screen
		addSlotToContainer(new UCSlotGhost(tileEntity, TileVendor.itemSellingSlot, xOffset + 8, 17));
		addSlotToContainer(new UCSlotOutput(tileEntity, TileVendor.itemCoinOutputSlot, xOffset + 133, 57));
		addSlotToContainer(new UCSlotCard(tileEntity, TileVendor.itemCardSlot, xOffset + 181, Integer.MAX_VALUE));
		
		//add all the inventory storage slots
		for (int i = 0; i < 9; i++) {
				addSlotToContainer(new Slot(tileEntity, TileVendor.itemStorageSlot1 + i, xOffset + 8 + i * 18, 96));
		}
		
		// commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	
	void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						xOffset + 8 + j * 18, 119 + i * 18));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, xOffset + 8 + i * 18, 177));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		// null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			
			// merges the item into player inventory since its in the tileEntity
			if (slot < 12) {
				if (!this.mergeItemStack(stackInSlot, 12, 48, true)) {
					return null;
				}
			}
			// places it into the tileEntity is possible since its in the player
			// inventory
			else {
				boolean foundSlot = false;
				for (int i = 0; i < 12; i++){
					if (((Slot)inventorySlots.get(i)).isItemValid(stackInSlot) && this.mergeItemStack(stackInSlot, i, i + 1, false)) {
						foundSlot = true;
						break;
					}
				}
				if (!foundSlot){
					return null;
				}
			}
			
			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			}
			else {
				slotObject.onSlotChanged();
			}
			
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		
		return stack;
	}
	
	/**
     * Looks for changes made in the container, sends them to every listener.
     */
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.lastCoinSum != this.tileEntity.coinSum
            		|| this.lastUserCoinSum != this.tileEntity.userCoinSum
            		|| this.lastItemPrice != this.tileEntity.itemPrice) {
                //update
            	tileEntity.updateTE();
            }

		this.lastCoinSum = this.tileEntity.coinSum;
		this.lastUserCoinSum = this.tileEntity.userCoinSum;
		this.lastItemPrice = this.tileEntity.itemPrice;
        }
	}
	
	@SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            //this.tileEntity.autoMode = par2;
        }
    }

}
