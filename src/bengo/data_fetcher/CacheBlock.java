package bengo.data_fetcher;

public class CacheBlock {

	int blockSize; // in words
	int tag;
	int[] data;
	boolean empty;
	
	public CacheBlock(int blockSize) {
		tag  = -1;
		data = new int[blockSize];
		empty = true;
	}
	public boolean isEmpty()
	{
		return this.empty;
	}
	
	public String toString() {
		String s = "<";
		s += tag; 
		for (int d : data)
			s += d + ",";
			
		s += ">";
		return s;
			
	}
}