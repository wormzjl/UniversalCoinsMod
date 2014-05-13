package universalcoins.net;

import cpw.mods.fml.common.FMLLog;
import universalcoins.UCTileEntity;
import universalcoins.UCTradeStationGUI;
import universalcoins.UniversalCoins;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PacketTradingStation extends AbstractPacket {
	
	private int x, y, z, button;
    private boolean bypass;
	
public PacketTradingStation() {
    	
    }

public PacketTradingStation(int x, int y, int z, int button, boolean bypass) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.button = button;
    this.bypass = bypass;
    }

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(button);
        buffer.writeBoolean(bypass);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        button = buffer.readInt();
        bypass = buffer.readBoolean();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
	
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		World world = player.worldObj;
		
		TileEntity ucTileEntity = world.getTileEntity(x, y, z);
        if (ucTileEntity instanceof UCTileEntity) {
        	if (button == UCTradeStationGUI.idBypassButton){
				((UCTileEntity) ucTileEntity).setBypass(bypass);
			}
			if (button == UCTradeStationGUI.idBuyButton) {
				((UCTileEntity) ucTileEntity).onBuyPressed();
			}
			else if (button == UCTradeStationGUI.idSellButton) {
				((UCTileEntity) ucTileEntity).onSellPressed();
			}
			else if (button == UCTradeStationGUI.idSellMaxButton){
				((UCTileEntity) ucTileEntity).onSellMaxPressed();
			}
			else if (button == UCTradeStationGUI.idBuyMaxButton){
				((UCTileEntity) ucTileEntity).onBuyMaxPressed();
			}
			else if (button <= UCTradeStationGUI.idHeapButton){
				((UCTileEntity) ucTileEntity).onRetrieveButtonsPressed(button);
			}
			
			NBTTagCompound data = new NBTTagCompound();
            ucTileEntity.writeToNBT(data);
            }
	}
}
