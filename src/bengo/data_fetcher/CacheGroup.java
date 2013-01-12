package bengo.data_fetcher;

class CacheGroup {

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
	
	public void Write(int[] TIO, int value, boolean hit, int policy) {
		// search for an empty slot. use a replacement algorithm otherwise
		for (int i = 0; i < blocks.length; i++)
		{
			if(blocks[i].isEmpty())
			{
				// assign cache block to data
			}
		}
		
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