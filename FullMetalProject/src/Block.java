import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Block {
	private int index;
	private int difficulty;
	private Timestamp timestamp;

	// CONSTANTS
	private final int BLOCK_GENERATION_INTERVAL = 10; // In seconds
	private final int DIFFICULTY_ADJUSTMENT_INTERVAL = 10; // In blocks

	Block(int index, Timestamp timestamp) {
		this.index = index;
		this.timestamp = timestamp;
	}

	public static void main(String[] args) {

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

		return (previousBlockTimeInSeconds < newBlockTimeInSeconds)
				&& newBlockTimeInSeconds - 60 < currentTimeInSeconds;
	}

}
