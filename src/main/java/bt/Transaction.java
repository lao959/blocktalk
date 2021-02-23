package bt;

/**
 * Class representing a transaction.
 * 
 * This class should only be used by the emulated block-chain.
 * 
 * @author jjos
 *
 *  Lao 02/23/2012: Add tow transcation type to support asset support
 *                       TYPE_SEND_ASSET
 *                       TYPE_ISSUE_ASSET
 */
public class Transaction {

	public static final byte TYPE_PAYMENT = 0;
	public static final byte TYPE_MESSAGING = 1;
	public static final byte TYPE_AT_CREATE = 2;
	public static final byte TYPE_METHOD_CALL = 3;
	public static final byte TYPE_SEND_ASSET = 4;
	public static final byte TYPE_ISSUE_ASSET = 5;

	Block block;
	Address sender;
	Address receiver;
	long amount;
	byte type;
	Timestamp ts;
	String msgString;
	Register msg;
	long assetId;
	Asset asset;
	long assetAmount;

	/**
	 * Users are not allowed to create new instances of this class, this function
	 * should be called by the emulator only.
	 * 
	 * @param sender
	 * @param receiver
	 * @param ammount
	 * @param type
	 * @param ts
	 * @param msg
	 * @return
	 */
	Transaction(Address sender, Address receiver, long ammount, byte type, Timestamp ts, String msg) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = ammount;
		this.type = type;
		this.ts = ts;
		msgString = msg;
		if (msg == null) {
			this.msgString = "";
			return;
		}
		this.msg = Register.newMessage(this.msgString);
	}

	/**
	 * Users are not allowed to create new instances of this class, this function
	 * should be called by the emulator only.
	 * 
	 * @param ad
	 * @param ammount
	 * @param type
	 * @param ts
	 * @param msg
	 * @return
	 */
	Transaction(Address sender, Address receiver, long ammount, byte type, Timestamp ts, Register msg) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = ammount;
		this.type = type;
		this.ts = ts;
		this.msg = msg;
	}

	/**
	 * Users are not allowed to create new instances of this class, this function
	 * should be called by the emulator only.
	 * 
	 * @param sender
	 * @param receiver
	 * @param ammount
	 * @param msg
	 * @param type
	 * @param ts
	 * @param assetId
	 * @param assetAmount
	 * @return
	 */
	public Transaction(Address sender, Address receiver, long amount, String msg, byte type, Timestamp ts, long assetId,
			long assetAmount) {

		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
		msgString = msg;
		if (msg == null) {
			this.msgString = "";
			return;
		}
		this.msg = Register.newMessage(this.msgString);
		this.type = type;
		this.ts = ts;
		this.assetId = assetId;
		this.assetAmount = assetAmount;
	}

	/**
	 * Users are not allowed to create new instances of this class, this function
	 * should be called by the emulator only.
	 * 
	 * @param owner
	 * @param ammount
	 * @param type
	 * @param ts
	 * @param asset
	 */
	public Transaction(Address owner, long amount, byte type, Timestamp ts, Asset asset) {
		this.sender = owner;
		this.receiver = null;
		this.amount = amount;
		this.type = type;
		this.ts = ts;
		this.asset = asset;
		this.assetId = asset.id;
		
	}

	/**
	 * @return the sender address for this transaction
	 */
	public Address getSenderAddress() {
		return sender;
	}

	/**
	 * @return the reciever address for this transaction
	 */
	@EmulatorWarning
	public Address getReceiverAddress() {
		return receiver;
	}

	/**
	 * @return the amount in this transaction minus the activation fee
	 */
	public long getAmount() {
		if (receiver != null && receiver.contract != null)
			return amount - receiver.contract.activationFee;
		return amount;
	}

	/**
	 * Return the message attached to a transaction.
	 * 
	 * Only unencrypted messages are received.
	 * 
	 * @return the message in this transaction
	 */
	public Register getMessage() {
		return msg;
	}

	/**
	 * Return the first 8 bytes of the message attached to a transaction.
	 * 
	 * Only unencrypted messages are received.
	 * 
	 * @return the first 8 bytes in the message
	 */
	public long getMessage1() {
		if(msg == null)
			return 0L;
		return msg.value[0];
	}

	/**
	 * @return the message in this transaction
	 */
	@EmulatorWarning
	public String getMessageString() {
		return msgString;
	}

	/**
	 * @return the pseudo-timestamp of this transaction (block height and txid)
	 */
	public Timestamp getTimestamp() {
		return ts;
	}

	public byte getType() {
		return type;
	}

	/**
	 * @return the asset amount in this transaction
	 */
	public long getAssetAmount() {
		return assetAmount;
	}

	/**
	 * @return the asset Id in this transaction
	 */
	public long getAssetId() {
		return assetId;
	}

	public Block getBlock() {
		return block;
	}
}
