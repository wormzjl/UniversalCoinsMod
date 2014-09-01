package universalcoins.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalcoins.UniversalCoins;
import universalcoins.inventory.ContainerTradeStation;
import universalcoins.tile.TileTradeStation;

public class TradeStationGUI extends GuiContainer {
	
	private TileTradeStation tileEntity;
	private GuiButton buyButton, sellButton, coinModeButton, autoModeButton, depositButton, withdrawButton;
	private GuiCoinButton retrCoinButton, retrSStackButton, retrLStackButton, retrSBagButton, retrLBagButton;
	public static final int idBuyButton = 0;
	public static final int idSellButton = 1;
	public static final int idCoinButton = 2;
	private static final int idSStackButton = 3;
	private static final int idLStackButton = 4;
	public static final int idSBagButton = 5;
	public static final int idLBagButton = 6;
	public static final int idCoinModeButton = 7;
	public static final int idAutoModeButton = 8;
	public static final int idDepositButton = 9;
	public static final int idWithdrawButton = 10;	

	boolean shiftPressed = false;
	boolean autoMode = UniversalCoins.autoModeEnabled;
	boolean cardTabMaximized = false;
	private int xDrawSize = 176;  //width of main GUI
	private int xOffset = 32; //we offset everything by 32px so it stays centered on the screen
	
	public String[] autoLabels = {"Off","Buy","Sell"};
	
	public TradeStationGUI(InventoryPlayer inventoryPlayer,
			TileTradeStation parTileEntity) {
		super(new ContainerTradeStation(inventoryPlayer, parTileEntity));
		tileEntity = parTileEntity;
		xSize = 240; // main GUI +64 px. This allows the slide out tab to have a
						// slot since all slots must be within the GUI
						// dimensions or items will be thrown out when clicked.
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buyButton = new GuiSlimButton(idBuyButton, xOffset + 36 + (width - xSize) / 2, 21 + (height - ySize) / 2, 25, 12, "Buy");
		sellButton = new GuiSlimButton(idSellButton, xOffset + 36 + (width - xSize) / 2, 38 + (height - ySize) / 2, 25, 12, "Sell");
		retrCoinButton = new GuiCoinButton(idCoinButton, xOffset + 80 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 0);
		retrSStackButton = new GuiCoinButton(idSStackButton, xOffset + 98 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 1);
		retrLStackButton = new GuiCoinButton(idLStackButton, xOffset + 116 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 2);
		retrSBagButton = new GuiCoinButton(idSBagButton, xOffset + 134 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 3);
		retrLBagButton = new GuiCoinButton(idLBagButton, xOffset + 152 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 4);
		coinModeButton = new GuiSlimButton(idCoinModeButton, xOffset + 110 + (width - xSize) / 2, 98 + (height - ySize) / 2, 28, 12, "Coin");
		depositButton = new GuiSlimButton(idDepositButton, xOffset + 180 + (width - xSize) / 2, 50 + (height - ySize) / 2, 50, 12, "Deposit");
		withdrawButton = new GuiSlimButton(idWithdrawButton, xOffset + 180 + (width - xSize) / 2, 64 + (height - ySize) / 2, 50, 12, "Withdraw");
		buttonList.clear();
		buttonList.add(buyButton);
		buttonList.add(sellButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrSBagButton);
		buttonList.add(retrLBagButton);
		buttonList.add(coinModeButton);
		buttonList.add(depositButton);
		buttonList.add(withdrawButton);
		depositButton.visible = false;
		withdrawButton.visible = false;
		
		
		//display only if auto buy/sell enabled?
		if (autoMode) {
			autoModeButton = new GuiSlimButton(idAutoModeButton, xOffset + 6 + (width - xSize) / 2, 84 + (height - ySize) / 2, 28, 12, "Mode");
			buttonList.add(autoModeButton);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString(tileEntity.getInventoryName(), xOffset + 6, 5, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRendererObj.drawString(
				StatCollector.translateToLocal("container.inventory"), xOffset + 6,
				ySize - 96 + 2, 4210752);
		fontRendererObj.drawString(String.valueOf(tileEntity.coinSum), xOffset + 98, 57, 4210752);
		String priceInLocal = "Price:";
		int stringWidth = fontRendererObj.getStringWidth(priceInLocal);
		fontRendererObj.drawString(priceInLocal, xOffset + 38 - stringWidth, 57, 4210752);
		if (tileEntity.itemPrice != 0){
			fontRendererObj.drawString(String.valueOf(tileEntity.itemPrice), xOffset + 41, 57,
					4210752);
		}
		else{
			fontRendererObj.drawString("No item", xOffset + 41, 57,
					4210752);
		}
		//display only if auto buy/sell enabled
		if (autoMode) {
			fontRendererObj.drawString(StatCollector.translateToLocal("Auto Buy/Sell"), xOffset + 6, 74, 4210752);
			fontRendererObj.drawString(StatCollector.translateToLocal(autoLabels[tileEntity.autoMode]), xOffset + 38, 87, 4210752);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

		buyButton.enabled = tileEntity.buyButtonActive;
		sellButton.enabled = tileEntity.sellButtonActive;
		retrCoinButton.enabled = tileEntity.coinButtonActive;
		retrSStackButton.enabled = tileEntity.isSStackButtonActive;
		retrLStackButton.enabled = tileEntity.isLStackButtonActive;
		retrSBagButton.enabled = tileEntity.isSBagButtonActive;
		retrLBagButton.enabled = tileEntity.isLBagButtonActive;

		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/tradeStation.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(xOffset + x, y, 0, 0, xDrawSize, ySize);
		
		//draw auto mode box if auto buy/sell enabled
		if (autoMode) {
			this.drawTexturedModalRect(xOffset + x + 35, y + 83, 176, 75, 38, 15);
		}
		
		//draw highlight over currently selected coin type (coinMode)
		int xHighlight[] = {0, 81, 99, 117, 135, 153};
		if (tileEntity.coinMode > 0) {
			this.drawTexturedModalRect(xOffset + x + xHighlight[tileEntity.coinMode], y + 94, 176, 90, 16, 2);
		}
		if (cardTabMaximized) {
			//draw card slider tab full 
			this.drawTexturedModalRect(xOffset + x + 176, y + 12, 176, 0, 65, 75);
		} else {
			//draw card slider tab minimized
			this.drawTexturedModalRect(xOffset + x + 176, y + 12, 232, 0, 10, 75);
		}
	}
	
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			shiftPressed = true;
		}
		else {
			shiftPressed = false;
		}
		if (par1GuiButton.id == idBuyButton){
			if ( shiftPressed ) {
				tileEntity.onBuyMaxPressed();
			}
			else {
				tileEntity.onBuyPressed();
			}
		}else if (par1GuiButton.id == idSellButton){
			if ( shiftPressed ) {
				tileEntity.onSellMaxPressed();
			}
			else {
				tileEntity.onSellPressed();
			}
		}else if (par1GuiButton.id == idAutoModeButton) {
			tileEntity.onAutoModeButtonPressed();
		}else if (par1GuiButton.id == idCoinModeButton) {
			tileEntity.onCoinModeButtonPressed();
		}else if (par1GuiButton.id <= idLBagButton) {
			tileEntity.onRetrieveButtonsPressed(par1GuiButton.id, shiftPressed);
		}else if (par1GuiButton.id == idDepositButton) {
			tileEntity.onDepositButtonPressed(shiftPressed);
		}else if (par1GuiButton.id == idWithdrawButton) {
			tileEntity.onWithdrawButtonPressed(shiftPressed);
		}
		tileEntity.sendPacket(par1GuiButton.id, shiftPressed);
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
				//tileEntity.sendPacket(42, !cardTabMaximized);
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
