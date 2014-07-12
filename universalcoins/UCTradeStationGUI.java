package universalcoins;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import universalcoins.net.GuiButtonMessage;

public class UCTradeStationGUI extends GuiContainer {
	
	private UCTileEntity tileEntity;
	private GuiButton buyButton, sellButton, retrCoinButton, retrSStackButton, retrLStackButton, retrHeapButton, coinModeButton, autoModeButton;
	public static final int idBuyButton = 0;
	public static final int idSellButton = 1;
	public static final int idCoinButton = 2;
	private static final int idSStackButton = 3;
	private static final int idLStackButton = 4;
	public static final int idHeapButton = 5;
	public static final int idCoinModeButton = 6;
	public static final int idAutoModeButton = 7;

	boolean shiftPressed = false;
	boolean autoMode = UniversalCoins.autoModeEnabled;
	
	public String[] autoLabels = {"Off","Buy","Sell"};
	
	public UCTradeStationGUI(InventoryPlayer inventoryPlayer,
			UCTileEntity parTileEntity) {
		super(new UCContainer(inventoryPlayer, parTileEntity));
		tileEntity = parTileEntity;
		xSize = 176;
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buyButton = new GuiButton(idBuyButton, 36 + (width - xSize) / 2, 22 + (height - ySize) / 2, 25, 11, "Buy");
		sellButton = new GuiButton(idSellButton, 36 + (width - xSize) / 2, 38 + (height - ySize) / 2, 25, 11, "Sell");
		retrCoinButton = new GuiButton(idCoinButton, 95 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		retrSStackButton = new GuiButton(idSStackButton, 114 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		retrLStackButton = new GuiButton(idLStackButton, 133 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		retrHeapButton = new GuiButton(idHeapButton, 152 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "");
		coinModeButton = new GuiButton(idCoinModeButton, 118 + (width - xSize) / 2, 98 + (height - ySize) / 2, 28, 13, "Coin");
		buttonList.clear();
		buttonList.add(buyButton);
		buttonList.add(sellButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrHeapButton);
		buttonList.add(coinModeButton);
		
		//display only if auto buy/sell enabled?
		if (autoMode) {
			autoModeButton = new GuiButton(idAutoModeButton, 10 + (width - xSize) / 2, 84 + (height - ySize) / 2, 28, 13, "Mode");
			buttonList.add(autoModeButton);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString("Universal Coins Trade Station", 6, 5, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRendererObj.drawString(
				StatCollector.translateToLocal("container.inventory"), 6,
				ySize - 96 + 2, 4210752);
		fontRendererObj.drawString(String.valueOf(tileEntity.coinSum), 98, 57, 4210752);
		String priceInLocal = "Price:";
		int stringWidth = fontRendererObj.getStringWidth(priceInLocal);
		fontRendererObj.drawString(priceInLocal, 38 - stringWidth, 57, 4210752);
		if (tileEntity.itemPrice != 0){
			fontRendererObj.drawString(String.valueOf(tileEntity.itemPrice), 41, 57,
					4210752);
		}
		else{
			fontRendererObj.drawString("No item", 41, 57,
					4210752);
		}
		//display only if auto buy/sell enabled
		if (autoMode) {
			fontRendererObj.drawString(StatCollector.translateToLocal("Auto Buy/Sell"), 10, 74, 4210752);
			fontRendererObj.drawString(StatCollector.translateToLocal(autoLabels[tileEntity.autoMode]), 44, 87, 4210752);
		}

		drawOverlay();
	}

	private void drawOverlay() {
		int x, y, u = 176, v = 0;
		//int x_offset = -125, y_offset = -20;
		int x_offset = -guiLeft;
		int y_offset = -guiTop;
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/tradeStation.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 96 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 115 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 134 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

		v += 16;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x = (width - xSize) / 2 + 153 + x_offset;
		y = (height - ySize) / 2 + 75 + y_offset;
		this.drawTexturedModalRect(x, y, u, v, 16, 16);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {

		buyButton.enabled = tileEntity.buyButtonActive;
		sellButton.enabled = tileEntity.sellButtonActive;
		retrCoinButton.enabled = tileEntity.coinButtonActive;
		retrSStackButton.enabled = tileEntity.sStackButtonActive;
		retrLStackButton.enabled = tileEntity.isStackButtonActive;
		retrHeapButton.enabled = tileEntity.heapButtonActive;

		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/tradeStation.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		//drawOverlay();
		
		//draw auto mode box if auto buy/sell enabled
		if (autoMode) {
			this.drawTexturedModalRect(x + 41, y + 83, 176, 63, 40, 15);
		}
		
		//draw highlight over currently selected coin type (coinMode)
		int xHighlight[] = {0, 96, 115, 134, 153};
		if (tileEntity.coinMode > 0) {
			this.drawTexturedModalRect(x + xHighlight[tileEntity.coinMode], y + 94, 178, 82, 16, 2);
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
		}
		else if (par1GuiButton.id == idSellButton){
			if ( shiftPressed ) {
				tileEntity.onSellMaxPressed();
			}
			else {
				tileEntity.onSellPressed();
			}
		}
		else if (par1GuiButton.id == idAutoModeButton) {
			tileEntity.onAutoModeButtonPressed();
		}
		else if (par1GuiButton.id == idCoinModeButton) {
			tileEntity.onCoinModeButtonPressed();
		}
		else if (par1GuiButton.id <= idHeapButton) {
			tileEntity.onRetrieveButtonsPressed(par1GuiButton.id, shiftPressed);
		}
		tileEntity.sendPacket(par1GuiButton.id, shiftPressed);
	}
}
