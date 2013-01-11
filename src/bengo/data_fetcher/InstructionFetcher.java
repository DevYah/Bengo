package bengo.data_fetcher;

import java.util.ArrayList;

import bengo.Bengo;
import bengo.Instruction;

public class InstructionFetcher {
	/*
	 * This is a container for the caches and memory
	 */
	
	int levels;
	Cache[] caches;
	ArrayList<Instruction> instructions; 
	int instructionHit;
	
	public InstructionFetcher(int levels,
						int size1, 		int size2, 		int size3,
						int lineSize1, 	int lineSize2,	int lineSize3,
  					   	int hitTime1,  	int hitTime2, 	int hitTime3,
  					   	int assoc1, 	int assoc2, 	int assoc3,
  					   	int hitPolicy1,	int hitPolicy2,	int hitPolicy3,
  					   	int missPolicy1,int missPolicy2,int missPolicy3,
  					   	ArrayList<Instruction> instructions, int instructionsHit) {

		this.instructions = instructions;
		this.instructionHit = instructionsHit;
		
		this.levels = levels;
		caches = new Cache[levels];
		
		if (levels <= 1)
			caches[0] = new Cache(size1, lineSize1, hitTime1, assoc1, hitPolicy1, missPolicy1);
		if (levels <= 2)
			caches[1] = new Cache(size2, lineSize2, hitTime2, assoc2,hitPolicy2, missPolicy2);
		if (levels <= 3)
			caches[2] = new Cache(size3, lineSize3, hitTime3, assoc3, hitPolicy3, missPolicy3);
	}
	
	
	public FetchAction fetch(int address) {
		int neededCycles = 0;
		for(int i = 0; i < levels; i++) {
			Integer res = caches[i].read(address);
			neededCycles += caches[i].hitTime; // in case of hit or miss
			if (res != null) { // in case of hit
				return new FetchAction(address, Bengo.CURRENT_CYCLE, neededCycles);
			}
		}
		neededCycles += 100; // FIXME what if the instruction was not in the cache
		return new FetchAction(address, Bengo.CURRENT_CYCLE, neededCycles);
	}

}