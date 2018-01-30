public class Transaction {
	public String id;
	public TxIn[] txIns;
	public TxOut[] txOuts;

	/**
	 * ShresthaN - This method returns transaction id.
	 * 
	 * @param transaction
	 * @return
	 */
	private static String getTransactionId(Transaction transaction) {

		TxIn[] txIn = transaction.txIns;
		String txInContent = NSConstants.EMPTY_STRING;

		TxOut[] txOut = transaction.txOuts;
		String txOutContent = NSConstants.EMPTY_STRING;

		if (txIn != null && txIn.length > 0) {
			for (int i = 0; i < txIn.length; i++) {
				String inputValue = txIn[i].txOutId + txIn[i].txOutIndex;
				if (inputValue != null && inputValue.trim().length() > 0) {
					txInContent = txInContent + inputValue;
				}
			}
		}

		if (txOut != null && txOut.length > 0) {
			for (int i = 0; i < txOut.length; i++) {
				String outputValue = txOut[i].address + txOut[i].amount;
				if (outputValue != null && outputValue.trim().length() > 0) {
					txOutContent = txOutContent + outputValue;
				}
			}
		}

		return CommonMethods.SHA256ToString(txInContent + txOutContent);
	}
}
