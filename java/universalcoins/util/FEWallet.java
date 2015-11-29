package universalcoins.util;

import java.text.DecimalFormat;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.economy.Wallet;

import net.minecraft.entity.player.EntityPlayerMP;

public class FEWallet implements Economy, Wallet {
	private String accountNumber;
	
	public FEWallet(String accountNumber) {
        this.accountNumber = accountNumber;
    }
	
	public FEWallet() {
		
	}

	@Override
	public void add(long arg0) {
		UniversalAccounts.getInstance().creditAccount(accountNumber, (int) arg0);
	}

	@Override
	public void add(double arg0) {
		UniversalAccounts.getInstance().creditAccount(accountNumber, (int) arg0);
	}

	@Override
	public boolean covers(long arg0) {
		if (UniversalAccounts.getInstance().getAccountBalance(accountNumber) > arg0) {
			return true;
		}
		return false;
	}

	@Override
	public long get() {
		return UniversalAccounts.getInstance().getAccountBalance(accountNumber);
	}

	@Override
	public void set(long arg0) {
		//TODO implement?
	}

	@Override
	public boolean withdraw(long arg0) {
		return UniversalAccounts.getInstance().debitAccount(accountNumber, (int) arg0);
	}

	@Override
	public String currency(long arg0) {
		return toString(arg0);
	}

	@Override
	public Wallet getWallet(UserIdent arg0) {
		String playerUID = arg0.toString().replace("(", "").substring(0, arg0.toString().indexOf("|") - 1);
		accountNumber = UniversalAccounts.getInstance().getOrCreatePlayerAccount(playerUID);
		return this;
	}

	@Override
	public Wallet getWallet(EntityPlayerMP arg0) {
		accountNumber = UniversalAccounts.getInstance().getOrCreatePlayerAccount(arg0.getUniqueID().toString());
		return this;
	}

	@Override
	public String toString(long arg0) {
		DecimalFormat formatter = new DecimalFormat("#,###,###,###");
		return formatter.format(arg0);
	}
	
	@Override
    public String toString() {
        return toString(UniversalAccounts.getInstance().getAccountBalance(accountNumber));
    }
}
