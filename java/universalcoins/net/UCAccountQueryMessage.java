package universalcoins.net;

import io.netty.buffer.ByteBuf;

import java.text.DecimalFormat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import universalcoins.util.UCWorldData;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCAccountQueryMessage implements IMessage, IMessageHandler<UCAccountQueryMessage, IMessage> {
	private String accountNumber;

    public UCAccountQueryMessage() {}

    public UCAccountQueryMessage(String accountNumber) { 
    	this.accountNumber = accountNumber;
    }

    @Override
    public void toBytes(ByteBuf buf) { 
    	ByteBufUtils.writeUTF8String(buf, accountNumber);
    }

    @Override
    public void fromBytes(ByteBuf buf) { 
    	this.accountNumber = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public IMessage onMessage(UCAccountQueryMessage message, MessageContext ctx) {
		EntityPlayer ePlayer = ctx.getServerHandler().playerEntity;
		World world = ctx.getServerHandler().playerEntity.worldObj;
		int accountCoins = getAccountBalance(world, message.accountNumber);
		DecimalFormat formatter = new DecimalFormat("#,###,###,###");
		ePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal(
					"item.itemUCCard.balance") + " " + formatter.format(accountCoins)));
		return null;

	}
	
	private int getAccountBalance(World world, String accountNumber) {
			return getWorldInt(world, accountNumber);
	}
	
	private int getWorldInt(World world, String tag) {
		UCWorldData wData = UCWorldData.get(world);
		NBTTagCompound wdTag = wData.getData();
		return wdTag.getInteger(tag);
	}
}
