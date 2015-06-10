package universalcoins.gui;

import java.text.DecimalFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import universalcoins.tile.TileBandit;

public class GuiBanditConfig extends GuiScreen {
    private TileBandit tileBandit;
    private GuiButton costEdit, fourMatchEdit, fiveMatchEdit, editButton, doneButton;
    private int xSize = 175;
    private int ySize = 121;
    private int x = 0;
    private int y = 0;
    private DecimalFormat formatter = new DecimalFormat("#,###,###,###");
    private double payoutPercentage = 0;
    private GuiTextField textField;
    private String catDisplay = "Fee";
    private int spinFee, fourMatch, fiveMatch;

	public GuiBanditConfig(TileBandit tileEntity) {
		this.tileBandit = (TileBandit) tileEntity;
	}
	
	/**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
		x = (width - xSize) / 2;
		y = (height - ySize) / 2;
    	costEdit = new GuiSlimButton(0 , x + 13, y + 52, 12, 12, StatCollector.translateToLocal(""));
    	fourMatchEdit = new GuiSlimButton(1 , x + 25, y + 52, 12, 12, StatCollector.translateToLocal(""));
    	fiveMatchEdit = new GuiSlimButton(2 , x + 37, y + 52, 12, 12, StatCollector.translateToLocal(""));
    	editButton = new GuiSlimButton(3 , x + 99, y + 52, 30, 12, StatCollector.translateToLocal("Edit"));
    	doneButton = new GuiSlimButton(4 , x + 129, y + 52, 30, 12, StatCollector.translateToLocal("Save"));
        this.buttonList.clear();
		buttonList.add(costEdit);
		buttonList.add(fourMatchEdit);
		buttonList.add(fiveMatchEdit);
		buttonList.add(editButton);
		buttonList.add(doneButton);
		
		textField = new GuiTextField(this.fontRendererObj, x + 88, y + 36, 70, 14);
		textField.setFocused(false);
		textField.setMaxStringLength(10);
		textField.setText(String.valueOf(tileBandit.spinFee));
		textField.setEnableBackgroundDrawing(false);
		
		spinFee = tileBandit.spinFee;
		fourMatch = tileBandit.fourMatchPayout;
		fiveMatch = tileBandit.fiveMatchPayout;
    }
    
    protected void keyTyped(char c, int i) {
		if (textField.isFocused()) {
			if (i == 14 || (i > 1 && i < 12)) {
				textField.textboxKeyTyped(c, i);
			}
		} else super.keyTyped(c, i);
	}
	
	public void onGuiClosed() {
		//tileBandit.sendServerUpdateMessage();
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
        	if (button.id == 0) {
        		catDisplay = "Fee";
        		textField.setText(String.valueOf(spinFee));
        	}
        	if (button.id == 1) {
        		catDisplay = "Four Match";
        		textField.setText(String.valueOf(fourMatch));
        	}
        	if (button.id == 2) {
        		catDisplay = "Five Match";
        		textField.setText(String.valueOf(fiveMatch));
        	}
        	if (button.id == 3) {
        		if (!textField.isFocused()) {
        			textField.setFocused(true);
        		} else {
        			try {
                        //tileEntity.itemPrice = Integer.parseInt(textField.getText());
                    } catch (NumberFormatException ex) {
                        // iPrice is a non-numeric string, do nothing
                    } catch (Throwable ex2) {
                        // fail silently?
                    }
                    textField.setFocused(false);
        		}
    		}
            if (button.id == 4) {
                this.tileBandit.markDirty();
                this.mc.displayGuiScreen((GuiScreen)null);
            }      
        updatePayoutPercentage();
        }
    }

	
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3) {
        final ResourceLocation texture = new ResourceLocation("universalcoins", "textures/gui/banditConfig.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		fontRendererObj.drawString(tileBandit.getInventoryName(), x + 6, y + 5, 4210752);
		String cost = String.valueOf(formatter.format(spinFee));
		int stringWidth = fontRendererObj.getStringWidth(cost);
		fontRendererObj.drawString("Fee", x + 15, y + 40, 4210752);
		fontRendererObj.drawString(cost, x + 156 - stringWidth, y + 40, 4210752);
	
		String label = String.valueOf(String.valueOf("Payout:"));
		stringWidth = fontRendererObj.getStringWidth(label);
		fontRendererObj.drawString(label, x + 98 - stringWidth, y + 98, 4210752);
		String percent = String.valueOf(String.valueOf(payoutPercentage) + "%");
		stringWidth = fontRendererObj.getStringWidth(percent);
		fontRendererObj.drawString(percent, x + 156 - stringWidth, y + 98, 4210752);
				
		super.drawScreen(par1, par2, par3);
    }
    
    private void updatePayoutPercentage() {
    	payoutPercentage = ((450 * (double)tileBandit.fourMatchPayout + 10 * (double)tileBandit.fiveMatchPayout) / 100000 * tileBandit.spinFee * 100 );
    }
}