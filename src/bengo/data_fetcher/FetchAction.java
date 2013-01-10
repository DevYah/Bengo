package bengo.data_fetcher;

import bengo.Bengo;

public class FetchAction {
	int startCycle;
	int neededCyles;
	int address; // memory address
	
	int data;
	
	public FetchAction(int address, int startCycle, int neededCycles) {
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
	
	public int getData() {
		if (isReady()) {
			return data;
		}
		else {
			System.err.println("Data is not ready yet");
			return (Integer) null;
		}
	}

	public static void main(String[] args) {
		int a = (Integer)null;
		System.out.println(a);
	}
}
