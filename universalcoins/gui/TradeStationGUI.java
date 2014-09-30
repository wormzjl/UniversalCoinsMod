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
	private GuiButton buyButton, sellButton, coinModeButton, autoModeButton;
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

	boolean shiftPressed = false;
	boolean autoMode = UniversalCoins.autoModeEnabled;
	
	public String[] autoLabels = {"Off","Buy","Sell"};
	
	public TradeStationGUI(InventoryPlayer inventoryPlayer,
			TileTradeStation parTileEntity) {
		super(new ContainerTradeStation(inventoryPlayer, parTileEntity));
		tileEntity = parTileEntity;
		xSize = 176;
		ySize = 200;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buyButton = new GuiSlimButton(idBuyButton, 52 + (width - xSize) / 2, 21 + (height - ySize) / 2, 25, 12, "Buy");
		sellButton = new GuiSlimButton(idSellButton, 52 + (width - xSize) / 2, 38 + (height - ySize) / 2, 25, 12, "Sell");
		retrCoinButton = new GuiCoinButton(idCoinButton, 80 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 0);
		retrSStackButton = new GuiCoinButton(idSStackButton, 98 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 1);
		retrLStackButton = new GuiCoinButton(idLStackButton, 116 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 2);
		retrSBagButton = new GuiCoinButton(idSBagButton, 134 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 3);
		retrLBagButton = new GuiCoinButton(idLBagButton, 152 + (width - xSize) / 2, 74 + (height - ySize) / 2, 18, 18, "", 4);
		coinModeButton = new GuiSlimButton(idCoinModeButton, 110 + (width - xSize) / 2, 98 + (height - ySize) / 2, 28, 12, "Coin");
		buttonList.clear();
		buttonList.add(buyButton);
		buttonList.add(sellButton);
		buttonList.add(retrCoinButton);
		buttonList.add(retrSStackButton);
		buttonList.add(retrLStackButton);
		buttonList.add(retrSBagButton);
		buttonList.add(retrLBagButton);
		buttonList.add(coinModeButton);
		
		
		//display only if auto buy/sell enabled?
		if (autoMode) {
			autoModeButton = new GuiSlimButton(idAutoModeButton, 6 + (width - xSize) / 2, 84 + (height - ySize) / 2, 28, 12, "Mode");
			buttonList.add(autoModeButton);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString(tileEntity.getInventoryName(), 6, 5, 4210752);
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
			fontRendererObj.drawString(StatCollector.translateToLocal("Auto Buy/Sell"), 6, 74, 4210752);
			fontRendererObj.drawString(StatCollector.translateToLocal(autoLabels[tileEntity.autoMode]), 38, 87, 4210752);
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
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		//draw auto mode box if auto buy/sell enabled
		if (autoMode) {
			this.drawTexturedModalRect(x + 35, y + 83, 176, 0, 38, 15);
		}
		
		//draw highlight over currently selected coin type (coinMode)
		int xHighlight[] = {0, 81, 99, 117, 135, 153};
		if (tileEntity.coinMode > 0) {
			this.drawTexturedModalRect(x + xHighlight[tileEntity.coinMode], y + 94, 176, 15, 16, 2);
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
		}
		tileEntity.sendPacket(par1GuiButton.id, shiftPressed);
	}
}
