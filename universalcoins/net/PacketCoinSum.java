package universalcoins.net;

import cpw.mods.fml.common.FMLLog;
import universalcoins.UCTileEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class PacketCoinSum extends AbstractPacket{
	
	private int x, y, z, coinsum;
	
public PacketCoinSum() {
    	
    }

public PacketCoinSum(int x, int y, int z, int coinsum) {
	this.x = x;
    this.y = y;
    this.z = z;
    this.coinsum = coinsum;
}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(coinsum);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        coinsum = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		World world = player.worldObj;

		TileEntity ucTileEntity = world.getTileEntity(x, y, z);
        if (ucTileEntity instanceof UCTileEntity) {
        	((UCTileEntity) ucTileEntity).coinSum = coinsum;
        }
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		World world = player.worldObj;
		
		TileEntity ucTileEntity = world.getTileEntity(x, y, z);
        if (ucTileEntity instanceof UCTileEntity) {
        	coinsum = ((UCTileEntity) ucTileEntity).coinSum;
        }
	}

}
