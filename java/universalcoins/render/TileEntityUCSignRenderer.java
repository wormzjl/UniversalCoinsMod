package universalcoins.render;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import universalcoins.UniversalCoins;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityUCSignRenderer extends TileEntitySignRenderer {
	
	    private static final ResourceLocation field_147513_b = new ResourceLocation("textures/entity/sign.png");
	    private final ModelSign field_147514_c = new ModelSign();
	    private int counter = 0;

		public void renderTileEntityAt(TileEntitySign p_147512_1_, double p_147512_2_, double p_147512_4_, double p_147512_6_, float p_147512_8_)
	    {
	        Block block = p_147512_1_.getBlockType();
	        GL11.glPushMatrix();
	        float f1 = 0.6666667F;
	        float f3;

	        if (block == UniversalCoins.proxy.standing_ucsign)
	        {
	            GL11.glTranslatef((float)p_147512_2_ + 0.5F, (float)p_147512_4_ + 0.75F * f1, (float)p_147512_6_ + 0.5F);
	            float f2 = (float)(p_147512_1_.getBlockMetadata() * 360) / 16.0F;
	            GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
	            this.field_147514_c.signStick.showModel = true;
	        }
	        else
	        {
	            int j = p_147512_1_.getBlockMetadata();
	            f3 = 0.0F;

	            if (j == 2)
	            {
	                f3 = 180.0F;
	            }

	            if (j == 4)
	            {
	                f3 = 90.0F;
	            }

	            if (j == 5)
	            {
	                f3 = -90.0F;
	            }

	            GL11.glTranslatef((float)p_147512_2_ + 0.5F, (float)p_147512_4_ + 0.75F * f1, (float)p_147512_6_ + 0.5F);
	            GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
	            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
	            this.field_147514_c.signStick.showModel = false;
	        }

	        this.bindTexture(field_147513_b);
	        GL11.glPushMatrix();
	        GL11.glScalef(f1, -f1, -f1);
	        this.field_147514_c.renderSign();
	        GL11.glPopMatrix();
	        FontRenderer fontrenderer = this.func_147498_b();
	        f3 = 0.016666668F * f1;
	        GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
	        GL11.glScalef(f3, -f3, f3);
	        GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
	        GL11.glDepthMask(false);
	        byte b0 = 0;

	        for (int i = 0; i < p_147512_1_.signText.length; ++i)
	        {
	            String s = p_147512_1_.signText[i];

	            if (i == p_147512_1_.lineBeingEdited)
	            {
	                s = "> " + s + " <";
	                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - p_147512_1_.signText.length * 5, b0);
	            }
	            else  {
	            	String colorCode = "§0";
            		if (s.startsWith("&") && s.length() > 1 && String.valueOf(s.charAt(1)).matches("[0-9a-fA-F]+")) {
            			colorCode = "§" + s.substring(1, 2);
            			s = s.substring(2);
            		}
	            	if (s.length() <= 16) {
	            		fontrenderer.drawString(colorCode + s, -fontrenderer.getStringWidth(s) / 2, i * 10 - p_147512_1_.signText.length * 5, b0);
	            	} else {
	            		//display a subset of string while scrolling through entire string
	            		String subset = "";
	            		if (counter / 10 < s.length() - 8) {
	            			subset = s.substring(Math.min(counter / 10, s.length() - 15), Math.min(counter / 10 + 15, s.length()));
	            		} else  {	            		
	            			subset = s.substring(0, 15);
	            		}
            			fontrenderer.drawString(colorCode + subset, -fontrenderer.getStringWidth(subset) / 2, i * 10 - p_147512_1_.signText.length * 5, b0);
	            		counter++;
	            		if (counter / 10 > s.length() + 8) counter = 0;
	            	}
	            }
	        }

	        GL11.glDepthMask(true);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        GL11.glPopMatrix();
	    }

	    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
	    {
	        this.renderTileEntityAt((TileEntitySign)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
	    }
}
