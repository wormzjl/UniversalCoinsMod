package universalcoins.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;

public class ItemBlockVendor extends ItemBlockWithMetadata {

	public ItemBlockVendor(Block block) {
		super(block, block);
		setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int meta){
	return meta;
	}
}
