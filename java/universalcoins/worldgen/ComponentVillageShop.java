package universalcoins.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import universalcoins.UniversalCoins;
import universalcoins.tile.TileVendor;
import universalcoins.util.UCItemPricer;

public class ComponentVillageShop extends StructureVillagePieces.Village {

	private int averageGroundLevel = -1;

	public ComponentVillageShop(Start startPiece, int p5, Random random, StructureBoundingBox box, int p4) {
		super(startPiece, p5);
		this.coordBaseMode = p4;
		this.boundingBox = box;
		MapGenStructureIO.func_143031_a(ComponentVillageShop.class, "ViUS");
	}

	public static ComponentVillageShop buildComponent(Start startPiece, List pieces, Random random, int p1, int p2,
			int p3, int p4, int p5) {
		StructureBoundingBox box = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 5, 7, p4);
		return canVillageGoDeeper(box) && StructureComponent.findIntersecting(pieces, box) == null ? new ComponentVillageShop(
				startPiece, p5, random, box, p4) : null;
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
		if (this.averageGroundLevel < 0) {
			this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

			if (this.averageGroundLevel < 0) {
				return true;
			}

			this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 5 - 1, 0);
		}

		// Clear area
		fillWithAir(world, sbb, 0, 0, 0, 5, 6, 7);
		// start with block
		fillWithBlocks(world, sbb, 0, 0, 0, 5, 0, 7, Blocks.double_stone_slab, Blocks.double_stone_slab, false);
		// main wall
		fillWithBlocks(world, sbb, 0, 1, 0, 5, 1, 7, Blocks.planks, Blocks.planks, false);
		// front
		fillWithBlocks(world, sbb, 0, 2, 0, 5, 3, 0, Blocks.fence, Blocks.fence, false);
		// back
		fillWithBlocks(world, sbb, 0, 2, 7, 5, 3, 7, Blocks.fence, Blocks.fence, false);
		// top
		fillWithBlocks(world, sbb, 0, 4, 0, 5, 4, 7, Blocks.planks, Blocks.planks, false);
		fillWithBlocks(world, sbb, 1, 5, 1, 4, 5, 6, Blocks.planks, Blocks.planks, false);
		// clear main
		fillWithAir(world, sbb, 1, 1, 1, 4, 4, 6);
		// clear door
		fillWithAir(world, sbb, 2, 1, 0, 3, 2, 0);
		// clear back
		fillWithAir(world, sbb, 1, 2, 7, 4, 2, 7);
		// torches
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 1, 4, 1, boundingBox);
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 4, 4, 1, boundingBox);
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 1, 4, 6, boundingBox);
		placeBlockAtCurrentPosition(world, Blocks.torch, 0, 4, 4, 6, boundingBox);
		// signs
		fillWithMetadataBlocks(world, sbb, 1, 1, 1, 1, 1, 6, UniversalCoins.proxy.wall_ucsign, getSignMeta(5),
				UniversalCoins.proxy.wall_ucsign, getSignMeta(5), false);
		fillWithMetadataBlocks(world, sbb, 4, 1, 1, 4, 1, 6, UniversalCoins.proxy.wall_ucsign, getSignMeta(4),
				UniversalCoins.proxy.wall_ucsign, getSignMeta(4), false);
		// vending blocks
		fillWithBlocks(world, sbb, 0, 2, 1, 0, 2, 6, UniversalCoins.proxy.blockVendor,
				UniversalCoins.proxy.blockVendor, false);
		fillWithBlocks(world, sbb, 5, 2, 1, 5, 2, 6, UniversalCoins.proxy.blockVendor,
				UniversalCoins.proxy.blockVendor, false);

		// list of items
		List<Item> saleItems = new ArrayList();

		// fill left vending blocks
		for (int i = 0; i < 6; i++) {
			int priceModifier = random.nextInt(UniversalCoins.shopMaxPrice - UniversalCoins.shopMinPrice)
					+ UniversalCoins.shopMinPrice;
			ItemStack stack = UCItemPricer.getInstance().getRandomPricedStack();
			while (saleItems.contains(stack.getItem())) {
				stack = UCItemPricer.getInstance().getRandomPricedStack();
			}
			int price = UCItemPricer.getInstance().getItemPrice(stack);
			addVendorItems(world, 0, 2, i + 1, stack, price * priceModifier / 100);
			saleItems.add(stack.getItem());
		}
		// fill right vending blocks
		for (int i = 0; i < 6; i++) {
			int priceModifier = random.nextInt(UniversalCoins.shopMaxPrice - UniversalCoins.shopMinPrice)
					+ UniversalCoins.shopMinPrice;
			ItemStack stack = UCItemPricer.getInstance().getRandomPricedStack();
			while (saleItems.contains(stack.getItem())) {
				stack = UCItemPricer.getInstance().getRandomPricedStack();
			}
			int price = UCItemPricer.getInstance().getItemPrice(stack);
			addVendorItems(world, 5, 2, i + 1, stack, price * priceModifier / 100);
			saleItems.add(stack.getItem());
		}

		// add stairs if needed
		if (this.getBlockAtCurrentPosition(world, 2, 0, -1, sbb).getMaterial() == Material.air
				&& this.getBlockAtCurrentPosition(world, 2, -1, -1, sbb).getMaterial() != Material.air) {
			this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs,
					this.getMetadataWithOffset(Blocks.stone_stairs, 3), 2, 0, -1, sbb);
			this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs,
					this.getMetadataWithOffset(Blocks.stone_stairs, 3), 3, 0, -1, sbb);
		}

		// build foundation
		for (int k = 0; k < 8; ++k) { // length
			for (int l = 0; l < 6; ++l) {// width
				this.clearCurrentPositionBlocksUpwards(world, l, 6, k, sbb);// set 6 to sbb height +1
				this.func_151554_b(world, Blocks.cobblestone, 0, l, -1, k, sbb);
			}
		}

		return true;
	}

	protected void addVendorItems(World world, int par4, int par5, int par6, ItemStack stack, int price) {
		int i1 = this.getXWithOffset(par4, par6);
		int j1 = this.getYWithOffset(par5);
		int k1 = this.getZWithOffset(par4, par6);

		if (world.getBlock(i1, j1, k1) == UniversalCoins.proxy.blockVendor) {
			TileEntity tentity = world.getTileEntity(i1, j1, k1);
			if (tentity instanceof TileVendor) {
				TileVendor tileentity = (TileVendor) tentity;
				tileentity.infiniteMode = true;
				tileentity.itemPrice = price;
				tileentity.setSellItem(stack);
				tileentity.updateTE();
				tileentity.updateSigns();
			}
		}
	}

	private int getSignMeta(int meta) {
		// sign meta/rotation 2=S ,3=N ,4=E ,5=W
		// coordBaseMode 0=S ,1=W ,2=N ,3=E
		// returns meta value needed to rotate sign normally
		if (coordBaseMode == 0) {
			if (meta == 2)
				return 3;
			if (meta == 3)
				return 2;
			if (meta == 4)
				return 4;
			if (meta == 5)
				return 5;
			return 2;
		}
		if (coordBaseMode == 1) {
			if (meta == 2)
				return 4;
			if (meta == 3)
				return 5;
			if (meta == 4)
				return 2;
			if (meta == 5)
				return 3;
			return 4;
		}
		if (coordBaseMode == 2) {
			if (meta == 2)
				return 2;
			if (meta == 3)
				return 3;
			if (meta == 4)
				return 4;
			if (meta == 5)
				return 5;
			return 3;
		}
		if (coordBaseMode == 3) {
			if (meta == 2)
				return 5;
			if (meta == 3)
				return 4;
			if (meta == 4)
				return 2;
			if (meta == 5)
				return 3;
			return 5;
		}
		return 5;
	}
}
