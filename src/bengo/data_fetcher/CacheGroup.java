package bengo.data_fetcher;

class CacheGroup {

	private CacheBlock[] blocks;
		
	public CacheGroup(int assoc, int blockSize) {
		blocks = new CacheBlock[assoc];
		
		for(int i = 0; i < blocks.length; i++) {
			 blocks[i]= new CacheBlock(blockSize);
		}
	}
	
	public short[] readBlock(int address, int tag) {
		for (CacheBlock b : blocks){
			if (b.tag == tag){ // found
				return b.getData();
			}
		}
		return null;
	}
	
	public void write(int[] TIO, short[] block, boolean dirtyWrite) {
		// search for the same tag
		for (int i = 0; i < blocks.length; i++)
		{
			if (blocks[i].tag == TIO[0]) {
				blocks[i].write(block, TIO[0], dirtyWrite);
				return;
			}
		}
		
		// search for an empty slot
		for (int i = 0; i < blocks.length; i++)
		{
			if(blocks[i].isEmpty())
			{
				// assign cache block to data
				blocks[i].write(block, TIO[0], dirtyWrite);
				return;
			}
		}
		
//		int rand = (int) Math.random() * blocks.length;
//		blocks[rand].write(block, TIO[0]);
		
		// replace LRU
		getLRUBlock().write(block, TIO[0], dirtyWrite);
		return;
		
	}
	public String toString() {
		String s = "";
		for (CacheBlock b : blocks) {
			s += b.toString();
			s += ",";
		}
		return s;	
	}

	public CacheBlock getLRUBlock() {
		// TODO Auto-generated method stub
		int LRUIndex = 0;
		for (int i = 1; i < blocks.length; i++) {
			if (blocks[i].lastUsed < blocks[LRUIndex].lastUsed)
				LRUIndex = i;
		}
		return blocks[LRUIndex];
	}
}