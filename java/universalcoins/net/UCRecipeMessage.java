package universalcoins.net;

import io.netty.buffer.ByteBuf;
import universalcoins.UniversalCoins;
import universalcoins.util.UCRecipeHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UCRecipeMessage implements IMessage, IMessageHandler<UCRecipeMessage, IMessage> {
private boolean recipesEnabled, vendorRecipesEnabled, vendorFrameRecipesEnabled, atmRecipeEnabled, 
enderCardRecipeEnabled, banditRecipeEnabled, signalRecipeEnabled, linkCardRecipeEnabled;

    public UCRecipeMessage()
    {
        this.recipesEnabled = UniversalCoins.recipesEnabled;
        this.vendorRecipesEnabled = UniversalCoins.vendorRecipesEnabled;
        this.vendorFrameRecipesEnabled = UniversalCoins.vendorFrameRecipesEnabled;
        this.atmRecipeEnabled = UniversalCoins.atmRecipeEnabled;
        this.enderCardRecipeEnabled = UniversalCoins.enderCardRecipeEnabled;
        this.banditRecipeEnabled = UniversalCoins.banditRecipeEnabled;
        this.signalRecipeEnabled = UniversalCoins.signalRecipeEnabled;
        this.linkCardRecipeEnabled = UniversalCoins.linkCardRecipeEnabled;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    	this.recipesEnabled = buf.readBoolean();
        this.vendorRecipesEnabled = buf.readBoolean();
        this.vendorFrameRecipesEnabled = buf.readBoolean();
        this.atmRecipeEnabled = buf.readBoolean();
        this.enderCardRecipeEnabled = buf.readBoolean();
        this.banditRecipeEnabled = buf.readBoolean();
        this.signalRecipeEnabled = buf.readBoolean();
        this.linkCardRecipeEnabled = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(recipesEnabled);
        buf.writeBoolean(vendorRecipesEnabled);
        buf.writeBoolean(vendorFrameRecipesEnabled);
        buf.writeBoolean(atmRecipeEnabled);
        buf.writeBoolean(enderCardRecipeEnabled);
        buf.writeBoolean(banditRecipeEnabled);
        buf.writeBoolean(signalRecipeEnabled);
        buf.writeBoolean(linkCardRecipeEnabled);
    }

	@Override
	public IMessage onMessage(UCRecipeMessage message, MessageContext ctx) {
		UCRecipeHelper.addCoinRecipes();
		if (message.recipesEnabled) {
			UCRecipeHelper.addTradeStationRecipe();
		}
		if (message.vendorRecipesEnabled){
			UCRecipeHelper.addVendingBlockRecipes();
		}
		if (message.vendorFrameRecipesEnabled){
			UCRecipeHelper.addVendingFrameRecipes();
		}
		if (message.atmRecipeEnabled){
			UCRecipeHelper.addCardStationRecipes();
		}
		if (message.enderCardRecipeEnabled){
			UCRecipeHelper.addEnderCardRecipes();
			UCRecipeHelper.addBlockSafeRecipe();
		}
		if (banditRecipeEnabled){
			UCRecipeHelper.addBanditRecipes();
		}
		if (signalRecipeEnabled){
			UCRecipeHelper.addSignalRecipes();
		}
		if (linkCardRecipeEnabled){
			UCRecipeHelper.addLinkCardRecipes();
		}
		UCRecipeHelper.addSignRecipes();
			
		
		return null;
	}
}