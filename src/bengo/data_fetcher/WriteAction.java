package bengo.data_fetcher;

import bengo.Bengo;

public class WriteAction {
	int startCycle;
	int neededCyles;
	int address; // memory address
	int data;
	
	public WriteAction(int address, int startCycle, int neededCycles, int data) {
		this.startCycle = startCycle;
		this.neededCyles = neededCycles;
		this.address = address;
		
	}
	
	public boolean isReady() {
		if (startCycle + neededCyles -1 == Bengo.CURRENT_CYCLE) {
			return true;
		}
		if (startCycle + neededCyles -1 > Bengo.CURRENT_CYCLE) {
			System.err.println("somthing is wrong, this check is bad." +
					" The fetch instructino of address " + address
					+ " was ready at least one cycle before the check");
			return true;
		}
		return false;
	}
	
	public int getRemaingCycles() {
		return Bengo.CURRENT_CYCLE - startCycle - neededCyles;
	}
}
