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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class VendorGUI extends GuiContainer{
	private TileVendor tileEntity;
	private GuiTextField itemPriceField;
	private GuiButton updateButton, setButton;
	private GuiCoinButton retrCoinButton, retrSStackButton, retrLStackButton, retrSBagButton, retrLBagButton;
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
		updateButton = new GuiButton(idUpdateButton, 80 + (width - xSize) / 2, 35 + (height - ySize) / 2, 42, 11, "Edit");
		setButton = new GuiButton(idSetButton, 124 + (width - xSize) / 2, 35 + (height - ySize) / 2, 42, 11, "Save");
		retrCoinButton = new GuiCoinButton(idCoinButton, 78 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 0);
		retrSStackButton = new GuiCoinButton(idSStackButton, 96 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 1);
		retrLStackButton = new GuiCoinButton(idLStackButton, 114 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 2);
		retrSBagButton = new GuiCoinButton(idSBagButton, 132 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 3);
		retrLBagButton = new GuiCoinButton(idLBagButton, 150 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 4);
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
		} else if (button.id <= idLBagButton) {
			tileEntity.onRetrieveButtonsPressed(button.id, shiftPressed);
			tileEntity.sendButtonMessage(button.id, shiftPressed);
		}
	}
}
