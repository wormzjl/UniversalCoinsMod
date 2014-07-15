package universalcoins.net;

import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import universalcoins.UCTileEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCTileEntityMessage implements IMessage, IMessageHandler<UCTileEntityMessage, IMessage> {
public int x, y, z, coinSum, itemPrice, autoMode, coinMode;

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
        this.autoMode = tileEntity.autoMode;
        this.coinMode = tileEntity.coinMode;        
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.coinSum = buf.readInt();
        this.itemPrice = buf.readInt();
        this.autoMode = buf.readInt();
        this.coinMode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(coinSum);
        buf.writeInt(itemPrice);
        buf.writeInt(autoMode);
        buf.writeInt(coinMode);
    }

	@Override
	public IMessage onMessage(UCTileEntityMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld
				.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof UCTileEntity) {
			((UCTileEntity) tileEntity).coinSum = message.coinSum;
			((UCTileEntity) tileEntity).itemPrice = message.itemPrice;
			((UCTileEntity) tileEntity).autoMode = message.autoMode;
			((UCTileEntity) tileEntity).coinMode = message.coinMode;
		}
		return null;
	}
}