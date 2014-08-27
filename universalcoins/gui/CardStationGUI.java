package universalcoins.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;
import universalcoins.inventory.ContainerCardStation;
import universalcoins.tile.TileCardStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class CardStationGUI extends GuiContainer{
	private TileCardStation tileEntity;
	private GuiButton cardButton;
	private GuiCoinButton retrCoinButton, retrSStackButton, retrLStackButton, retrSBagButton, retrLBagButton;
	public static final int idCardButton = 0;
	public static final int idCoinButton = 1;
	private static final int idSStackButton = 2;
	private static final int idLStackButton = 3;
	public static final int idSBagButton = 4;
	public static final int idLBagButton = 5;
	
	boolean shiftPressed = false;

	public CardStationGUI(InventoryPlayer inventoryPlayer, TileCardStation tEntity) {
		super(new ContainerCardStation(inventoryPlayer, tEntity));
		tileEntity = tEntity;
		
		xSize = 176;
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		cardButton = new GuiSlimButton(idCardButton, 57 + (width - xSize) / 2, 
				36 + (height - ySize) / 2, 62, 12, tileEntity.canWithdraw() ? "Withdraw" : "New Card");
		retrCoinButton = new GuiCoinButton(idCoinButton, 43 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 0);
		retrSStackButton = new GuiCoinButton(idSStackButton, 61 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 1);
		retrLStackButton = new GuiCoinButton(idLStackButton, 78 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 2);
		retrSBagButton = new GuiCoinButton(idSBagButton, 97 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 3);
		retrLBagButton = new GuiCoinButton(idLBagButton, 115 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 4);
		buttonList.clear();
		buttonList.add(cardButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrSBagButton);
		buttonList.add(retrLBagButton);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/cardStation.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		cardButton.enabled = tileEntity.isCardButtonActive;
		retrCoinButton.enabled = tileEntity.isCoinButtonActive;
		retrSStackButton.enabled = tileEntity.isSStackButtonActive;
		retrLStackButton.enabled = tileEntity.isLStackButtonActive;
		retrSBagButton.enabled = tileEntity.isSBagButtonActive;
		retrLBagButton.enabled = tileEntity.isLBagButtonActive;		
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString(tileEntity.getInventoryName(), 6, 5, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRendererObj.drawString(StatCollector.translateToLocal(
				"container.inventory"), 6, ySize - 96 + 2, 4210752);
		fontRendererObj.drawString(String.valueOf(tileEntity.coinSum), 46, 60, 4210752);
		cardButton.displayString = (tileEntity.canWithdraw() ? "Withdraw" : "New Card");
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
}
