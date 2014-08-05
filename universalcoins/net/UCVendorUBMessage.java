package universalcoins.net;

import universalcoins.TradeStationGUI;
import universalcoins.gui.VendorSaleGUI;
import universalcoins.tile.TileTradeStation;
import universalcoins.tile.TileVendor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCVendorUBMessage implements IMessage, IMessageHandler<UCVendorUBMessage, IMessage> {
	private int x, y, z, buttonId;
	private boolean shiftPressed;

    public UCVendorUBMessage() {}

    public UCVendorUBMessage(int x, int y, int z, int button, boolean shift) { 
    	this.x = x;
    	this.y = y;
    	this.z = z;
        this.buttonId = button;
        this.shiftPressed = shift;
    }

    @Override
    public void toBytes(ByteBuf buf) { 
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(buttonId);
        buf.writeBoolean(shiftPressed);
    }

    @Override
    public void fromBytes(ByteBuf buf) { 
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.buttonId = buf.readInt();
        this.shiftPressed = buf.readBoolean();
	}

	@Override
	public IMessage onMessage(UCVendorUBMessage message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;

		TileEntity tileEntity = world.getTileEntity(message.x, message.y,
				message.z);
		if (tileEntity instanceof TileVendor) {
			if (message.buttonId == VendorSaleGUI.idBuyButton) {
				((TileVendor) tileEntity).onBuyPressed();
			} else if (message.buttonId <= TradeStationGUI.idLBagButton) {
				((TileVendor) tileEntity).onUserRetrieveButtonsPressed(
						message.buttonId, message.shiftPressed);
			}
		}
		return null; // no response in this case
	}
}
