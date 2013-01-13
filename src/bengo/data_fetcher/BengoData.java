package bengo.data_fetcher;

import java.util.ArrayList;

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

	public DataAction fetch(int address) {
		int neededCycles = 0;
		int[] res;
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
		// assume no cycles to wait
		for (int j = i-1; j >= 0; j--) {
			int[] block = caches[j].compatibleBlock(address, mem);
//			write(j, address, block, true);
			caches[j].write(address, block);
		}
		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, value);
	}

	// instant, no penalty calculations
	public DataAction write(int address, int word, boolean instant) {
		// TODO  write policies
		if (instant == true) {
			mem.write(address, word);
			return null;
		}

		if(caches[0].read(address) != null) {// hit
			if (caches[0].hitPolicy == 0) { // write through
				System.out.println( "in the right codnition");
				return writeThrough(address, word, 0);
			}else { // write back
				return writeBack(address, word, 0);
			}
		}else {
			if (caches[0].missPolicy == 0) { // write through
				writeAround(address, word, 0);
			}else { // write back
				writeAllocate(address, word, 0);
			}
		}
		return null;
	}

	// assume no buffer
	private DataAction writeThrough(int address, int word, int cacheIndex) {
		int neededCycles = 0;
		ArrayList<WriteAction> writes = new ArrayList<WriteAction>();
		for (int i = 0; i < caches.length; i++) {
			neededCycles += caches[i].hitTime;
			writes.add(0, new WriteAction(Bengo.CURRENT_CYCLE, caches[i].hitTime, mem, caches[i], address, word));
		}

		neededCycles += mem.hitTime;
		writes.add(0, new WriteAction(Bengo.CURRENT_CYCLE, mem.hitTime, mem, address, word));
		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, word, writes);
	}

	private DataAction writeBack(int address, int word, int cacheIndex) {
		return null;
	}

	private DataAction writeAround(int address, int word, int cacheIndex) {
		return null;
	}

	private DataAction writeAllocate(int address, int word, int cacheIndex) {
		return null;
	}

	public static void test() {
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
		mem.write(0, 99);
		mem.write(1, 98);
		mem.write(7, 97);
		mem.write(6, 96);
		d.mem = mem;

//		System.out.println("caches[0] " + d.caches[0]);
		System.out.println(d.fetch(7));
		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);


		System.out.println(d.fetch(7));
		System.out.println(d.fetch(6));

		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);

		DataAction action = d.write(7, 100, false);
		for (int i = 0; i < 90; i++) {
			System.out.println("Cycle " + Bengo.CURRENT_CYCLE);
			System.out.println(action);
			Bengo.CURRENT_CYCLE++;
			action.update();
			System.out.println(mem.map);
		}

		System.out.println(d.caches[0]);
		System.out.println("-------------------------");
		System.out.println(d.caches[1]);
	}

	public static void main(String[] args) {
		test();
	}
}
