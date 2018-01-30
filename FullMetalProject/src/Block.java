import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Block {
	private int index;
	private String previousHash;
	private Timestamp timestamp;
	private String data;
	private String hash;
	private int difficulty;
	private int nonce;

	// CONSTANTS
	private final int BLOCK_GENERATION_INTERVAL = 10; // In seconds
	private final int DIFFICULTY_ADJUSTMENT_INTERVAL = 10; // In blocks

	Block(int index, String hash, String previousHash, Timestamp timestamp, String data, int difficulty, int nonce) {
		this.index = index;
		this.previousHash = previousHash;
		this.timestamp = timestamp;
		this.data = data;
		this.hash = hash;
		this.difficulty = difficulty;
		this.nonce = nonce;
	}

	public static void main(String[] args) {
		Date date = new Date();
		Timestamp currentTimestamp = new Timestamp(date.getTime());

		Block genesisBlock = new Block(0, NSConstants.EMPTY_STRING, NSConstants.EMPTY_STRING, currentTimestamp,
				"This is my first genesis block!", 3, 1);

	}

	private String hexToBinary(String hash) {
		return "";
	}

	private boolean hashMatchesDifficulty(String hash, int difficulty) {
		String hashInBinary = hexToBinary(hash);
		String requiredPrefix = repeat(difficulty);
		return hashInBinary.startsWith(requiredPrefix);
	}

	private String repeat(int difficulty) {
		String result = "";
		for (int i = 0; i < difficulty; i++) {
			result = result + "0";
		}
		return result;
	}

	// Mining a block without reward
	private Block findBlock(int number, String previousHash, Timestamp timestamp, String data)
			throws NoSuchAlgorithmException {
		Block newBlock = null;
		int nonce = 0;
		boolean blockConstruced = false;

		while (!blockConstruced) {
			String hash = calculateHash(index, previousHash, timestamp, data, difficulty, nonce);

			if (hashMatchesDifficulty(hash, difficulty)) {
				return new Block(index, hash, previousHash, timestamp, data, difficulty, nonce);
			}

			nonce++;
		}

		return newBlock;
	}

	private String calculateHash(int index, String previousHash, Timestamp timestamp, String data, int difficulty,
			int nonce) throws NoSuchAlgorithmException {

		String text = index + previousHash + timestamp + data + difficulty + nonce;

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
		return CommonMethods.bytesToHex(hash).toString();

	}

	private int getDifficulty(Block[] aBlockChain) {
		Block latestBlock = aBlockChain[aBlockChain.length - 1];

		// Since DIFFICULTY_ADJUSTMENT_INTERVAL is set to 10, that means for
		// every 10th block
		// the following calculation is valid. Also, the code is making sure
		// that the genesis block
		// is not considered.
		if (latestBlock.index % DIFFICULTY_ADJUSTMENT_INTERVAL == 0 && latestBlock.index != 0) {
			return getAdjustedDifficulty(latestBlock, aBlockChain);
		} else {
			return latestBlock.difficulty;
		}
	}

	private int getAdjustedDifficulty(Block latestBlock, Block[] aBlockchain) {
		Block prevAdjustmentBlock = aBlockchain[aBlockchain.length - DIFFICULTY_ADJUSTMENT_INTERVAL];
		int timeExpected = BLOCK_GENERATION_INTERVAL * DIFFICULTY_ADJUSTMENT_INTERVAL;
		long timeExpectedMilliseconds = timeExpected * 1000;

		// Calculate time taken to generate the end blocks
		// Sample: Time taken to generate the 20th and 30th block
		// 1 to 10
		// 11 to 20
		// 21 to 30
		long timeTakenmilliseconds = latestBlock.timestamp.getTime() - prevAdjustmentBlock.timestamp.getTime();

		// If it took more time, then decrease the difficulty by 1.
		if (timeTakenmilliseconds > timeExpectedMilliseconds * 2) {
			return prevAdjustmentBlock.difficulty - 1;
		} else if (timeTakenmilliseconds < timeExpectedMilliseconds / 2) {
			return prevAdjustmentBlock.difficulty + 1;
		} else {
			return prevAdjustmentBlock.difficulty;
		}
	}

	// To mitigate the attack where false timestamp is introduced.
	boolean isValidTimestamp(Block newBlock, Block previousBlock) {
		// Time when previous block was generated
		long previousBlockTimeInMilliSeconds = previousBlock.timestamp.getTime();
		long previousBlockTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(previousBlockTimeInMilliSeconds);

		// Time when new block is generated
		long newBlockTimeInMilliSeconds = newBlock.timestamp.getTime();
		long newBlockTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(newBlockTimeInMilliSeconds);

		Date date = new Date();
		Timestamp currentTimestamp = new Timestamp(date.getTime());
		long currentTimeInSeconds = TimeUnit.MILLISECONDS.toSeconds(currentTimestamp.getTime());

		if (newBlockTimeInSeconds - previousBlockTimeInSeconds > 60) {
			if (currentTimeInSeconds - newBlockTimeInSeconds > 60) {
				return true;
			}

		}
		return false;
	}

}
