public class Cache {
	
	int size;
	int lineSize;
	int associativity;
	int hitPolicy; // 0 for write back, 1 for write through
	int missPolicy; // 0 for write allocate, 1 for write around
	int hitTime;
	int penalty;
	
	int numLines;
	int[] map;
	int[] dirtyBits;
	
	public Cache(int size, int lineSize, int hitTime, int penalty, int assoc, int hitPolicy) {
		this.size 		= size;
		this.hitTime 	= hitTime;
		this.associativity = assoc;
		this.penalty 	= penalty;
		this.hitPolicy 	= hitPolicy;
		
		numLines = size/lineSize;
		map = new int[numLines];
		dirtyBits = new int[numLines];
	}
	
	/*
	 * this will return a number that has the binary value of n
	 * consecutive ones. Example, n = 3 will return 0b111 = 7 
	 * 
	 */
	public static int makeNOnes(int n) {
		int res = 0;
		for (int i = 0; i < n; i++) {
			res += Math.pow(2, i);
		}
		return res;
	}
	
	public static int log2(int n) {
		return (int) (Math.log10(n)/Math.log10(2));
	}
	
	// returns array = {tag, index, offset}
	public int[] map(int address) {
		// TESTED AND  WORKING (all sheet examples were tests)
		
		int offset = address & makeNOnes(log2(lineSize));
		offset <<= 2;
		int index 	= address & (makeNOnes(log2(numLines/associativity)) << log2(lineSize));
		index = index >> log2(lineSize);
		
		int tag = address >> ((log2(lineSize) + log2(numLines/associativity)));
		
//		System.out.println(Integer.toBinaryString(tag) + " " + Integer.toBinaryString(index) + " " + Integer.toBinaryString(offset));
//		System.out.println(tag + " " + index + " " + offset);
		return new int[] {tag, index, offset};
	}
	
	public Integer read(Integer address) {
		int[] tagIndexOffset = map(address);
		// handle hit, miss
		return null;
	}
	
	public void write(Integer address, Integer value) {
	}
	
}