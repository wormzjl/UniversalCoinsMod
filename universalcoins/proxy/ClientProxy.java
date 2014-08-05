package universalcoins.proxy;

import net.minecraftforge.common.MinecraftForge;
import universalcoins.gui.BlockVendorRenderer;
import universalcoins.gui.HintGuiRenderer;
import universalcoins.gui.TileEntityVendorRenderer;
import universalcoins.tile.TileVendor;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

	@Override
	public void registerRenderers() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileVendor.class, new TileEntityVendorRenderer());
		RenderingRegistry.registerBlockHandler(new BlockVendorRenderer(RenderingRegistry.getNextAvailableRenderId()));
		//register handler for GUI hints for vending blocks
		MinecraftForge.EVENT_BUS.register(HintGuiRenderer.instance);
	}

}
