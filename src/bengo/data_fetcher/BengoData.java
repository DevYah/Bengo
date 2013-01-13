package bengo.data_fetcher;

import java.util.ArrayList;
import java.util.Arrays;

import bengo.Bengo;

public class BengoData {
	/*
	 * This is a container for the caches and memory
	 */

	int levels;
	private Cache[] caches;
	private Memory mem;

	public BengoData(int levels, int[] sizes, int[]lineSizes,
					int[] hitTimes, int[] assocs, int[] hitPolicies,
					int[] missPolicies) {

		this.levels = levels;
		caches = new Cache[levels];

		// notes that blockSizes must be in an increasing order
		if (levels >= 1)
			caches[0] = new Cache(sizes[0], lineSizes[0], hitTimes[0], assocs[0], hitPolicies[0], missPolicies[0]);
		if (levels >= 2)
			caches[1] = new Cache(sizes[1], lineSizes[1], hitTimes[1], assocs[1], hitPolicies[1], missPolicies[1]);
		if (levels >= 3)
			caches[2] = new Cache(sizes[2], lineSizes[2], hitTimes[2], assocs[2], hitPolicies[2], missPolicies[2]);
	}

	public DataAction read(int address) {
		int neededCycles = 0;
		short[] res;
		int wordOffset;
		int i;
		boolean foundInCache = false;
		int value = -55555555;
		for(i = 0; i < levels; i++) {
			res = caches[i].read(address);
			wordOffset = caches[i].map(address)[2] >> 2;
			neededCycles += caches[i].hitTime; // in case of hit or miss
			if (res != null) { // in case of hit
				foundInCache = true;
				value = res[wordOffset];
				break;
			}
		}

		if (!foundInCache) {
			value = mem.read(address);
			neededCycles += mem.hitTime;
		}

		// write in the caches where the data doesn't exist
//		 XXX assume no cycles to wait (uncommented). Updated, assume cycles to wait (commented0
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		for (int j = i-1; j >= 0; j--) {
			// dont' apply penalty
			short[] block = caches[j].compatibleBlock(address, mem);
			caches[j].write(address, block);
			
			// apply penalty
//			writes.add(new WriteAction(caches[j].hitTime, mem, caches[j], address, value));
//			neededCycles += caches[j].hitTime;
		}
		neededCycles += mem.hitTime;
		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, value, writes);
	}

	public DataAction write(int address, short word) {
		return write(address, word, false);
	}
	
	// instant, no penalty calculations
	public DataAction write(int address, short word, boolean instant) {
		if (instant == true) {
			mem.write(address, word);
			for (int i = 0; i < caches.length; i++)
				caches[i].write(address, caches[i].compatibleBlock(address, word, mem));
			return null;
		}

		if(caches[0].read(address) != null) {// hit
			if (caches[0].hitPolicy == Cache.WRITE_THROUGH) { // write through
				System.out.println( "in the right codnition");
				return writeThrough(address, word, 0);
			}else { // write back
				return writeBack(address, word, 0);
			}
			
		}else {
			if (caches[0].hitPolicy == 1) { // write back
				if (caches[0].missPolicy == Cache.WRITE_AROUND) 
					System.err.println( "cache level " + 0 + " is writeback and writeAround (not compatible)");
				return writeAllocate(address, word, 0);
			} else { // write through
				if (caches[0].missPolicy == Cache.WRITE_AROUND) { // write Around
					return writeAround(address, word, 0);
				}else { // write allocate
					return writeAllocate(address, word, 0);
				}
			}
			
		}
	}

	// assume no buffer
	private DataAction writeThrough(int address, short word, int cacheIndex) {
		int neededCycles = 0;
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		for (int i = 0; i < caches.length; i++) {
			neededCycles += caches[i].hitTime;
			writes.add(0, new WriteAction(caches[i].hitTime, mem, caches[i], address, word));
		}

		neededCycles += mem.hitTime;
		writes.add(0, new WriteAction(mem.hitTime, mem, null, address, word));
		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, word, writes);
	}

	private DataAction writeBack(int address, int word, int cacheIndex) {
		// TODO write back
		return null;
	}

	private DataAction writeAround(int address, int word, int cacheIndex) {
		// TODO write around
		return null;
	}

	private DataAction writeAllocate(int address, short word, int cacheIndex) {
		int neededCycles = 0;
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		
		// write to memory
		neededCycles += mem.hitTime;
		WriteAction memWrite = new WriteAction(mem.hitTime, mem, null, address, word);
		writes.add(memWrite);
		
		
		// if one write allocate in one cache, assume write allocate in all lower caches (write around
		// is not allowed).
		for (int i = caches.length - 1; i >= cacheIndex; i--) {
			WriteAction write = new WriteAction(caches[i].hitTime, mem, caches[i], address, word);
			writes.add(write);
			neededCycles += caches[i].hitTime;
			
		}
		
		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, word, writes);
	}

	// test reading and write hit
	public static void test1() {
		int levels = 2;
		int[] numWords = {8, 16};
		int[] blockSizes =  {1,2};
		int[] hitTimes =  {10, 20};
		int[] assocs =  {1, 2};
		int[] hitPolicies =  {0,0};
		int[] missPolicies = {1,1};
		BengoData d = new BengoData(levels, numWords, blockSizes,
									hitTimes, assocs, hitPolicies, missPolicies);

//		System.out.println(d.caches[0]);
//		System.out.println("-------------------------");
//		System.out.println(d.caches[1]);
		Memory mem = new Memory(50);
		mem.write(0, (short)99);
		mem.write(1, (short)98);
		mem.write(7, (short)97);
		mem.write(6, (short)96);
		d.mem = mem;

//		System.out.println("caches[0] " + d.caches[0]);
		System.out.println(d.read(7));
		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);


		System.out.println(d.read(7));
		System.out.println(d.read(6));

		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);

		// test write-hit
		DataAction action = d.write(7, (short)100, false);
		for (int i = 0; i < 90; i++) {
//			System.out.println("Cycle " + Bengo.CURRENT_CYCLE);
//			System.out.println(action);
			action.update();
			Bengo.CURRENT_CYCLE++;
//			System.out.println(mem.map);
		}
//
		System.out.println(d.caches[0]);
		System.out.println(mem.map);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);
	}
	
	public static void test2() {
		int levels = 2;
		int[] numWords = {8, 16};
		int[] blockSizes =  {1,2};
		int[] hitTimes =  {10, 20};
		int[] assocs =  {1, 2};
		int[] hitPolicies =  {Cache.WRITE_THROUGH, Cache.WRITE_THROUGH};
		int[] missPolicies = {Cache.WRITE_ALLOCATE, Cache.WRITE_ALLOCATE};
		BengoData d = new BengoData(levels, numWords, blockSizes,
									hitTimes, assocs, hitPolicies, missPolicies);
		Memory mem = new Memory(50);
		d.mem = mem;

		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);
		System.out.println(mem);
		
		DataAction action = d.write(18, (short)88);
		for (int i = 0; i < 90; i++) {
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		System.out.println(Arrays.toString(d.caches[1].map(18)));
	}

	public static void main(String[] args) {
		test2();
		System.out.println(Integer.toBinaryString(18));
	}
}
