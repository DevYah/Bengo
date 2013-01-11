package bengo.data_fetcher;
public class Cache {
	
	// Input 
	int size; // in KB
	int blockSize; // in words
	int associativity;
	int hitPolicy; // 0 for write through, 1 for write back
	int missPolicy; // 0 for write allocate, 1 for write around
	int hitTime;
	
	// Calculated
	int numGroups;
	CacheGroup[] cacheGroups;
	int[] dirtyBits;
	int[] tags;
	
	public Cache(int size,  int blockSize, int hitTime,
				int assoc, int hitPolicy, int missPolicy) {
		this.size 			= size;
		this.blockSize 		= blockSize;
		this.hitTime 		= hitTime;
		this.associativity 	= assoc;
		this.hitPolicy 		= hitPolicy;
		
		numGroups 	= size/(associativity * blockSize * 4);
		cacheGroups 		= new CacheGroup[numGroups];

		for (CacheGroup g : cacheGroups) 
			g = new CacheGroup(associativity, blockSize);
			
		dirtyBits 	= new int[numGroups * associativity];
		tags 		= new int[numGroups * associativity];
	}
	
	/*
	 * this will return a number that has the binary value of n
	 * consecutive ones. Example, n = 3 will return 0b111 = 7 
	 * 
	 */
	private static int makeNOnes(int n) {
		int res = 0;
		for (int i = 0; i < n; i++) {
			res += (1 << i);
		}
		return res;
	}
	
	private static int log2(int n) {
		return (int) (Math.log10(n)/Math.log10(2));
	}
	
	// returns array = {tag, index, offset}
	private int[] map(int address) {
		// TESTED AND  WORKING (all sheet examples were tests)
		
		int offset = address & makeNOnes(log2(blockSize));
		offset <<= 2;
		int index 	= address &
				(makeNOnes(log2(numGroups/associativity)) << log2(blockSize));
		index = index >> log2(blockSize);
		
		int tag = address >> ((log2(blockSize) + log2(numGroups/associativity)));
		
//		System.out.println(tag + " " + index + " " + offset);
		return new int[] {tag, index, offset};
	}
	
	
	public Integer read(int address) {
	    // TIO = {Tag, Index, Offset}
	    int[] TIO = map(address);
	    return null;
	    
	}
	
	public void write(int address, int value) {

		int[] TIO = map(address);
		if (read(address) != null) { // hit
			if (hitPolicy == 0) { // write through
				writeThrough(address, value);
			} else { // write-back
				writeBack(address, value);
			}
		} else { // miss
			// TODO miss policy
			if (missPolicy == 0) { // write allocate

			} else { // write around

			}

		}

	}
	
	private void writeBack(int address, int value) {
		// TODO Auto-generated method stub
		
	}

	private void writeThrough(int address, int value) {
	// On data-write hit, could just update the block in cache
	// But then cache and memory would be inconsistent
	// Write through: also update memory
	// Solution: write buffer. Lecture 4, slide 8
	// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
	}
	
}
