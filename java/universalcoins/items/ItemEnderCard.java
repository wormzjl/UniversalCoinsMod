package universalcoins.items;

import java.text.DecimalFormat;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import universalcoins.UniversalCoins;
import universalcoins.util.UniversalAccounts;

public class ItemEnderCard extends Item {

	private static final int[] multiplier = new int[] { 1, 9, 81, 729, 6561 };
	private static final Item[] coins = new Item[] { UniversalCoins.proxy.itemCoin,
			UniversalCoins.proxy.itemSmallCoinStack, UniversalCoins.proxy.itemLargeCoinStack,
			UniversalCoins.proxy.itemSmallCoinBag, UniversalCoins.proxy.itemLargeCoinBag };

	public ItemEnderCard() {
		super();
		this.maxStackSize = 1;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
				.registerIcon(UniversalCoins.MODID + ":" + this.getUnlocalizedName().substring(5));
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
		if (stack.stackTagCompound != null) {
			list.add(stack.stackTagCompound.getString("Name"));
			list.add(stack.stackTagCompound.getString("Account"));
		} else {
			list.add(StatCollector.translateToLocal("item.itemUCCard.warning"));
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side,
			float px, float py, float pz) {
		if (world.isRemote)
			return true;
		if (itemstack.stackTagCompound == null) {
			createNBT(itemstack, world, player);
		}
		long accountBalance = UniversalAccounts.getInstance()
				.getAccountBalance(itemstack.stackTagCompound.getString("Account"));
		DecimalFormat formatter = new DecimalFormat("#,###,###,###,###,###,###");
		ItemStack[] inventory = player.inventory.mainInventory;
		String accountNumber = itemstack.stackTagCompound.getString("Account");
		int coinsDeposited = 0;
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null && (inventory[i].getItem() == UniversalCoins.proxy.itemCoin
					|| inventory[i].getItem() == UniversalCoins.proxy.itemSmallCoinStack
					|| inventory[i].getItem() == UniversalCoins.proxy.itemLargeCoinStack
					|| inventory[i].getItem() == UniversalCoins.proxy.itemSmallCoinBag
					|| inventory[i].getItem() == UniversalCoins.proxy.itemLargeCoinBag)) {
				if (accountBalance == -1)
					return true; // get out of here if the card is invalid
				int coinType = getCoinType(inventory[i].getItem());
				if (coinType == -1)
					return true; // something went wrong
				int coinValue = multiplier[coinType];
				if (UniversalAccounts.getInstance().creditAccount(accountNumber, inventory[i].stackSize * coinValue)) {
					coinsDeposited += inventory[i].stackSize * coinValue;
					player.inventory.setInventorySlotContents(i, null);
					player.inventoryContainer.detectAndSendChanges();
				}
			}
		}
		if (coinsDeposited > 0) {
			player.addChatMessage(
					new ChatComponentText(StatCollector.translateToLocal("item.itemEnderCard.message.deposit") + " "
							+ formatter.format(coinsDeposited) + " "
							+ StatCollector.translateToLocal("item.itemCoin.name")));
		}
		player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("item.itemEnderCard.balance") + " "
				+ formatter.format(UniversalAccounts.getInstance().getAccountBalance(accountNumber))));
		return true;
	}

	private void createNBT(ItemStack stack, World world, EntityPlayer entityPlayer) {
		String accountNumber = UniversalAccounts.getInstance()
				.getOrCreatePlayerAccount(entityPlayer.getPersistentID().toString());
		stack.stackTagCompound = new NBTTagCompound();
		stack.stackTagCompound.setString("Name", entityPlayer.getDisplayName());
		stack.stackTagCompound.setString("Owner", entityPlayer.getPersistentID().toString());
		stack.stackTagCompound.setString("Account", accountNumber);
	}

	private int getCoinType(Item item) {
		for (int i = 0; i < 5; i++) {
			if (item == coins[i]) {
				return i;
			}
		}
		return -1;
	}
}
