package bt;

/**
 * A burstcoin asset.
 * 
 * This class should not be directly instantiated by users.
 * Use {@link Contract#getAsset(String)} to get an asset from
 * a string.
 * 
 * @author lao
 */
public class Asset {

	long id;
    String name;
    String description;
    long quantity;
    long decimals;
    long balance;

    /**
	 * Should be called by the emulator only.
	 * 
	 * @param asset
	 * @param balance
	 */
	@EmulatorWarning
    public Asset(Asset asset, long balance) {
        if(asset != null ) {
            this.id = asset.id;
            this.name = asset.name;
            this.description = asset.description;
            this.quantity = asset.quantity;
            this.decimals = asset.decimals;
        }
        else {
            this.id = 0L;
            this.name = "";
            this.description = "";
            this.quantity = 0;
            this.decimals = 0;
        }

        this.balance = balance;
	}
	
	/**
	 * Should be called by the emulator only.
	 * 
	 * @param id
     * @param name
     * @param description
     * @param quantity
     * @param decimals
	 * @param balance
	 */
	@EmulatorWarning
    public Asset(long id, String name, String description, long quantity, long decimals) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.decimals = decimals;
        this.balance = 0;
	}

	
	/**
	 * @return the id
	 */
	@EmulatorWarning
	public long getId() {
		return id;
	}

    /**
	 * @return the name
	 */
	@EmulatorWarning
	public String getName() {
		return name;
	}

    /**
	 * @return the description
	 */
	@EmulatorWarning
	public String getDesc() {
		return description;
	}

    /**
	 * @return the quantity
	 */
	@EmulatorWarning
	public long getQuantity() {
		return quantity;
	}

    /**
	 * @return the decimals
	 */
	@EmulatorWarning
	public long getDecimals() {
		return decimals;
	}

    /**
	 * @return the balance
	 */
	@EmulatorWarning
	public long getBalance() {
		return balance;
	}
}
