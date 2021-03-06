package bt;

/**
 * A burstcoin address.
 * 
 * This class should not be directly instantiated by users.
 * Use {@link Contract#parseAddress(String)} to get an address from
 * a string. Another alternative to get an address is by
 * {@link Contract#getCurrentTx()} and then
 * {@link Transaction#getSenderAddress()}.
 * 
 * @author jjos
 * 
 * Lao 02/23/2012: Add  asset field
 * 
 * To-do: support mutiple assets. ***Smart contract supports multiple assets, just the Emulator not yet.***
 */
public class Address {

	long id;
	String rsAddress;
	long balance;
	Contract contract;
	Asset asset;
	boolean sleeping;
	
	/**
	 * Should be called by the emulator only.
	 * 
	 * @param id
	 * @param balance
	 * @param rs
	 */
	@EmulatorWarning
	Address(long id, long balance, String rs) {
		this.id = id;
		this.balance = balance;
		this.rsAddress = rs;
		this.asset = null;
	}
	
	/**
	 * @return the reed solomon address
	 */
	@EmulatorWarning
	public String getRsAddress() {
		return rsAddress;
	}

	/**
	 * @return the reed solomon address
	 */
	@EmulatorWarning
	public long getId() {
		return id;
	}

	/**
	 * @return the current balance available
	 */
	@EmulatorWarning
	public long getBalance() {
		return balance;
	}

	/**
	 * @return the underlying contract or null
	 */
	@EmulatorWarning
	public Contract getContract() {
		return contract;
	}

	/**
	 * @return the underlying asset or null
	 */
	@EmulatorWarning
	public Asset getAsset() {
		return asset;
	}
	
	/**
	 * @return true if it is a sleeping contract
	 */
	@EmulatorWarning
	public boolean isSleeping() {
		return sleeping;
	}
	
	@EmulatorWarning
	public void setSleeping(boolean sleeping) {
		this.sleeping = sleeping;
	}

	@Override
	@EmulatorWarning
	public String toString() {
		return rsAddress;
	}

	@EmulatorWarning
	public String getFieldValues() {
		String ret = "<html>";
	
		if(asset != null && asset.id > 0L ){
			ret += "<br>Asset<br><b>   Name</b> = " + asset.name + "<br>";
			ret += "   Balance</b> = " + asset.balance / Math.pow(10, asset.decimals) + "<br>";
		}
		else
			ret += "No Asset Yet<br>";

		return ret;
	}
}
