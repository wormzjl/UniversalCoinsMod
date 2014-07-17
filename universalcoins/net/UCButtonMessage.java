package universalcoins.net;

import universalcoins.UCTileEntity;
import universalcoins.UCTradeStationGUI;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCButtonMessage implements IMessage, IMessageHandler<UCButtonMessage, IMessage> {
	private int x, y, z, buttonId;
	private boolean shiftPressed;

    public UCButtonMessage() {}

    public UCButtonMessage(int x, int y, int z, int button, boolean shift) { 
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
	public IMessage onMessage(UCButtonMessage message, MessageContext ctx) {
		World world = ctx.getServerHandler().playerEntity.worldObj;

		TileEntity tileEntity = world.getTileEntity(message.x, message.y,
				message.z);
		if (tileEntity instanceof UCTileEntity) {
			if (message.buttonId == UCTradeStationGUI.idBuyButton) {
				if (message.shiftPressed) {
					((UCTileEntity) tileEntity).onBuyMaxPressed();
				} else {
					((UCTileEntity) tileEntity).onBuyPressed();
				}
			} else if (message.buttonId == UCTradeStationGUI.idSellButton) {
				if (message.shiftPressed) {
					((UCTileEntity) tileEntity).onSellMaxPressed();
				} else {
					((UCTileEntity) tileEntity).onSellPressed();
				}
			} else if (message.buttonId == UCTradeStationGUI.idAutoModeButton) {
				((UCTileEntity) tileEntity).onAutoModeButtonPressed();
			} else if (message.buttonId == UCTradeStationGUI.idCoinModeButton) {
				((UCTileEntity) tileEntity).onCoinModeButtonPressed();
			} else if (message.buttonId <= UCTradeStationGUI.idLBagButton) {
				((UCTileEntity) tileEntity).onRetrieveButtonsPressed(
						message.buttonId, message.shiftPressed);
			}

			NBTTagCompound data = new NBTTagCompound();
			tileEntity.writeToNBT(data);
		}
		return null; // no response in this case
	}
}
