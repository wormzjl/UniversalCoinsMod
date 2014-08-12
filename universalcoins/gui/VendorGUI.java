package universalcoins.gui;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalcoins.inventory.ContainerVendor;
import universalcoins.tile.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class VendorGUI extends GuiContainer{
	private TileVendor tileEntity;
	private GuiTextField itemPriceField;
	private GuiButton updateButton, setButton, retrCoinButton, retrSStackButton, retrLStackButton, retrSBagButton, retrLBagButton;
	public static final int idUpdateButton = 0;
	public static final int idSetButton = 1;
	public static final int idCoinButton = 2;
	private static final int idSStackButton = 3;
	private static final int idLStackButton = 4;
	public static final int idSBagButton = 5;
	public static final int idLBagButton = 6;
	private boolean textActive = false;
	private boolean shiftPressed = false;

	public VendorGUI(InventoryPlayer inventoryPlayer, TileVendor tEntity) {
		super(new ContainerVendor(inventoryPlayer, tEntity));
		tileEntity = tEntity;
		
		xSize = 176;
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		updateButton = new GuiButton(idUpdateButton, 80 + (width - xSize) / 2, 35 + (height - ySize) / 2, 42, 11, "Change");
		setButton = new GuiButton(idSetButton, 124 + (width - xSize) / 2, 35 + (height - ySize) / 2, 42, 11, "Set");
		retrCoinButton = new GuiButton(idCoinButton, 78 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		retrSStackButton = new GuiButton(idSStackButton, 96 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		retrLStackButton = new GuiButton(idLStackButton, 114 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		retrSBagButton = new GuiButton(idSBagButton, 132 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		retrLBagButton = new GuiButton(idLBagButton, 150 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		buttonList.clear();
		buttonList.add(updateButton);
		buttonList.add(setButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrSBagButton);
		buttonList.add(retrLBagButton);
		
		itemPriceField = new GuiTextField(this.fontRendererObj, 82, 21, 86, 15);
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
	
	protected void mouseClicked(int par1, int par2, int par3) {
        super.mouseClicked(par1, par2, par3);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/vendor.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString("Vending Block", 6, 5, 4210752);
		String priceInLocal = "Price:";
		int stringWidth = fontRendererObj.getStringWidth(priceInLocal);
		fontRendererObj.drawString(priceInLocal, 78 - stringWidth, 22, 4210752);
		//draw itemprice
		if (!textActive) {
		String iSum = String.valueOf(tileEntity.itemPrice);
		stringWidth = fontRendererObj.getStringWidth(iSum);
		fontRendererObj.drawString(iSum, 82, 22, 4210752);
		} else itemPriceField.drawTextBox();
		//draw coinsum
		String cSum = String.valueOf(tileEntity.coinSum);
		stringWidth = fontRendererObj.getStringWidth(cSum);
		fontRendererObj.drawString(cSum, 125 - stringWidth, 62, 4210752);
		drawButtonOverlay();
		
	}
	
	private void drawButtonOverlay() {
		int x, y, u = 176, v = 0;
		//int x_offset = -125, y_offset = -20;
		int x_offset = -guiLeft;
		int y_offset = -guiTop;
		final ResourceLocation texture = new ResourceLocation("universalcoins:textures/gui/vendor.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 79 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 97 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 116 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 133 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);
		
		v = 0;
		u = 191;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 150 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);
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
			int iPrice = Integer.parseInt(price);
			try {
				tileEntity.itemPrice = iPrice;
			} catch (Throwable ex2) {
				//fail silently?
			}
			textActive = false;
			itemPriceField.setFocused(false);
			tileEntity.sendServerUpdateMessage();
		} else if (button.id <= idLBagButton) {
			tileEntity.onRetrieveButtonsPressed(button.id, shiftPressed);
			tileEntity.sendButtonMessage(button.id, shiftPressed);
		}
	}
}
