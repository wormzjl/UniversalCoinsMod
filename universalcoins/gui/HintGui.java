package universalcoins.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;
import universalcoins.tile.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class HintGui extends GuiScreen {
	private static Minecraft mc = Minecraft.getMinecraft();

	public void renderToHud() {
		if ((mc.inGameHasFocus || (mc.currentScreen != null && (mc.currentScreen instanceof GuiChat)))
				&& !mc.gameSettings.showDebugInfo) {
			ScaledResolution res = new ScaledResolution(
					HintGui.mc, HintGui.mc.displayWidth,
					HintGui.mc.displayHeight);
			FontRenderer fontRender = mc.fontRenderer;
			int width = res.getScaledWidth();
			int height = res.getScaledHeight();
			int w = 80;
			int h = 26;
			int centerYOff = -80;
			int cx = width / 2;
			int x = cx - w / 2;
			int y = height / 2 - h / 2 + centerYOff;
			World world = mc.theWorld;
			MovingObjectPosition mop = mc.objectMouseOver;
			if (mop == null) return; //null pointer error bugfix?
			TileEntity te = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
			if (te == null) {
				return;
			}
			if (!(te instanceof TileVendor)) {
				return;
			}
			TileVendor tileEntity = (TileVendor) te;
			ItemStack itemSelling = tileEntity.getSellItem();
			List<String> itemInfoStringList = new ArrayList<String>();
			if (itemSelling != null) {
				if (itemSelling.stackSize > 1) {
					itemInfoStringList.add(itemSelling.stackSize + " " + itemSelling.getDisplayName());
				} else { 
					itemInfoStringList.add(itemSelling.getDisplayName());
				}
				int longestString = itemInfoStringList.get(0).toString().length();
				if (itemSelling.isItemEnchanted()) {
					NBTTagList tagList = itemSelling.getEnchantmentTagList();
					for (int i = 0; i < tagList.tagCount(); i++) {
						NBTTagCompound enchant = ((NBTTagList) tagList)
								.getCompoundTagAt(i);
						String eInfo = Enchantment.enchantmentsList[enchant
								.getInteger("id")].getTranslatedName(enchant
								.getInteger("lvl"));
						if (eInfo.length() > longestString) longestString = eInfo.length();
						itemInfoStringList.add(eInfo);
					}
				}
				itemInfoStringList.add("Price: " + tileEntity.itemPrice);
				//add out of stock notification if not infinite and no stock found
				if (!tileEntity.infiniteSell && !tileEntity.hasSellingInventory()) {
					itemInfoStringList.add("Out Of Stock!");
				}
				// reset height since we now have more lines
				h = (10 * itemInfoStringList.size() + 4);
				if (longestString * 6 > w) { 
					w = longestString * 6;
					x = cx - w / 2;
				}
			} else {
				return;
			}

			int color = 0xffffff;
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0f, 0.0f, -180.0f);
			drawGradientRect(x, y, x + w, y + h, 0xc0101010, 0xd0101010);
			for (int i = 0; i < itemInfoStringList.size(); i++) {
				fontRender.drawString(itemInfoStringList.get(i), x + 4, y + 4
						+ (10 * i), color);
			}
			GL11.glPopMatrix();
		}
	}

}
