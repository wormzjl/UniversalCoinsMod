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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class VendorSaleGUI extends GuiContainer{
	private TileVendor tileEntity;
	private GuiButton buyButton, retrCoinButton, retrSStackButton, retrLStackButton, retrSBagButton, retrLBagButton;
	public static final int idBuyButton = 7;
	public static final int idCoinButton = 8;
	private static final int idSStackButton = 9;
	private static final int idLStackButton = 10;
	public static final int idSBagButton = 11;
	public static final int idLBagButton = 12;
	
	boolean shiftPressed = false;

	public VendorSaleGUI(InventoryPlayer inventoryPlayer, TileVendor tEntity) {
		super(new ContainerVendorSale(inventoryPlayer, tEntity));
		tileEntity = tEntity;
		
		xSize = 176;
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buyButton = new GuiButton(idBuyButton, 124 + (width - xSize) / 2, 42 + (height - ySize) / 2, 42, 11, "Buy");
		retrCoinButton = new GuiButton(idCoinButton, 42 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "");
		retrSStackButton = new GuiButton(idSStackButton, 60 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "");
		retrLStackButton = new GuiButton(idLStackButton, 78 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "");
		retrSBagButton = new GuiButton(idSBagButton, 96 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "");
		retrLBagButton = new GuiButton(idLBagButton, 114 + (width - xSize) / 2, 84 + (height - ySize) / 2, 18, 18, "");
		buttonList.clear();
		buttonList.add(buyButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrSBagButton);
		buttonList.add(retrLBagButton);
	}
	
	private void drawOverlay() {
		int x, y, u = 176, v = 0;
		//int x_offset = -125, y_offset = -20;
		int x_offset = -guiLeft;
		int y_offset = -guiTop;
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/tradeStation.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 43 + x_offset;
		y = (height - ySize) / 2 + 85 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 61 + x_offset;
		y = (height - ySize) / 2 + 85 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 80 + x_offset;
		y = (height - ySize) / 2 + 84 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 17;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 96 + x_offset;
		y = (height - ySize) / 2 + 86 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);
		
		v = 0;
		u = 191;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 114 + x_offset;
		y = (height - ySize) / 2 + 85 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/vendor-sale.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		buyButton.enabled = tileEntity.buyButtonActive;
		retrCoinButton.enabled = tileEntity.uCoinButtonActive;
		retrSStackButton.enabled = tileEntity.uSStackButtonActive;
		retrLStackButton.enabled = tileEntity.uLStackButtonActive;
		retrSBagButton.enabled = tileEntity.uSBagButtonActive;
		retrLBagButton.enabled = tileEntity.uLBagButtonActive;		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString("Vending Block", 6, 5, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRendererObj.drawString(StatCollector.translateToLocal(
				"container.inventory"), 6, ySize - 96 + 2, 4210752);
		fontRendererObj.drawString(String.valueOf(tileEntity.itemPrice), 46, 29, 4210752);
		fontRendererObj.drawString(String.valueOf(tileEntity.userCoinSum), 46, 71, 4210752);

		drawOverlay();
	}
	
	protected void actionPerformed(GuiButton button) {
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			shiftPressed = true;
		}
		else {
			shiftPressed = false;
		}
		if (button.id == idBuyButton){
			if (shiftPressed) {
				tileEntity.onBuyMaxPressed();
			}
			else {
				tileEntity.onBuyPressed();
			}
		}
		else if (button.id <= idLBagButton) {
			tileEntity.onRetrieveButtonsPressed(button.id, shiftPressed);
		}
		tileEntity.sendButtonMessage(button.id, shiftPressed);
	}
}
