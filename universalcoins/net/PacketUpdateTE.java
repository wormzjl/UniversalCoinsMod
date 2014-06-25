package universalcoins.net;

import cpw.mods.fml.common.FMLLog;
import universalcoins.UCTileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PacketUpdateTE extends AbstractPacket{
	
	private int x, y, z, itemprice, coinsum, automode, coinmode;
	
public PacketUpdateTE() {
    	
    }

public PacketUpdateTE(int x, int y, int z, int itemprice, int coinsum, int automode, int coinmode) {
	this.x = x;
    this.y = y;
    this.z = z;
    this.itemprice = itemprice;
    this.coinsum = coinsum;
    this.automode = automode;
    this.coinmode = coinmode;
}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(itemprice);
        buffer.writeInt(coinsum);
        buffer.writeInt(automode);
        buffer.writeInt(coinmode);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        itemprice = buffer.readInt();
        coinsum = buffer.readInt();
        automode = buffer.readInt();
        coinmode = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		World world = player.worldObj;
		//FMLLog.info("UC: Client received PacketCoinSum");
		TileEntity ucTileEntity = world.getTileEntity(x, y, z);
        if (ucTileEntity instanceof UCTileEntity) {
        	((UCTileEntity) ucTileEntity).itemPrice = itemprice;
        	((UCTileEntity) ucTileEntity).coinSum = coinsum;
        	((UCTileEntity) ucTileEntity).autoMode = automode;
        	((UCTileEntity) ucTileEntity).coinMode = coinmode;
        }
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		World world = player.worldObj;
		
		TileEntity ucTileEntity = world.getTileEntity(x, y, z);
        if (ucTileEntity instanceof UCTileEntity) {
        	itemprice = ((UCTileEntity) ucTileEntity).itemPrice;
        	coinsum = ((UCTileEntity) ucTileEntity).coinSum;
        	automode = ((UCTileEntity) ucTileEntity).autoMode;
        	coinmode = ((UCTileEntity) ucTileEntity).coinMode;
        }
	}

}
