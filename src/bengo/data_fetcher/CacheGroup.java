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
	
	public void write(int[] TIO, int[] value) {
		// search for an empty slot. use a replacement algorithm otherwise
		for (int i = 0; i < blocks.length; i++)
		{
			if(blocks[i].isEmpty())
			{
				// assign cache block to data
				System.out.println("block to write in is " + blocks[i].blockSize);
				blocks[i].write(value, TIO[0]);
				break;
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