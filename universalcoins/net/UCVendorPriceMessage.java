package universalcoins.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalcoins.TradeStationGUI;
import universalcoins.gui.VendorSaleGUI;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCVendorPriceMessage  implements IMessage, IMessageHandler<UCVendorPriceMessage, IMessage> {
	private int x, y, z, itemPrice;

    public UCVendorPriceMessage() {}

    public UCVendorPriceMessage(int x, int y, int z, int price) { 
    	this.x = x;
    	this.y = y;
    	this.z = z;
        this.itemPrice = price;
    }

    @Override
    public void toBytes(ByteBuf buf) { 
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(itemPrice);
    }

    @Override
    public void fromBytes(ByteBuf buf) { 
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.itemPrice = buf.readInt();
	}

	@Override
	public IMessage onMessage(UCVendorPriceMessage message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;

		TileEntity tileEntity = world.getTileEntity(message.x, message.y, message.z);
		if (tileEntity instanceof TileVendor) {
			((TileVendor) tileEntity).itemPrice = message.itemPrice;
			}

			NBTTagCompound data = new NBTTagCompound();
			tileEntity.writeToNBT(data);
			return null;
	}
}
