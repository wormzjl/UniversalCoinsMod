package universalcoins.util;

import java.text.DecimalFormat;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.economy.Economy;
import com.forgeessentials.api.economy.Wallet;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StatCollector;

public class FEWallet implements Wallet {
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
    public String toString() {
        return APIRegistry.economy.toString(UniversalAccounts.getInstance().getAccountBalance(accountNumber));
    }
}
