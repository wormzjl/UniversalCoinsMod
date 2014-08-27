package universalcoins.net;

import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import universalcoins.tile.TileCardStation;
import universalcoins.tile.TileTradeStation;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCTileCardStationMessage implements IMessage, IMessageHandler<UCTileCardStationMessage, IMessage> {
public int x, y, z, coinSum;
public String customName;

    public UCTileCardStationMessage()
    {
    }

    public UCTileCardStationMessage(TileTradeStation tileEntity)
    {
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
        this.coinSum = tileEntity.coinSum;
        this.customName = tileEntity.getInventoryName();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.coinSum = buf.readInt();
        this.customName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(coinSum);
        ByteBufUtils.writeUTF8String(buf, customName);
    }

	@Override
	public IMessage onMessage(UCTileCardStationMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld
				.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof TileCardStation) {
			//FMLLog.info("UC: received TE packet");
			((TileCardStation) tileEntity).coinSum = message.coinSum;
			((TileCardStation) tileEntity).setInventoryName(message.customName);
		}
		return null;
	}
}