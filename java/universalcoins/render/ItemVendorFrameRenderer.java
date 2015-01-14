package universalcoins.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ItemVendorFrameRenderer implements IItemRenderer {
	@Override
	public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack itemStack, ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
		Minecraft mc = Minecraft.getMinecraft();
		TextureManager texturemanager = mc.getTextureManager();
		Item item = itemStack.getItem();
		IIcon iicon = item.getIcon(itemStack, 0);

        if (iicon == null) {
            GL11.glPopMatrix();
            return;
        }

        texturemanager.bindTexture(texturemanager.getResourceLocation(itemStack.getItemSpriteNumber()));
        Tessellator tessellator = Tessellator.instance;
        float f = iicon.getMinU();
        float f1 = iicon.getMaxU();
        float f2 = iicon.getMinV();
        float f3 = iicon.getMaxV();
        float f4 = 0.0F;
        float f5 = 0.3F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef(-f4, -f5, 0.0F);
        float f6 = 1.5F;
        GL11.glScalef(f6, f6, f6);
        GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        texturemanager.bindTexture(texturemanager.getResourceLocation(itemStack.getItemSpriteNumber()));
        TextureUtil.func_147945_b();
    }
	
	/**
     * Renders an item held in hand as a 2D texture with thickness
     */
    public static void renderItemIn2D(Tessellator par0Tessellator, float par1, float par2, float par3, float par4, int par5, int par6, float par7)
    {
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double)par1, (double)par4);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, (double)par1, (double)par2);
        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
        par0Tessellator.addVertexWithUV(0.0D, 1.0D, (double)(0.0F - par7), (double)par1, (double)par2);
        par0Tessellator.addVertexWithUV(1.0D, 1.0D, (double)(0.0F - par7), (double)par3, (double)par2);
        par0Tessellator.addVertexWithUV(1.0D, 0.0D, (double)(0.0F - par7), (double)par3, (double)par4);
        par0Tessellator.addVertexWithUV(0.0D, 0.0D, (double)(0.0F - par7), (double)par1, (double)par4);
        par0Tessellator.draw();
        float f5 = 0.5F * (par1 - par3) / (float)par5;
        float f6 = 0.5F * (par4 - par2) / (float)par6;
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        int k;
        float f7;
        float f8;

        for (k = 0; k < par5; ++k)
        {
            f7 = (float)k / (float)par5;
            f8 = par1 + (par3 - par1) * f7 - f5;
            par0Tessellator.addVertexWithUV((double)f7, 0.0D, (double)(0.0F - par7), (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f7, 0.0D, 0.0D, (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f7, 1.0D, 0.0D, (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f7, 1.0D, (double)(0.0F - par7), (double)f8, (double)par2);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(1.0F, 0.0F, 0.0F);
        float f9;

        for (k = 0; k < par5; ++k)
        {
            f7 = (float)k / (float)par5;
            f8 = par1 + (par3 - par1) * f7 - f5;
            f9 = f7 + 1.0F / (float)par5;
            par0Tessellator.addVertexWithUV((double)f9, 1.0D, (double)(0.0F - par7), (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f9, 1.0D, 0.0D, (double)f8, (double)par2);
            par0Tessellator.addVertexWithUV((double)f9, 0.0D, 0.0D, (double)f8, (double)par4);
            par0Tessellator.addVertexWithUV((double)f9, 0.0D, (double)(0.0F - par7), (double)f8, (double)par4);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, 1.0F, 0.0F);

        for (k = 0; k < par6; ++k)
        {
            f7 = (float)k / (float)par6;
            f8 = par4 + (par2 - par4) * f7 - f6;
            f9 = f7 + 1.0F / (float)par6;
            par0Tessellator.addVertexWithUV(0.0D, (double)f9, 0.0D, (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f9, 0.0D, (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f9, (double)(0.0F - par7), (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f9, (double)(0.0F - par7), (double)par1, (double)f8);
        }

        par0Tessellator.draw();
        par0Tessellator.startDrawingQuads();
        par0Tessellator.setNormal(0.0F, -1.0F, 0.0F);

        for (k = 0; k < par6; ++k)
        {
            f7 = (float)k / (float)par6;
            f8 = par4 + (par2 - par4) * f7 - f6;
            par0Tessellator.addVertexWithUV(1.0D, (double)f7, 0.0D, (double)par3, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f7, 0.0D, (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(0.0D, (double)f7, (double)(0.0F - par7), (double)par1, (double)f8);
            par0Tessellator.addVertexWithUV(1.0D, (double)f7, (double)(0.0F - par7), (double)par3, (double)f8);
        }

        par0Tessellator.draw();
    }
}
