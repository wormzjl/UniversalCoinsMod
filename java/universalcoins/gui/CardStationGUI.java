package universalcoins.gui;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.common.FMLLog;
import universalcoins.UniversalCoins;
import universalcoins.inventory.ContainerCardStation;
import universalcoins.tile.TileCardStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class CardStationGUI extends GuiContainer{
	private GuiButton buttonOne, buttonTwo, buttonThree, buttonFour;
	private GuiTextField coinAmountField;
	private TileCardStation tEntity;
	public static final int idButtonOne = 0;
	public static final int idButtonTwo = 1;
	public static final int idButtonThree = 2;
	public static final int idButtonFour = 3;
	
	public int menuState = 0;
	private static final String[] menuStateName = new String[] { "welcome",	"auth", "main",  
		"deposit", "withdraw", "newcard", "transferaccount", "takecard", "takecoins", 
		"insufficient", "invalid", "badcard", "unauthorized"};
	int barProgress = 0;
	
	boolean shiftPressed = false;

	public CardStationGUI(InventoryPlayer inventoryPlayer, TileCardStation tileEntity) {
		super(new ContainerCardStation(inventoryPlayer, tileEntity));
		tEntity = tileEntity;
		
		xSize = 176;
		ySize = 201;
	}
	
	@Override
	protected void keyTyped(char c, int i) {
		if (menuState == 4) {
			coinAmountField.setFocused(true);
			coinAmountField.textboxKeyTyped(c, i);
			coinAmountField.setFocused(false);
		} else super.keyTyped(c, i);

	}
	
	@Override
	public void initGui() {
		super.initGui();
		buttonOne = new GuiSlimButton(idButtonOne, 8 + (width - xSize) / 2, 30 + (height - ySize) / 2, 18, 12, "");
		buttonTwo = new GuiSlimButton(idButtonTwo, 8 + (width - xSize) / 2, 50 + (height - ySize) / 2, 18, 12, "");
		buttonThree = new GuiSlimButton(idButtonThree, 8 + (width - xSize) / 2, 70 + (height - ySize) / 2, 18, 12, "");
		buttonFour = new GuiSlimButton(idButtonFour, 8 + (width - xSize) / 2, 90 + (height - ySize) / 2, 18, 12, "");
		buttonList.clear();
		buttonList.add(buttonOne);
		buttonList.add(buttonTwo);
		buttonList.add(buttonThree);
		buttonList.add(buttonFour);
		
		coinAmountField = new GuiTextField(this.fontRendererObj, 1, 1, 100, 13);
		coinAmountField.setFocused(false);
		coinAmountField.setMaxStringLength(9);
		coinAmountField.setText("0");
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/cardStation.png");
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		if (menuState == 0 && super.inventorySlots.getSlot(0).getStack() != null && tEntity.accountBalance >= 0) {
			menuState = 2;
		}
		if (menuState == 1) {
			//state 1 is auth - run eye scan
			barProgress++;
			this.drawTexturedModalRect(x + 151, y + 19, 176, 0, 18, 18);
			this.drawTexturedModalRect(x + 34, y + 43, 0, 201, Math.min(barProgress, 104), 5);
			if (barProgress > 105) {
				fontRendererObj.drawString(StatCollector.translateToLocal("cardstation.auth.access"), x + 34, y + 52, 4210752);
			}
			if (barProgress > 120) {
				if (!tEntity.accountNumber.matches("none")) {
					fontRendererObj.drawString(StatCollector.translateToLocal("cardstation.auth.success"), x + 34, y + 72, 4210752);
					if (barProgress > 160) {menuState = 2;}
				} else {
					fontRendererObj.drawString(StatCollector.translateToLocal("cardstation.auth.fail"), x + 34, y + 72, 4210752);
					if (barProgress > 160) {menuState = 5;}
				}
			}
		}
		if (menuState == 2 && tEntity.accountBalance == -1) {
			tEntity.sendButtonMessage(6, false); //message to destroy card
			menuState = 11;
		}
		
		DecimalFormat formatter = new DecimalFormat("#,###,###,###");
		if (menuState == 3) {
			fontRendererObj.drawString(formatter.format(tEntity.accountBalance), x + 34, y + 52, 4210752);
		}
		if (menuState == 4) {
			//display account balance
			fontRendererObj.drawString(formatter.format(tEntity.accountBalance), x + 34, y + 32, 4210752);
			fontRendererObj.drawString(coinAmountField.getText(), x + 34, y + 52, 4210752);
		}
		if (menuState == 12) {
			barProgress++;
			if (barProgress > 100) {
				menuState = 2;
				barProgress = 0;
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		// draw text and stuff here
		// the parameters for drawString are: string, x, y, color
		fontRendererObj.drawString(tEntity.getInventoryName(), 6, 5, 4210752);
		// draws "Inventory" or your regional equivalent
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 6, ySize - 96 + 2, 4210752);
		drawMenu(menuState);
	}
	
	protected void actionPerformed(GuiButton button) {
		//We are not going to send button IDs to the server
		//instead, we are going to use function IDs to send
		//a packet to the server to do things
		int functionID = 0;
		switch (menuState) {
			case 0:
				//welcome
				//we run function 5 here to get the player account info
				if (button.id == idButtonOne){functionID = 5;menuState = 1;}
				if (button.id == idButtonTwo){functionID = 5;menuState = 1;}
				if (button.id == idButtonThree){functionID = 5;menuState = 1;}
				if (button.id == idButtonFour){functionID = 5;menuState = 1;}
				barProgress = 0; //always set scan progress to zero so scan will take place next time
				break;
			case 1:
				//authentication
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){}
				break;
			case 2:
				//main menu
				if (button.id == idButtonOne){functionID = 3;menuState = 3;}
				if (button.id == idButtonTwo){menuState = 4;}
				if (button.id == idButtonThree){
					if (!tEntity.cardOwner.contentEquals(tEntity.player)) {
						menuState = 12;
					} else menuState = 5;}
				if (button.id == idButtonFour){
					if (!tEntity.cardOwner.contentEquals(tEntity.player)) {
						menuState = 12;
					} else menuState = 6;}
				break;
			case 3:
				//deposit
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){menuState = 2;}
				break;
			case 4:
				//withdraw
				int coinWithdrawalAmount = 0;
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){//Output coins;
					try { coinWithdrawalAmount = Integer.parseInt(coinAmountField.getText());
					} catch (NumberFormatException ex) {
		                menuState = 10;
		            } catch (Throwable ex2) {
		                menuState = 10;
		            }
					if (coinWithdrawalAmount > tEntity.accountBalance) {
						menuState = 9;
					} else if (coinWithdrawalAmount <= 0) {
						menuState = 10;
					} else { 
						//send message to server with withdrawal amount
						tEntity.sendServerUpdatePacket(coinWithdrawalAmount);
						functionID = 4;
						menuState = 8;} }
				if (button.id == idButtonFour){menuState = 2;}
				break;
			case 5:
				//new card
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){
					//TODO error if coins not present
					
					functionID = 1;menuState = 7;}
				if (button.id == idButtonFour){menuState = 0;}
				break;
			case 6:
				//transfer account
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){functionID = 2;menuState = 7;}
				if (button.id == idButtonFour){menuState = 2;}
				break;
			case 7:
				//take card
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){menuState = 0;}
				break;
			case 8:
				//take coins
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){menuState = 0;}
				break;
			case 9:
				//Insufficient funds
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){menuState = 4;}
				break;
			case 10:
				//Invalid input
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){menuState = 4;}
				break;
			case 11:
				//Bad card - account not found
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){}
				break;
			case 12:
				//unauthorized access - card does not belong to player
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){}
				break;
			default:
				//we should never get here
				if (button.id == idButtonOne){}
				if (button.id == idButtonTwo){}
				if (button.id == idButtonThree){}
				if (button.id == idButtonFour){menuState = 1;}				
				break;
		}
		//We include the shift boolean for compatibility with the button message only. It does nothing
		if (Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			shiftPressed = true;
		}
		else {
			shiftPressed = false;
		}
		//function1 - new card
		//function2 - transfer account
		//function3 - deposit
		//function4 - withdraw
		//function5 - get account info
		//function6 - destroy invalid card - called from drawGuiContainerBackgroundLayer 
		  //since no buttons are clicked when inserting card
		tEntity.sendButtonMessage(functionID, shiftPressed);
	}
	
	private void drawMenu(int state) {
        fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".one"), 34, 22, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".two"), 34, 32, 4210752);
	 	fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".three"), 34, 42, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".four"), 34, 52, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".five"), 34, 62, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".six"), 34, 72, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".seven"), 34, 82, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("cardstation." + menuStateName[state] + ".eight"), 34, 92, 4210752);
	}
}
