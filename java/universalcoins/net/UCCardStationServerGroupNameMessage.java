package universalcoins.net;

import universalcoins.tile.TileCardStation;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCCardStationServerGroupNameMessage  implements IMessage, IMessageHandler<UCCardStationServerGroupNameMessage, IMessage> {
	private int x, y, z;
	private String groupName;

    public UCCardStationServerGroupNameMessage() {}

    public UCCardStationServerGroupNameMessage(int x, int y, int z, String stringGroupName) { 
    	this.x = x;
    	this.y = y;
    	this.z = z;
        this.groupName = stringGroupName;
    }

    @Override
    public void toBytes(ByteBuf buf) { 
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeUTF8String(buf, groupName);    }

    @Override
    public void fromBytes(ByteBuf buf) { 
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.groupName = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public IMessage onMessage(UCCardStationServerGroupNameMessage message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;

		TileEntity tileEntity = world.getTileEntity(message.x, message.y, message.z);
		if (tileEntity instanceof TileCardStation) {
			((TileCardStation) tileEntity).groupAccountName = message.groupName;
			}
			return null;
	}
}
