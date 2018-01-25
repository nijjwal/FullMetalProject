//This class locks the coins to the new address.

public class TxOut {
	public String address;
	public double amount;

	TxOut(String address, double amount) {
		this.address = address;
		this.amount = amount;
	}
}
