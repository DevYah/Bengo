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
						int numlines1, 	int numlines2, 	int numlines3,
  					   	int hitTime1,  	int hitTime2, 	int hitTime3,
  					   	int penalty1, 	int penalty2, 	int penalty3,
  					   	int assoc1, 	int assoc2, 	int assoc3,
  					   	int policy1,  	int policy2, 	int policy3,
  					   	ArrayList<Instruction> instructions, int instructionsHit) {

		this.instructions = instructions;
		this.instructionHit = instructionsHit;
		
		this.levels = levels;
		caches = new Cache[levels];
		
		if (levels <= 1)
			caches[0] = new Cache(numlines1, hitTime1, penalty1, assoc1, policy1);
		if (levels <= 2)
			caches[1] = new Cache(numlines2, hitTime2, penalty2, assoc2, policy2);
		if (levels <= 3)
			caches[2] = new Cache(numlines3, hitTime3, penalty3, assoc3, policy3);
	}
	
	
	public Instruction fetch(int address) {
		for(int i = 0; i < levels; i++) {
			Integer res = caches[i].read(address);
			if (res == null) {
				// miss, apply penalty 
				// FIXME fix the the logic of penalty
				Bengo.CURRENT_CYCLE += caches[i].penalty;
			}else {
				// FIXME fix the the logic of penalty
				Bengo.CURRENT_CYCLE += caches[i].hitTime;
				//return res;
			}
		}
		// FIXME fix the logic of penalty
		Bengo.CURRENT_CYCLE += instructionHit;
		return instructions.get(address);
	}
}