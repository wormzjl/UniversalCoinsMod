package universalcoins.util;

import java.text.DecimalFormat;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.economy.Wallet;
import com.forgeessentials.data.v2.DataManager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;

public class FEEconomy implements Economy {

	@Override
	public String currency(long arg0) {
		return toString(arg0);
	}

	@Override
	public Wallet getWallet(UserIdent ident) {
		String accountNumber = UniversalAccounts.getInstance().getOrCreatePlayerAccount(ident.getUuid().toString());
		FEWallet wallet = DataManager.getInstance().load(FEWallet.class, accountNumber);
		if (wallet == null)
            wallet = new FEWallet(accountNumber);
		return wallet;
	}

	@Override
	public Wallet getWallet(EntityPlayerMP player) {
		String accountNumber = UniversalAccounts.getInstance().getOrCreatePlayerAccount(player.getUniqueID().toString());
		FEWallet wallet = DataManager.getInstance().load(FEWallet.class, accountNumber);
		if (wallet == null)
            wallet = new FEWallet(accountNumber);
		return wallet;
	}

	@Override
	public String toString(long arg0) {
		DecimalFormat formatter = new DecimalFormat("#,###,###,###");
		return formatter.format(arg0) + " " + StatCollector.translateToLocal("item.itemCoin.name");
	}
}
