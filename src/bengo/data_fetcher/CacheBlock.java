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
	
	public void write(int[] newData, int tag) {
		this.tag = tag;
		this.data = makeCompatible(newData);
		lastUpdated = Bengo.CURRENT_CYCLE;
		empty = false;
		
	}
	
	// in case of data has lower number of words than this cacheBlock size
	private int[] makeCompatible(int[] newData) {
		if (newData.length == blockSize)
			return newData;
		
		int[] comptaible = new int[blockSize];
		// copy data from newData to comptible
		for (int i = 0; i < newData.length; i++)
			comptaible[i] = newData[i];
		
		return comptaible;
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