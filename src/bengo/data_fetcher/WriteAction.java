package bengo.data_fetcher;

import bengo.Bengo;

public class WriteAction {
	int startCycle;
	int neededCycles;
	Cache cache;
	Memory mem;
	int address;
	int word;

	public WriteAction(int startCycle, int neededCycles, Memory mem, Cache cache, int address, int word) {;
		this.startCycle = startCycle;
		this.neededCycles = neededCycles;
		this.cache = cache;
		this.mem = mem;
		this.address = address;
		this.word = word;
	}

	public WriteAction(int startCycle, int neededCycles, Memory mem, int address, int word) {
		this(startCycle, neededCycles, mem, null, address, word);
	}

	public void update() {
		if (Bengo.CURRENT_CYCLE == startCycle + neededCycles) {
			if (cache != null) { // write to cache action
				cache.write(address, cache.compatibleBlock(address, word, mem));
			}
			else {
				mem.write(address, word);
			}

		}
	}

	public boolean isReady() {
		return (Bengo.CURRENT_CYCLE == startCycle + neededCycles);
	}

	public String toString() {
		String s = "start: " + startCycle;
		s += "  needed: " + neededCycles;
		s += "  address: " + address;
		s += "  word: " + word;

		return s;
	}

}
