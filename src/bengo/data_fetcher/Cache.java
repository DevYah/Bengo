package bengo.data_fetcher;
public class Cache {
	
	int size; // in KB
	int wordsPerLine; // in words
	int associativity;
	int hitPolicy; // 0 for write through, 1 for write back
	int missPolicy; // 0 for write allocate, 1 for write around
	int hitTime;
	int penalty;
	
	int numLines;
	int[][] map;
	int[] dirtyBits;
	int[] tags;
	
	public Cache(int size, int wordsPerLine, int hitTime,
			     int penalty, int assoc, int hitPolicy) {
		this.size 			= size;
		this.hitTime 		= hitTime;
		this.associativity 	= assoc;
		this.penalty 		= penalty;
		this.hitPolicy 		= hitPolicy;
		this.wordsPerLine 	= wordsPerLine;
		
		numLines 	= size/(wordsPerLine * 4);
		map 		= new int[numLines][wordsPerLine];
		dirtyBits 	= new int[numLines];
		tags 		= new int[numLines];
	}
	
	/*
	 * this will return a number that has the binary value of n
	 * consecutive ones. Example, n = 3 will return 0b111 = 7 
	 * 
	 */
	public static int makeNOnes(int n) {
		int res = 0;
		for (int i = 0; i < n; i++) {
			res += (1 << i);
		}
		return res;
	}
	
	public static int log2(int n) {
		return (int) (Math.log10(n)/Math.log10(2));
	}
	
	// returns array = {tag, index, offset}
	public int[] map(int address) {
		// TESTED AND  WORKING (all sheet examples were tests)
		
		int offset = address & makeNOnes(log2(wordsPerLine));
		offset <<= 2;
		int index 	= address &
				(makeNOnes(log2(numLines/associativity)) << log2(wordsPerLine));
		index = index >> log2(wordsPerLine);
		
		int tag = address >> ((log2(wordsPerLine) + log2(numLines/associativity)));
		
//		System.out.println(tag + " " + index + " " + offset);
		return new int[] {tag, index, offset};
	}
	
	public Integer read(Integer address) {
	    // TIO = {Tag, Index, Offset}
	    int[] TIO = map(address);
	    // handle hit, miss
	    if(tags[TIO[1]] == TIO[0]) { // hit
      	  // FIXME, apply access time
	      return map[TIO[1]][TIO[2] >> 2]; // shift offset by 2 because it is
				                           // word-addressable
	    }else { // miss
	    	// should be handled in the DataFetcher
	      return null;
	    }
	}
	
	public void write(Integer address, Integer value) {
		// On data-write hit, could just update the block in cache
		// But then cache and memory would be inconsistent
		// Write through: also update memory
		// Solution: write buffer Lecture 4, slide 8

		int[] TIO = map(address);
		if (tags[TIO[1]] == TIO[0]) { // hit
			if (hitPolicy == 0) { // write through
				// TODO Write buffer
				map[TIO[1]][TIO[2]] = value; // FIXME, apply access time

			} else { // write-back

			}
		} else { // miss
			// TODO miss policy
			if (missPolicy == 0) { // write allocate

			} else { // write around

			}

		}

	}
	
	public static void main(String[] args) {
	}
	
}
