package bengo.data_fetcher;

public class CacheGroup {

	private CacheBlock[] blocks;
	
	public CacheGroup(int assoc, int blockSize) {
		blocks = new CacheBlock[assoc];
		
		for(int i = 0; i < blocks.length; i++)
			 blocks[i]= new CacheBlock(blockSize);
	}
	
	public Integer read(int address, int tag, int offset) {
		for (CacheBlock b : blocks){
			if (b.tag == tag){ // found
				return b.data[offset >> 2];
			}
		}
		return null;
	}
	public String toString() {
		String s = "";
		for (CacheBlock b : blocks) {
			s += b.toString();
			s += ",";
		}
		return s;	
	}
}