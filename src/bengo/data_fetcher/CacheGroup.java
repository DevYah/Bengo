package bengo.data_fetcher;

public class CacheGroup {

	private CacheBlock[] blocks;
	
	public CacheGroup(int assoc, int blockSize) {
		blocks = new CacheBlock[assoc];
		
		for(CacheBlock b : blocks)
			b = new CacheBlock(blockSize);
	}
	
	public Integer read(int address) {
		// TODO loop on blocks, return null if not found
		return null;
	}
}