package universalcoins.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;
import universalcoins.inventory.ContainerVendor;
import universalcoins.tile.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class VendorGUI extends GuiContainer{
	private TileVendor tileEntity;
	private GuiTextField itemPriceField;
	private GuiButton updateButton, setButton, depositButton, withdrawButton;
	private GuiCoinButton retrCoinButton, retrSStackButton, retrLStackButton, retrSBagButton, retrLBagButton;
	public static final int idUpdateButton = 0;
	public static final int idSetButton = 1;
	public static final int idCoinButton = 2;
	private static final int idSStackButton = 3;
	private static final int idLStackButton = 4;
	public static final int idSBagButton = 5;
	public static final int idLBagButton = 6;
	public static final int idDepositButton = 7;
	public static final int idWithdrawButton = 8;
	private boolean textActive = false;
	private boolean shiftPressed = false;
	boolean cardTabMaximized = false;
	private int xDrawSize = 176;  //width of main GUI
	private int xOffset = 32; //we offset everything by 32px so it stays centered on the screen

	public VendorGUI(InventoryPlayer inventoryPlayer, TileVendor tEntity) {
		super(new ContainerVendor(inventoryPlayer, tEntity));
		tileEntity = tEntity;
		
		xSize = 240; // main GUI +64 px. This allows the slide out tab to have a
		// slot since all slots must be within the GUI
		// dimensions or items will be thrown out when clicked.
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		updateButton = new GuiSlimButton(idUpdateButton, xOffset + 80 + (width - xSize) / 2, 35 + (height - ySize) / 2, 42, 12, "Edit");
		setButton = new GuiSlimButton(idSetButton, xOffset + 124 + (width - xSize) / 2, 35 + (height - ySize) / 2, 42, 12, "Save");
		retrCoinButton = new GuiCoinButton(idCoinButton, xOffset + 78 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 0);
		retrSStackButton = new GuiCoinButton(idSStackButton, xOffset + 96 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 1);
		retrLStackButton = new GuiCoinButton(idLStackButton, xOffset + 114 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 2);
		retrSBagButton = new GuiCoinButton(idSBagButton, xOffset + 132 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 3);
		retrLBagButton = new GuiCoinButton(idLBagButton, xOffset + 150 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 4);
		depositButton = new GuiSlimButton(idDepositButton, xOffset + 180 + (width - xSize) / 2, 50 + (height - ySize) / 2, 50, 12, "Deposit");
		withdrawButton = new GuiSlimButton(idWithdrawButton, xOffset + 180 + (width - xSize) / 2, 64 + (height - ySize) / 2, 50, 12, "Withdraw");
		buttonList.clear();
		buttonList.add(updateButton);
		buttonList.add(setButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrSBagButton);
		buttonList.add(retrLBagButton);
		buttonList.add(depositButton);
		buttonList.add(withdrawButton);
		depositButton.visible = false;
		withdrawButton.visible = false;
		
		itemPriceField = new GuiTextField(this.fontRendererObj, xOffset + 82, 21, 86, 15);
		itemPriceField.setFocused(false);
		itemPriceField.setMaxStringLength(10);
		itemPriceField.setEnableBackgroundDrawing(false);
		//itemPriceField.setTextColor(4210752);
	}

	
	protected void keyTyped(char c, int i) {
		if (itemPriceField.isFocused()) {
			itemPriceField.textboxKeyTyped(c, i);
		} else super.keyTyped(c, i);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/vendor.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(xOffset + x, y, 0, 0, xDrawSize, ySize);
		
		retrCoinButton.enabled = tileEntity.coinButtonActive;
		retrSStackButton.enabled = tileEntity.isSStackButtonActive;
		retrLStackButton.enabled = tileEntity.isLStackButtonActive;
		retrSBagButton.enabled = tileEntity.isSBagButtonActive;
		retrLBagButton.enabled = tileEntity.isLBagButtonActive;	
		
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
		String priceInLocal = "Price:";
		int stringWidth = fontRendererObj.getStringWidth(priceInLocal);
		fontRendererObj.drawString(priceInLocal, xOffset + 78 - stringWidth, 22, 4210752);
		//draw itemprice
		if (!textActive) {
		String iSum = String.valueOf(tileEntity.itemPrice);
		stringWidth = fontRendererObj.getStringWidth(iSum);
		fontRendererObj.drawString(iSum, xOffset + 82, 22, 4210752);
		} else itemPriceField.drawTextBox();
		//draw coinsum
		String cSum = String.valueOf(tileEntity.coinSum);
		stringWidth = fontRendererObj.getStringWidth(cSum);
		fontRendererObj.drawString(cSum, xOffset + 125 - stringWidth, 62, 4210752);		
	}
	
	protected void actionPerformed(GuiButton button) {
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			shiftPressed = true;
		}
		else {
			shiftPressed = false;
		}		
		if (button.id == idUpdateButton) {
			itemPriceField.setText(String.valueOf(tileEntity.itemPrice));
			textActive = true;
			itemPriceField.setFocused(true);
		} else if (button.id == idSetButton) {
			String price = itemPriceField.getText();
			if (price != "") {
				int iPrice = Integer.parseInt(price);
				try {
					tileEntity.itemPrice = iPrice;
				} catch (Throwable ex2) {
					// fail silently?
				}
				textActive = false;
				itemPriceField.setFocused(false);
				tileEntity.sendServerUpdateMessage();
			}
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
