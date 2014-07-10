package universalcoins;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;



class UCContainer extends Container {
	private UCTileEntity tileEntity;
	private int lastCoinSum;
	private int lastItemPrice;
	private int lastAutoMode;
	private int lastCoinMode;
	
	public UCContainer(InventoryPlayer inventoryPlayer, UCTileEntity tEntity) {
		tileEntity = tEntity;
		// the Slot constructor takes the IInventory and the slot number in that
		// it binds to
		// and the x-y coordinates it resides on-screen
		//addSlotToContainer(new Slot(tileEntity, UCTileEntity.itemCoinSlot, 16, 27));
		addSlotToContainer(new Slot(tileEntity, UCTileEntity.itemInputSlot, 16, 27));
		addSlotToContainer(new UCSlotResult(tileEntity, UCTileEntity.itemOutputSlot, 144, 27));
		
		// commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tileEntity.isUseableByPlayer(entityplayer);
	}
	
	void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
						8 + j * 18, 119 + i * 18));
			}
		}
		
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 177));
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
			if (slot < 2) {
				if (!this.mergeItemStack(stackInSlot, 2, 38, true)) {
					return null;
				}
			}
			// places it into the tileEntity is possible since its in the player
			// inventory
			else {
				boolean foundSlot = false;
				for (int i = 0; i < 2; i++){
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
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.lastCoinSum != this.tileEntity.coinSum)
            {
                icrafting.sendProgressBarUpdate(this, 0, this.tileEntity.coinSum);
            }
            if (this.lastItemPrice != this.tileEntity.itemPrice)
            {
                icrafting.sendProgressBarUpdate(this, 1, this.tileEntity.itemPrice);
            }
            if (this.lastAutoMode != this.tileEntity.autoMode)
            {
                icrafting.sendProgressBarUpdate(this, 2, this.tileEntity.autoMode);
            }
            if (this.lastCoinMode != this.tileEntity.coinMode)
            {
                icrafting.sendProgressBarUpdate(this, 3, this.tileEntity.coinMode);
            }
        }

        this.lastCoinSum = this.tileEntity.coinSum;
        this.lastItemPrice = this.tileEntity.itemPrice;
        this.lastAutoMode = this.tileEntity.autoMode;
        this.lastCoinMode = (int) this.tileEntity.coinMode;
    }
    
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2)
    {
        if (par1 == 0)
        {
            this.tileEntity.coinSum = par2;
        }
        if (par1 == 1)
        {
            this.tileEntity.itemPrice = par2;
        }
        if (par1 == 2)
        {
            this.tileEntity.autoMode = par2;
        }
        if (par1 == 3)
        {
            this.tileEntity.coinMode = par2;
        }
    }

}
