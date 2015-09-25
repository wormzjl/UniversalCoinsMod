package universalcoins.gui;

import java.text.DecimalFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import universalcoins.inventory.ContainerPowerReceiver;
import universalcoins.tile.TilePowerReceiver;

public class PowerReceiverGUI extends GuiContainer {
	private TilePowerReceiver tEntity;
	private GuiButton coinButton;
	public static final int idCoinButton = 0;
	DecimalFormat formatter = new DecimalFormat("#,###,###,###");

	public PowerReceiverGUI(InventoryPlayer inventoryPlayer, TilePowerReceiver tileEntity) {
		super(new ContainerPowerReceiver(inventoryPlayer, tileEntity));
		tEntity = tileEntity;

		xSize = 176;
		ySize = 152;
	}

	@Override
	public void initGui() {
		super.initGui();
		coinButton = new GuiSlimButton(idCoinButton, 123 + (width - xSize) / 2, 55 + (height - ySize) / 2, 46, 12,
				StatCollector.translateToLocal("general.button.coin"));
		buttonList.clear();
		buttonList.add(coinButton);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/powerReceiver.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		fontRendererObj.drawString(tEntity.getInventoryName(), 6, 5, 4210752);

		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 6, 58, 4210752);
		
		// display rf sold
		String formattedrf = formatter.format(tEntity.rfLevel);
		int rfLength = fontRendererObj.getStringWidth(formattedrf + " RF");
		fontRendererObj.drawString(formattedrf + " RF", 142 - rfLength, 21, 4210752);

		// display coin balance
		String formattedBalance = formatter.format(tEntity.coinSum);
		int balLength = fontRendererObj.getStringWidth(formattedBalance);
		fontRendererObj.drawString(formattedBalance, 142 - balLength, 43, 4210752);
	}
	
	protected void actionPerformed(GuiButton button) {
		tEntity.sendPacket(button.id, isShiftKeyDown());
	}
}