package bengo.data_fetcher;

import bengo.Bengo;

class CacheBlock {

	int blockSize; // in words
	int tag;
	int[] data;
	int lastUpdated;
	boolean empty;
	
	public CacheBlock(int blockSize) {
		tag  = -1;
		data = new int[blockSize];
		this.blockSize = blockSize;
		empty = true;
	}
	
	public boolean isEmpty()
	{
		return this.empty;
	}
	
	public void write(int[] newData, int tag){
		try {
	//		this.data = makeCompatible(newData);
			if (newData.length != this.blockSize)
				throw new Exception("Incompatible blockSize");
			else
				this.data = newData;
			
			this.tag = tag;
			lastUpdated = Bengo.CURRENT_CYCLE;
			empty = false;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
	}

	public String toString() {
		String s = "<";
		s += "tag:" +  tag + "  " ; 
		for (int d : data)
			s += d + ",";
			
		s += ">";
		return s;
			
	}
}