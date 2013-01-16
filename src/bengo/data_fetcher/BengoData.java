package bengo.data_fetcher;

import java.util.ArrayList;
import java.util.Arrays;

import bengo.Bengo;

public class BengoData {
	/*
	 * This is a container for the caches and memory
	 */

	int levels;
	public Cache[] caches;
	public Memory mem;

	public BengoData(int levels, int[] sizes, int[]lineSizes,
					int[] hitTimes, int[] assocs, int[] hitPolicies,
					int[] missPolicies, int memHitTime) {

		this.levels = levels;
		caches = new Cache[levels];
		this.mem = new Memory(memHitTime);

		// notes that blockSizes must be in an increasing order
		if (levels >= 1)
			caches[0] = new Cache(sizes[0], lineSizes[0], hitTimes[0], assocs[0],
								hitPolicies[0], missPolicies[0]);
		if (levels >= 2)
			caches[1] = new Cache(sizes[1], lineSizes[1], hitTimes[1], assocs[1],
								hitPolicies[1], missPolicies[1]);
		if (levels >= 3)
			caches[2] = new Cache(sizes[2], lineSizes[2], hitTimes[2], assocs[2],
								hitPolicies[2], missPolicies[2]);
	}

	public DataAction read(int address) {
		int neededCycles = 0;
		short[] res;
		int wordOffset;
		int i;
		boolean foundInCache = false;
		short value = -5555;
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		ArrayList<ReadAction> reads = new ArrayList<ReadAction>();
		for(i = 0; i < levels; i++) {
			res = caches[i].read(address);
			wordOffset = caches[i].map(address)[2] >> 2;
			reads.add(new ReadAction(Bengo.CURRENT_CYCLE + neededCycles,
									caches[i].hitTime, mem, caches[i], address));
			
			neededCycles += caches[i].hitTime; // in case of hit or miss
			
			if (caches[i].isHit(address)) { // in case of hit
				foundInCache = true;
				value = res[wordOffset];
				break;
			}
		}

		if (!foundInCache) {
			reads.add(new ReadAction(Bengo.CURRENT_CYCLE + neededCycles,
							mem.hitTime, mem, null, address));
			value = mem.read(address);
			neededCycles += mem.hitTime;
		}

		// write in the caches where the data doesn't exist
		for (int j = i-1; j >= 0; j--) {
			// dont' apply penalty
//			short[] block = caches[j].compatibleBlock(address, mem);
//			caches[j].write(address, block);
			
			// apply penalty
			writes.add(new WriteAction(Bengo.CURRENT_CYCLE + neededCycles, 
							caches[j].hitTime, mem, caches[j], address, value, false));
			neededCycles += caches[j].hitTime;
		}
		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, value, writes, reads);
	}

	public DataAction write(int address, short word) {
		return write(address, word, false);
	}
	
	// instant, no penalty calculations
	public DataAction write(int address, short word, boolean instant) {
		if (instant == true) {
			mem.write(address, word);
			//for (int i = 0; i < caches.length; i++)
				//caches[i].write(address, caches[i].compatibleBlock(address, word, mem), true);
			return null;
		}
		ArrayList<WriteAction> writes;
		if(caches[0].isHit(address)) {// hit
			System.out.println("HIT");
			if (caches[0].hitPolicy == Cache.WRITE_THROUGH) { // write through
				writes=  writeThrough(address, word, 0);
			}else { // write back
				writes =  writeBack(address, word, 0);
			}
			
		}else {
			if (caches[0].hitPolicy == 1) { // write back
				if (caches[0].missPolicy == Cache.WRITE_AROUND) 
					System.err.println( "cache level " + 0 + " is writeback and writeAround (not compatible)");
				
				writes =  writeAllocate(address, word, 0);
			} else { // write through
				if (caches[0].missPolicy == Cache.WRITE_AROUND) { // write Around
					writes =  writeAround(address, word, 0);
				}else { // write allocate
					writes =  writeAllocate(address, word, 0);
				}
			}
			
		}
		return new DataAction(address, Bengo.CURRENT_CYCLE, getNeededCycles(writes),
								word, writes, new ArrayList<ReadAction>());
	}

	private int getNeededCycles(ArrayList actions) {
		int res = 0;
		for (Object a : actions) {
			if (a instanceof WriteAction )
				res += ((WriteAction) a).neededCycles;
			else
				res += ((ReadAction) a).neededCycles;
				
		}
		return res;
	}

	// assume no buffer
	private ArrayList<WriteAction> writeThrough(int address, short word, int cacheIndex) {
		int neededCycles = 0;
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		for (int i = 0; i < caches.length; i++) {
			writes.add(0, new WriteAction(Bengo.CURRENT_CYCLE + neededCycles, 
								caches[i].hitTime, mem, caches[i], address, word));
			neededCycles += caches[i].hitTime;
		}

		writes.add(0, new WriteAction(Bengo.CURRENT_CYCLE + neededCycles,
							mem.hitTime, mem, null, address, word));
		
		neededCycles += mem.hitTime;
		return writes;
//		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles,
//					word, writes, new ArrayList<ReadAction>());
	}

	private ArrayList<WriteAction> writeBack(int address, short word, int cacheIndex) {
		int neededCycles = 0;
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		CacheBlock toReplace = caches[cacheIndex].getBlockToReplace(address);
		if (toReplace.dirty) {
			System.out.println("dirty");
			// write in lower levels of cache
			writes.addAll(writeThrough(address, toReplace.getData()[0], cacheIndex));
			neededCycles += getNeededCycles(writes);
			writes.add(new WriteAction(Bengo.CURRENT_CYCLE + neededCycles, caches[cacheIndex].hitTime,
						mem, caches[cacheIndex], address, word));
			neededCycles += caches[cacheIndex].hitTime;
		}else {
			System.out.println("not dirty");
			writes.add(new WriteAction(Bengo.CURRENT_CYCLE, caches[cacheIndex].hitTime, mem, caches[cacheIndex], address, word));
		}
		return writes;
	}

	private ArrayList<WriteAction> writeAround(int address, short word, int cacheIndex) {
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		WriteAction write = new WriteAction(Bengo.CURRENT_CYCLE, mem.hitTime, mem, null, address, word);
		writes.add(write);
		return writes;
	}

	private ArrayList<WriteAction> writeAllocate(int address, short word, int cacheIndex) {
		int neededCycles = 0;
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		
		// write to memory
		WriteAction memWrite = new WriteAction(Bengo.CURRENT_CYCLE + neededCycles,
									mem.hitTime, mem, null, address, word);
		neededCycles += mem.hitTime;
		writes.add(memWrite);
		
		
		// if one write allocate in one cache, assume write allocate in all lower 
		// caches (write around is not allowed).
		for (int i = caches.length - 1; i >= cacheIndex; i--) {
			WriteAction write = new WriteAction(Bengo.CURRENT_CYCLE + neededCycles,
									caches[i].hitTime, mem, caches[i], address, word);
			writes.add(write);
			neededCycles += caches[i].hitTime;
			
		}
		
//		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles,
//					word, writes, new ArrayList<ReadAction>());
		return writes;
	}
	
	public void printRatios(){
		for (int i = 0; i < levels; i++)
			System.out.println(caches[i].name + "  Hit Ratio: " + caches[i].getHitRatio() + "   Miss Ratio: " + caches[i].getMissRatio());
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
									hitTimes, assocs, hitPolicies, missPolicies,50);

		Memory mem = new Memory(50);
		mem.write(0, (short)99);
		mem.write(1, (short)98);
		mem.write(7, (short)97);
		mem.write(6, (short)96);
		d.mem = mem;
		
//		d.caches[1].write(7, d.caches[1].compatibleBlock(7, mem));

		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);
		
//		System.out.println("caches[0] " + d.caches[0]);
//		System.out.println(d.read(7));
		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);


//		System.out.println(d.read(6));
//
//		System.out.println(d.caches[0]);
//		System.out.println("-------------------------");
//		System.out.println(d.caches[1]);

		// test write-hit
//		DataAction action = d.write(7, (short)100, false);
		DataAction action = d.read(7);
//		System.out.println(action);
		for (int i = 0; i < 120; i++) {
//			System.out.println("\n\nCycle " + Bengo.CURRENT_CYCLE);
//			System.out.println(action);
			action.update();
			Bengo.CURRENT_CYCLE++;
//			System.out.println(mem.map);
		}
		
		System.out.println("\n\n----$$$$\n\n");
		action = d.read(7);
		System.out.println(action);
		for (int i = 0; i < 120; i++) {
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		
		System.out.println("\n\n----$$$$\n\n");
		action = d.read(7);
		System.out.println(action);
		for (int i = 0; i < 120; i++) {
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		System.out.println(d.caches[0]);
		System.out.println(mem.map);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);
		
		System.out.println("misses: " + d.caches[0].misses);
		System.out.println("hits  " +d.caches[0].hits);
		System.out.println();
		System.out.println("misses: " + d.caches[1].misses);
		System.out.println("hits: " + d.caches[1].hits);
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
									hitTimes, assocs, hitPolicies, missPolicies,50);


		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);
		System.out.println(d.mem);
		
		DataAction action = d.write(18, (short)88);
		for (int i = 0; i < 90; i++) {
			System.out.println("\n\nCycle " + Bengo.CURRENT_CYCLE);
			System.out.println(action);
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		System.out.println(Arrays.toString(d.caches[1].map(18)));
	}
	
	public static void testWB() {
		int levels = 2;
		int[] numWords = {8, 16};
		int[] blockSizes =  {1,2};
		int[] hitTimes =  {10, 20};
		int[] assocs =  {1, 2};
		int[] hitPolicies =  {Cache.WRITE_BACK, Cache.WRITE_BACK};
		int[] missPolicies = {Cache.WRITE_ALLOCATE, Cache.WRITE_ALLOCATE};
		BengoData d = new BengoData(levels, numWords, blockSizes,
									hitTimes, assocs, hitPolicies, missPolicies,50);

		d.mem.write(2, (short)22);
		
		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);
		System.out.println(d.mem);
		
		
		System.out.println("\n\n---------\n\n");
		System.out.println("write 88 in address 18");
		DataAction action = d.write(18, (short)88);
		System.out.println(action);
		for (int i = 0; i < 82; i++) {
//			System.out.println("\n\nCycle " + Bengo.CURRENT_CYCLE);
//			System.out.println(action);
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		
		System.out.println("\n\n---------\n\n");
		System.out.println("write 77 in address 18");
		action = d.write(18, (short)777);
		System.out.println(action);
		System.out.println(action);
		for (int i = 0; i < 12; i++) {
//			System.out.println("\n\nCycle " + Bengo.CURRENT_CYCLE);
//			System.out.println(action);
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		
		System.out.println("\n\n---------\n\n");
		System.out.println("read address 18");
		action = d.read(18);
		System.out.println(action);
		for (int i = 0; i < 12; i++) {
//			System.out.println("\n\nCycle " + Bengo.CURRENT_CYCLE);
//			System.out.println(action);
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		System.out.println("\n\n---------\n\n");
		System.out.println("read address 2");
		action = d.read(2);
		System.out.println(action);
		for (int i = 0; i < 115; i++) {
//			System.out.println("\n\nCycle " + Bengo.CURRENT_CYCLE);
//			System.out.println(action);
			action.update();
			Bengo.CURRENT_CYCLE++;
		}
		System.out.println(Arrays.toString(d.caches[1].map(18)));
	}

	public static void main(String[] args) {
		test1();
//		testWB();
	}
}
