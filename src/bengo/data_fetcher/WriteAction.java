package bengo.data_fetcher;

import bengo.Bengo;

public class WriteAction {
	int startCycle; // DataAction will set this in update
	int neededCycles;
	Cache cache;
	Memory mem;
	int address;
	short word;
	boolean dirtyWrite;

	public WriteAction(int startCycle, int neededCycles, Memory mem, Cache cache, int address, short word, boolean dirtyWrite) {;
		this.startCycle = startCycle;
		this.neededCycles = neededCycles;
		this.cache = cache;
		this.mem = mem;
		this.address = address;
		this.word = word;
		this.dirtyWrite = dirtyWrite;
	}

	public WriteAction(int startCycle, int neededCycles, Memory mem, Cache cache, int address, short word) {
		this(startCycle, neededCycles, mem, cache, address, word, true);
	}
	
	public void update() {
		if (Bengo.CURRENT_CYCLE == startCycle + neededCycles - 1) {
			if (cache != null) { // write to cache action
				System.out.println("writing to cache " + cache.name + "  address: " + address + "  word: " + word+ "  at cycle: " + Bengo.CURRENT_CYCLE);
				cache.write(address, cache.compatibleBlock(address, word, mem), dirtyWrite);
				System.out.println(cache);
			}
			else {
				System.out.println("writing to mem " +  "  address: " + address + "  word: " + word + "  at cycle: " + Bengo.CURRENT_CYCLE);
				mem.write(address, word);
				System.out.println(mem.map);
			}

		}
	}

	public boolean isReady() {
		return (Bengo.CURRENT_CYCLE == startCycle + neededCycles - 1);
	}

	public String toString() {
		String s = "start: " + startCycle;
		s += "  needed: " + neededCycles;
		s += "  address: " + address;
		s += "  word: " + word;
		s += "  on ";
		s += cache == null? "Memory" : cache.name;

		return s;
	}

}
