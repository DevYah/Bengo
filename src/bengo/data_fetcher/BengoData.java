package bengo.data_fetcher;

import bengo.Bengo;

public class BengoData {
	/*
	 * This is a container for the caches and memory
	 */
	
	int levels;
	Cache[] caches;
	Memory mem;
	
	public BengoData(int levels, int[] sizes, int[]lineSizes,
						int[] hitTimes, int[] assocs, int[] hitPolicies,
						int[] missPolicies) {

		this.levels = levels;
		caches = new Cache[levels];
		
		if (levels >= 1)
			caches[0] = new Cache(sizes[0], lineSizes[0], hitTimes[0], assocs[0], hitPolicies[0], missPolicies[0]);
		if (levels >= 2)
			caches[1] = new Cache(sizes[1], lineSizes[1], hitTimes[1], assocs[1], hitPolicies[1], missPolicies[1]);
		if (levels >= 3)
			caches[2] = new Cache(sizes[2], lineSizes[2], hitTimes[2], assocs[2], hitPolicies[2], missPolicies[2]);
	}
	
	public DataAction fetch(int address) {
		int neededCycles = 0;
		for(int i = 0; i < levels; i++) {
			Integer res = caches[i].read(address);
			neededCycles += caches[i].hitTime; // in case of hit or miss
			if (res != null) { // in case of hit
				return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, res);
			}
		}
		neededCycles += mem.hitTime;
		return new DataAction(address, Bengo.CURRENT_CYCLE, neededCycles, mem.read(address));
	}
	
	public DataAction write(int address, int value) {
		// TODO 
		return null;
	}
	
	public static void main(String[] args) {
		int levels = 1;
		int[] numWords = {12};
		int[] lineSizes =  {1};
		int[] hitTimes =  {10};
		int[] assocs =  {1};
		int[] hitPolicies =  {1};
		int[] missPolicies = {1};
		BengoData d = new BengoData(levels, numWords,lineSizes,
										hitTimes, assocs, hitPolicies, missPolicies);
		
		Memory mem = new Memory(20);
		mem.write(0, 99);
		mem.write(1, 98);
		mem.write(7, 97);
		d.mem = mem;
		
//		System.out.println("caches[0] " + d.caches[0]);
		System.out.println(d.fetch(7));
		
	}
}