package universalcoins.net;

import universalcoins.gui.CardStationGUI;
import universalcoins.gui.TradeStationGUI;
import universalcoins.gui.VendorGUI;
import universalcoins.gui.VendorSaleGUI;
import universalcoins.tile.TileCardStation;
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
		if (tileEntity instanceof TileTradeStation) {
			if (message.buttonId == TradeStationGUI.idBuyButton) {
				if (message.shiftPressed) {
					((TileTradeStation) tileEntity).onBuyMaxPressed();
				} else {
					((TileTradeStation) tileEntity).onBuyPressed();
				}
			} else if (message.buttonId == TradeStationGUI.idSellButton) {
				if (message.shiftPressed) {
					((TileTradeStation) tileEntity).onSellMaxPressed();
				} else {
					((TileTradeStation) tileEntity).onSellPressed();
				}
			} else if (message.buttonId == TradeStationGUI.idAutoModeButton) {
				((TileTradeStation) tileEntity).onAutoModeButtonPressed();
			} else if (message.buttonId == TradeStationGUI.idCoinModeButton) {
				((TileTradeStation) tileEntity).onCoinModeButtonPressed();
			} else if (message.buttonId <= TradeStationGUI.idLBagButton) {
				((TileTradeStation) tileEntity).onRetrieveButtonsPressed(
						message.buttonId, message.shiftPressed);
			} else if (message.buttonId == TradeStationGUI.idDepositButton) {
				((TileTradeStation) tileEntity).onDepositButtonPressed(message.shiftPressed);
			} else if (message.buttonId <= TradeStationGUI.idWithdrawButton) {
				((TileTradeStation) tileEntity).onWithdrawButtonPressed(message.shiftPressed);
			}

			NBTTagCompound data = new NBTTagCompound();
			tileEntity.writeToNBT(data);
		}
		if (tileEntity instanceof TileVendor) {
			if (message.buttonId < VendorGUI.idCoinButton) {
				//do nothing here
			} else if (message.buttonId <= VendorGUI.idLBagButton) {
				((TileVendor) tileEntity).onRetrieveButtonsPressed(
						message.buttonId, message.shiftPressed);
			}  else if (message.buttonId == VendorGUI.idDepositButton) {
				((TileVendor) tileEntity).onDepositButtonPressed(message.shiftPressed);
			} else if (message.buttonId <= VendorGUI.idWithdrawButton) {
				((TileVendor) tileEntity).onWithdrawButtonPressed(message.shiftPressed);
			} else if (message.buttonId == VendorSaleGUI.idBuyButton) {
				if (message.shiftPressed) {
					((TileVendor) tileEntity).onBuyMaxPressed();
				} else {
					((TileVendor) tileEntity).onBuyPressed();
				}
			} else if (message.buttonId <= VendorSaleGUI.idLBagButton) {
				((TileVendor) tileEntity).onRetrieveButtonsPressed(
						message.buttonId, message.shiftPressed);
			}else if (message.buttonId == VendorSaleGUI.idPDepositButton) {
				((TileVendor) tileEntity).onPDepositButtonPressed(message.shiftPressed);
			} else if (message.buttonId <= VendorSaleGUI.idPWithdrawButton) {
				((TileVendor) tileEntity).onPWithdrawButtonPressed(message.shiftPressed);
			}
		}
		if (tileEntity instanceof TileCardStation) {
			if (message.buttonId == CardStationGUI.idCardButton) {
				((TileCardStation) tileEntity).onCardButtonPressed();
			} else if (message.buttonId <= CardStationGUI.idLBagButton) {
				((TileCardStation) tileEntity).onRetrieveButtonsPressed(
						message.buttonId, message.shiftPressed);
			}
		}
		return null; // no response in this case
	}
}
