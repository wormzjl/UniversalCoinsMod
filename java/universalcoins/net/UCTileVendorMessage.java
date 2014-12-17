package universalcoins.net;

import universalcoins.tile.TileVendor;
import net.minecraft.tileentity.TileEntity;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCTileVendorMessage implements IMessage, IMessageHandler<UCTileVendorMessage, IMessage> {
private int x, y, z, coinSum, userCoinSum, itemPrice;
private boolean infiniteSell, sellMode, buyButtonActive, coinButtonActive, isSStackButtonActive, isLStackButtonActive,
	isSBagButtonActive, isLBagButtonActive, uCoinButtonActive, uSStackButtonActive, uLStackButtonActive, 
	uSBagButtonActive, uLBagButtonActive;
private String blockOwner;

    public UCTileVendorMessage() { }

    public UCTileVendorMessage(TileVendor tileEntity) {
        this.x = tileEntity.xCoord;
        this.y = tileEntity.yCoord;
        this.z = tileEntity.zCoord;
        this.blockOwner = tileEntity.blockOwner;
        this.coinSum = tileEntity.coinSum;
        this.userCoinSum = tileEntity.userCoinSum;
        this.itemPrice = tileEntity.itemPrice;
        this.infiniteSell = tileEntity.infiniteSell;
        this.sellMode = tileEntity.sellMode;
        this.buyButtonActive = tileEntity.buyButtonActive;
        this.coinButtonActive = tileEntity.coinButtonActive;
        this.isSStackButtonActive = tileEntity.isSStackButtonActive;
        this.isLStackButtonActive = tileEntity.isLStackButtonActive;
        this.isSBagButtonActive = tileEntity.isSBagButtonActive;
        this.isLBagButtonActive = tileEntity.isLBagButtonActive;
        this.uCoinButtonActive = tileEntity.uCoinButtonActive;
        this.uSStackButtonActive = tileEntity.uSStackButtonActive;
        this.uLStackButtonActive = tileEntity.uLStackButtonActive;
        this.uSBagButtonActive = tileEntity.uSBagButtonActive;
        this.uLBagButtonActive = tileEntity.uLBagButtonActive;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.blockOwner = ByteBufUtils.readUTF8String(buf);
        this.coinSum = buf.readInt();
        this.userCoinSum = buf.readInt();
        this.itemPrice = buf.readInt();
        this.infiniteSell = buf.readBoolean();
        this.sellMode = buf.readBoolean();
        this.buyButtonActive = buf.readBoolean();
        this.coinButtonActive = buf.readBoolean();
        this.isSStackButtonActive = buf.readBoolean();
        this.isLStackButtonActive = buf.readBoolean();
        this.isSBagButtonActive = buf.readBoolean();
        this.isLBagButtonActive = buf.readBoolean();
        this.uCoinButtonActive = buf.readBoolean();
        this.uSStackButtonActive = buf.readBoolean();
        this.uLStackButtonActive = buf.readBoolean();
        this.uSBagButtonActive = buf.readBoolean();
        this.uLBagButtonActive = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        ByteBufUtils.writeUTF8String(buf,blockOwner);
        buf.writeInt(coinSum);
        buf.writeInt(userCoinSum);
        buf.writeInt(itemPrice);
        buf.writeBoolean(infiniteSell);
        buf.writeBoolean(sellMode);
        buf.writeBoolean(buyButtonActive);
        buf.writeBoolean(coinButtonActive);
        buf.writeBoolean(isSStackButtonActive);
        buf.writeBoolean(isLStackButtonActive);
        buf.writeBoolean(isSBagButtonActive);
        buf.writeBoolean(isLBagButtonActive);
        buf.writeBoolean(uCoinButtonActive);
        buf.writeBoolean(uSStackButtonActive);
        buf.writeBoolean(uLStackButtonActive);
        buf.writeBoolean(uSBagButtonActive);
        buf.writeBoolean(uLBagButtonActive);
    }

	@Override
	public IMessage onMessage(UCTileVendorMessage message, MessageContext ctx) {
		TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld
				.getTileEntity(message.x, message.y, message.z);

		if (tileEntity instanceof TileVendor) {
			((TileVendor) tileEntity).blockOwner = message.blockOwner;
			((TileVendor) tileEntity).coinSum = message.coinSum;
			((TileVendor) tileEntity).userCoinSum = message.userCoinSum;
			((TileVendor) tileEntity).itemPrice = message.itemPrice;
			((TileVendor) tileEntity).infiniteSell = message.infiniteSell;
			((TileVendor) tileEntity).sellMode = message.sellMode;
			((TileVendor) tileEntity).buyButtonActive = message.buyButtonActive;
			((TileVendor) tileEntity).coinButtonActive = message.coinButtonActive;
			((TileVendor) tileEntity).isSStackButtonActive = message.isSStackButtonActive;
			((TileVendor) tileEntity).isLStackButtonActive = message.isLStackButtonActive;
			((TileVendor) tileEntity).isSBagButtonActive = message.isSBagButtonActive;
			((TileVendor) tileEntity).isLBagButtonActive = message.isLBagButtonActive;
			((TileVendor) tileEntity).uCoinButtonActive = message.uCoinButtonActive;
			((TileVendor) tileEntity).uSStackButtonActive = message.uSStackButtonActive;
			((TileVendor) tileEntity).uLStackButtonActive = message.uLStackButtonActive;
			((TileVendor) tileEntity).uSBagButtonActive = message.uSBagButtonActive;
			((TileVendor) tileEntity).uLBagButtonActive = message.uLBagButtonActive;
		}
		return null;
	}
}