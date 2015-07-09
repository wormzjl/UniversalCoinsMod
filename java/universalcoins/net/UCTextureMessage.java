package universalcoins.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalcoins.tile.TileUCSign;
import universalcoins.tile.TileVendorFrame;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCTextureMessage implements IMessage, IMessageHandler<UCTextureMessage, IMessage> {
	private int x, y, z;
	private String blockIcon;

	public UCTextureMessage() {
	}

	public UCTextureMessage(int x, int y, int z, String blockIcon) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockIcon = blockIcon;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, blockIcon);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.blockIcon = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public IMessage onMessage(UCTextureMessage message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;

		TileEntity tileEntity = world.getTileEntity(message.x, message.y, message.z);
		if (tileEntity instanceof TileVendorFrame) {
			((TileVendorFrame) tileEntity).blockIcon = message.blockIcon;
		}
		if (tileEntity instanceof TileUCSign) {
			((TileUCSign) tileEntity).blockIcon = message.blockIcon;
		}
		return null;
	}
}
