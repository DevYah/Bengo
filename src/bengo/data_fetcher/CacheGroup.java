package bengo.data_fetcher;

class CacheGroup {

	private CacheBlock[] blocks;
		
	public CacheGroup(int assoc, int blockSize) {
		blocks = new CacheBlock[assoc];
		
		for(int i = 0; i < blocks.length; i++) {
			 blocks[i]= new CacheBlock(blockSize);
		}
	}
	
	public int[] readBlock(int address, int tag) {
		for (CacheBlock b : blocks){
			if (b.tag == tag){ // found
				return b.data;
			}
		}
		return null;
	}
	
	public void write(int[] TIO, int[] block) {
		// search for the same tag
		for (int i = 0; i < blocks.length; i++)
		{
			if (blocks[i].tag == TIO[0]) {
//				blocks[i].write(block, TIO[0]);
				return;
			}
		}
		
		// search for an empty slot
		for (int i = 0; i < blocks.length; i++)
		{
			if(blocks[i].isEmpty())
			{
				// assign cache block to data
				blocks[i].write(block, TIO[0]);
				return;
			}
		}
		
		// replace Randomly
		int rand = (int) Math.random() * blocks.length;
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