package universalcoins.net;

import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import universalcoins.UCTileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCTileEntityMessage implements IMessage, IMessageHandler<UCTileEntityMessage, IMessage> {
public int x, y, z, coinSum, itemPrice;
public String customName;

    public UCTileEntityMessage()
    {
    }

    public UCTileEntityMessage(UCTileEntity tileEntity)
    {
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
        this.coinSum = tileEntity.coinSum;
        this.itemPrice = tileEntity.itemPrice;
        this.customName = tileEntity.getInventoryName();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.coinSum = buf.readInt();
        this.itemPrice = buf.readInt();
        this.customName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(coinSum);
        buf.writeInt(itemPrice);
        ByteBufUtils.writeUTF8String(buf, customName);
    }

	@Override
	public IMessage onMessage(UCTileEntityMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld
				.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof UCTileEntity) {
			//FMLLog.info("UC: received TE packet");
			((UCTileEntity) tileEntity).coinSum = message.coinSum;
			((UCTileEntity) tileEntity).itemPrice = message.itemPrice;
			((UCTileEntity) tileEntity).setInventoryName(message.customName);
		}
		return null;
	}
}