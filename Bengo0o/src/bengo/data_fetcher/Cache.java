package bengo.data_fetcher;

class Cache {

	static int WRITE_THROUGH = 0;
	static int WRITE_BACK = 1;
	static int WRITE_ALLOCATE = 0;
	static int WRITE_AROUND = 1;
	// Input
	int numWords;
	int blockSize; // in words
	int associativity;
	int hitPolicy; // 0 for write through, 1 for write back
	int missPolicy; // 0 for write allocate, 1 for write around
	int hitTime;
	String name;
	
	double hits;
	double misses;
	double accesses;

	// Calculated
	int numGroups;
	CacheGroup[] cacheGroups;

	static int count = 0;
	public Cache(int numWords,  int blockSize, int hitTime,
				int assoc, int hitPolicy, int missPolicy) {
		this.numWords 		= numWords;
		this.blockSize 		= blockSize;
		this.hitTime 		= hitTime;
		this.associativity 	= assoc;
		this.hitPolicy 		= hitPolicy;

		numGroups 	= numWords/(associativity * blockSize);
		cacheGroups = new CacheGroup[numGroups];

		for (int i = 0; i < cacheGroups.length; i++)
			cacheGroups[i] = new CacheGroup(associativity, blockSize);

		
		this.name = "L" + ++count;
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
		return (int) Math.ceil(Math.log10(n)/Math.log10(2));
	}

	// returns array = {tag, index, offset}
	public int[] map(int address) {
		// TESTED AND  WORKING (all sheet examples were tests)

		int offset = address & makeNOnes(log2(blockSize));
		offset <<= 2;
		int index 	= address & ((makeNOnes(log2(numGroups))) << log2(blockSize));
		index = index >> log2(blockSize);

		int tag = address >> ((log2(blockSize) + log2(numGroups)));

//		System.out.println(tag + " " + index + " " + offset);
		return new int[] {tag, index, offset};
	}


	public short[] read(int address) {
	    // TIO = {Tag, Index, Offset}
	    int[] TIO = map(address);

    	short[] resBlock = cacheGroups[TIO[1]].readBlock(address, TIO[0]);
    	
		return resBlock;
	}

	public void write(int address, short[] value, boolean dirtyWrite) {
		int[] TIO = map(address);

		cacheGroups[TIO[1]].write(TIO, value, dirtyWrite);
	}
	
	public boolean isHit(int address) {
		accesses++;
		if (read(address) != null) { // hit
			hits++;
			return true;
		}
		else {
			misses++;
			return false;
		}
	}

	// for reading
	public short[] compatibleBlock(int address, Memory mem) {
		int wordOffset = map(address)[2] >> 2;
		short[] block = new short[blockSize];
		int baseAddress = address - wordOffset;
		for (int k = 0; k < blockSize; k++) {
			block[k] = mem.read(baseAddress + k);
		}
		return block;
	}

	// for writing
	public short[] compatibleBlock(int address, short wordToWrite, Memory mem) {
		short[] blockToWrite = compatibleBlock(address, mem);
		int offset = map(address)[2] >> 2;
		blockToWrite[offset] = wordToWrite;
		return blockToWrite;
	}

	public String toString() {
		String s = "Cache " + name + ":\n";
		for (int i = 0; i < cacheGroups.length; i++) {
			s += "index " + i + ":";
			s += cacheGroups[i];
			s += "\n";
		}
		return s;
	}
	
	public double getMissRatio() {
		return misses*1.0/accesses;
	}

	public double getHitRatio() {
		return hits*1.0/accesses;
	}
	
	public CacheBlock getBlockToReplace(int address) {
		int[] TIO = map(address);
		return cacheGroups[TIO[1]].getLRUBlock();
	}

}
