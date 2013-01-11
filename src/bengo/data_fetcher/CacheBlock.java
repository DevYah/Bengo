package bengo.data_fetcher;

public class CacheBlock {

	int blockSize; // in words
	int tag;
	int[] data;
	boolean empty;
	
	public CacheBlock(int blockSize) {
		if (empty)
			this.data = new int[blockSize];
		
	}
}