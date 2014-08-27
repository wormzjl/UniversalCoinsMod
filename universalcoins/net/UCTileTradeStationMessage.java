package universalcoins.net;

import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import universalcoins.tile.TileTradeStation;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCTileTradeStationMessage implements IMessage, IMessageHandler<UCTileTradeStationMessage, IMessage> {
public int x, y, z, coinSum, itemPrice;
public String customName;

    public UCTileTradeStationMessage()
    {
    }

    public UCTileTradeStationMessage(TileTradeStation tileEntity)
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
	public IMessage onMessage(UCTileTradeStationMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld
				.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof TileTradeStation) {
			//FMLLog.info("UC: received TE packet");
			((TileTradeStation) tileEntity).coinSum = message.coinSum;
			((TileTradeStation) tileEntity).itemPrice = message.itemPrice;
			((TileTradeStation) tileEntity).setInventoryName(message.customName);
		}
		return null;
	}
}