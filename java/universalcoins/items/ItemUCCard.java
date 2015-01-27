package universalcoins.items;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import universalcoins.UniversalCoins;
import universalcoins.util.UCWorldData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemUCCard extends Item {
	
	public ItemUCCard() {
		super();
		this.maxStackSize = 1;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		this.itemIcon = par1IconRegister.registerIcon(UniversalCoins.modid + ":" + this.getUnlocalizedName().substring(5));
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {		
		if( stack.stackTagCompound != null ) {
			list.add(stack.stackTagCompound.getString("Name"));
			list.add(stack.stackTagCompound.getString("Account"));
		} else {
			list.add(StatCollector.translateToLocal("item.itemUCCard.warning"));
		}
	}
	
	@Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz){
		if (world.isRemote) return true;
		if( itemstack.stackTagCompound == null ) {
			createNBT(itemstack, world, player);
		}
		int accountCoins = getAccountBalance(world, itemstack.stackTagCompound.getString("Account"));
		DecimalFormat formatter = new DecimalFormat("#,###,###,###");
		player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(
					"item.itemUCCard.balance") + " " + formatter.format(accountCoins)));
        return true;
    }
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer entityPlayer) {
		createNBT(stack, world, entityPlayer);
	}
	
	private void createNBT(ItemStack stack, World world, EntityPlayer entityPlayer) {
		String accountNumber = getOrCreatePlayerAccount(world, entityPlayer.getPersistentID().toString());
		stack.stackTagCompound = new NBTTagCompound();
		stack.stackTagCompound.setString("Name", entityPlayer.getDisplayName());
		stack.stackTagCompound.setString("Owner", entityPlayer.getPersistentID().toString());
		stack.stackTagCompound.setString("Account", accountNumber);
	}
	
	private String getOrCreatePlayerAccount(World world, String playerUID) {
		String accountNumber = getWorldString(world, playerUID);
		if (accountNumber == "") {
			while (getWorldString(world, accountNumber) == "") {
				accountNumber = String.valueOf(generateAccountNumber());
				if (getWorldString(world, accountNumber) == "") {
					setWorldData(world, playerUID, accountNumber);
					setWorldData(world, accountNumber, 0);
				}
			}
		}
		return accountNumber;
	}
	
	private int getAccountBalance(World world, String accountNumber) {
		return getWorldInt(world, accountNumber);
	}
	
	private int generateAccountNumber() {
		return (int) (Math.floor(Math.random() * 99999999) + 11111111);
	}

	private int getWorldInt(World world, String tag) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getInteger(tag);
	}
	
	private String getWorldString(World world, String tag) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getString(tag);
	}
	
	private void setWorldData(World world, String tag, String data) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		wdTag.setString(tag, data);
		wData.markDirty();
	}
	
	private void setWorldData(World world, String tag, int data) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		wdTag.setInteger(tag, data);
		wData.markDirty();
	}
}
