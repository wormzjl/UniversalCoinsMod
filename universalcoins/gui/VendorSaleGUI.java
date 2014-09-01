package universalcoins.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;
import universalcoins.inventory.ContainerVendorSale;
import universalcoins.tile.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class VendorSaleGUI extends GuiContainer{
	private TileVendor tileEntity;
	private GuiButton buyButton, depositButton, withdrawButton;
	private GuiCoinButton retrCoinButton, retrSStackButton, retrLStackButton, retrSBagButton, retrLBagButton;
	public static final int idBuyButton = 9;
	public static final int idCoinButton = 10;
	private static final int idSStackButton = 11;
	private static final int idLStackButton = 12;
	public static final int idSBagButton = 13;
	public static final int idLBagButton = 14;
	public static final int idPDepositButton = 15;
	public static final int idPWithdrawButton = 16;
	
	boolean shiftPressed = false;
	boolean cardTabMaximized = false;
	private int xDrawSize = 176;  //width of main GUI
	private int xOffset = 32; //we offset everything by 32px so it stays centered on the screen

	public VendorSaleGUI(InventoryPlayer inventoryPlayer, TileVendor tEntity) {
		super(new ContainerVendorSale(inventoryPlayer, tEntity));
		tileEntity = tEntity;
		
		xSize = 240; // main GUI +64 px. This allows the slide out tab to have a
		// slot since all slots must be within the GUI
		// dimensions or items will be thrown out when clicked.
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buyButton = new GuiSlimButton(idBuyButton, xOffset + 124 + (width - xSize) / 2, 42 + (height - ySize) / 2, 42, 12, "Buy");
		retrCoinButton = new GuiCoinButton(idCoinButton, xOffset + 42 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "", 0);
		retrSStackButton = new GuiCoinButton(idSStackButton, xOffset + 60 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "", 1);
		retrLStackButton = new GuiCoinButton(idLStackButton, xOffset + 78 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "", 2);
		retrSBagButton = new GuiCoinButton(idSBagButton, xOffset + 96 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "", 3);
		retrLBagButton = new GuiCoinButton(idLBagButton, xOffset + 114 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "", 4);
		depositButton = new GuiSlimButton(idPDepositButton, xOffset + 180 + (width - xSize) / 2, 50 + (height - ySize) / 2, 50, 12, "Deposit");
		withdrawButton = new GuiSlimButton(idPWithdrawButton, xOffset + 180 + (width - xSize) / 2, 64 + (height - ySize) / 2, 50, 12, "Withdraw");
		buttonList.clear();
		buttonList.add(buyButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrSBagButton);
		buttonList.add(retrLBagButton);
		buttonList.add(depositButton);
		buttonList.add(withdrawButton);
		depositButton.visible = false;
		withdrawButton.visible = false;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/vendor-sale.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(xOffset + x, y, 0, 0, xDrawSize, ySize);
		
		buyButton.enabled = tileEntity.buyButtonActive;
		retrCoinButton.enabled = tileEntity.uCoinButtonActive;
		retrSStackButton.enabled = tileEntity.uSStackButtonActive;
		retrLStackButton.enabled = tileEntity.uLStackButtonActive;
		retrSBagButton.enabled = tileEntity.uSBagButtonActive;
		retrLBagButton.enabled = tileEntity.uLBagButtonActive;
		
		if (cardTabMaximized) {
			//draw card slider tab full 
			this.drawTexturedModalRect(xOffset + x + 176, y + 12, 176, 0, 65, 75);
		} else {
			//draw card slider tab minimized
			this.drawTexturedModalRect(xOffset + x + 176, y + 12, 232, 0, 10, 75);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString("Vending Block", xOffset + 6, 5, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRendererObj.drawString(StatCollector.translateToLocal(
				"container.inventory"), xOffset + 6, ySize - 96 + 2, 4210752);
		fontRendererObj.drawString(String.valueOf(tileEntity.itemPrice), xOffset + 46, 29, 4210752);
		fontRendererObj.drawString(String.valueOf(tileEntity.userCoinSum), xOffset + 46, 71, 4210752);
	}
	
	protected void actionPerformed(GuiButton button) {
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			shiftPressed = true;
		}
		else {
			shiftPressed = false;
		}
		tileEntity.sendButtonMessage(button.id, shiftPressed);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int z) {
		// tracking for card tab min/max
		int x = (this.width - this.xSize) / 2; // X asis on GUI
		int y = (this.height - this.ySize) / 2; // Y asis on GUI
		int boxX = (cardTabMaximized ? x + 232 : x + 176);
		int boxY = y + 29;
		int sizeX = 10;
		int sizeY = 35;

		if (mouseX > xOffset + boxX && mouseX < xOffset + boxX + sizeX) {
			if (mouseY > boxY && mouseY < boxY + sizeY) {
				cardTabMaximized = !cardTabMaximized;
				depositButton.visible = cardTabMaximized;
				withdrawButton.visible = cardTabMaximized;
				hideCardSlot(!cardTabMaximized);
			}
		} else {
			super.mouseClicked(mouseX, mouseY, z);
		}
	}
	
	private void hideCardSlot(boolean hide) {
		int defaultCoord = 22;
		int hideCoord = Integer.MAX_VALUE;
		Slot slot = super.inventorySlots.getSlot(2);
		
		if (hide) {
			slot.yDisplayPosition = hideCoord;
		} else {
			slot.yDisplayPosition = defaultCoord;
		}
	}
}
