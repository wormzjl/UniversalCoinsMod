package universalcoins.net;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCTileVendorMessage implements IMessage, IMessageHandler<UCTileVendorMessage, IMessage> {
public int x, y, z, coinSum, userCoinSum, itemPrice;
public String blockOwner;
public ItemStack sellItem;
public boolean sellMode, infiniteSell;

    public UCTileVendorMessage()
    {
    }

    public UCTileVendorMessage(TileVendor tileEntity)
    {
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
        this.coinSum = tileEntity.coinSum;
        this.userCoinSum = tileEntity.userCoinSum;
        this.itemPrice = tileEntity.itemPrice;
        this.blockOwner = tileEntity.blockOwner;
        this.sellItem = tileEntity.getSellItem();
        this.sellMode = tileEntity.sellMode;
        this.infiniteSell = tileEntity.infiniteSell;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.coinSum = buf.readInt();
        this.userCoinSum = buf.readInt();
        this.itemPrice = buf.readInt();
        this.blockOwner = ByteBufUtils.readUTF8String(buf);
        this.sellItem = ByteBufUtils.readItemStack(buf);
        this.sellMode = buf.readBoolean();
        this.infiniteSell = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(coinSum);
        buf.writeInt(userCoinSum);
        buf.writeInt(itemPrice);
        ByteBufUtils.writeUTF8String(buf, blockOwner);
        ByteBufUtils.writeItemStack(buf, sellItem);
        buf.writeBoolean(sellMode);
        buf.writeBoolean(infiniteSell);
    }

	@Override
	public IMessage onMessage(UCTileVendorMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld
				.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof TileVendor) {
			((TileVendor) tileEntity).coinSum = message.coinSum;
			((TileVendor) tileEntity).userCoinSum = message.userCoinSum;
			((TileVendor) tileEntity).itemPrice = message.itemPrice;
			((TileVendor) tileEntity).blockOwner = message.blockOwner;
			((TileVendor) tileEntity).setSellItem(message.sellItem);
			((TileVendor) tileEntity).sellMode = message.sellMode;
			((TileVendor) tileEntity).infiniteSell = message.infiniteSell;
		}
		return null;
	}
}