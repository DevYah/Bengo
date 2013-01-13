package bengo.data_fetcher;

import java.util.ArrayList;

import bengo.Bengo;

class DataAction {
	int startCycle;
	int neededCyles;
	int address; // memory address
	int word;

	ArrayList<WriteAction> writes;

	public DataAction(int address, int startCycle, int neededCycles, int word, ArrayList<WriteAction> writes) {
		this.startCycle = startCycle;
		this.neededCyles = neededCycles;
		this.address = address;
		this.word = word;

		this.writes = writes;
		if (writes.size() >= 1)
			this.writes.get(0).startCycle = Bengo.CURRENT_CYCLE;
	}

	public DataAction(int address, int startCycle, int neededCycles, int word) {
		this(address,  startCycle,  neededCycles,  word, new ArrayList<WriteAction>());
	}

	public void update() {
		if (writes.size() == 0)
			return;
		writes.get(0).update();
		if (writes.get(0).isReady()) {
			writes.remove(0);
			if (writes.size() != 0)
				writes.get(0).startCycle = Bengo.CURRENT_CYCLE + 1;
		}
		
	}

	public boolean isReady() {
		if (startCycle + neededCyles == Bengo.CURRENT_CYCLE) {
			return true;
		}
		if (startCycle + neededCyles + 1 < Bengo.CURRENT_CYCLE) {
			System.out.println("WARNING: somthing is wrong, this check is bad." +
					" The fetch instructino of address " + address
					+ " was ready at least one cycle before the check");
			return true;
		}
		return false;
	}

	public int getRemaingCycles() {
		return Bengo.CURRENT_CYCLE - startCycle - neededCyles;
	}

	public Integer getData() {
		if (isReady()) {
			return word;
		}
		else {
			System.err.println("Data is not ready yet");
			return (Integer) null;
		}
	}

	public String toString() {
		String s = "address: " + address;
		s += "  start cycle:" + startCycle;
		s += "  needed Cycles: " + neededCyles ;
		s += "  data: " + word;
		if (isReady())
			s += " (Ready)";
		else
			s += " (Not Ready)";
		s += "\nwrites: \n";
		for (WriteAction w : writes)
			s += "\t" +  w.toString() + "\n";


		return s;
	}

	public static void test() {
		DataAction d = new DataAction(123, 0, 2, 1213);
		if (d.getData() != null)
			System.out.println(d.getData());
		Bengo.CURRENT_CYCLE ++;
		if (d.getData() != null)
			System.out.println(d.getData());
		Bengo.CURRENT_CYCLE ++;
	}
}
