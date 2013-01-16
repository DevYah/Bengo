package bengo.data_fetcher;

import java.util.ArrayList;

import bengo.Bengo;

public class DataAction {
	int startCycle;
	int neededCyles;
	public int address; // memory address
	short word;

	ArrayList<WriteAction> writes;
	ArrayList<ReadAction> reads;

	public DataAction(int address, int startCycle, int neededCycles, short word, ArrayList<WriteAction> writes, ArrayList<ReadAction> reads) {
		this.startCycle = startCycle;
		this.neededCyles = neededCycles;
		this.address = address;
		this.word = word;

		this.writes = writes;
		this.reads = reads;
	}
	public int getNeededCycles()
	{
		return this.neededCyles;
	}

	public void update() {
		updateWrites();
		updateReads();
	}
	
	public void updateReads() {
		if (reads.size() == 0)
			return;
		
		if (reads.get(0).startCycle > Bengo.CURRENT_CYCLE)
			return ;
		
		reads.get(0).update();
		if (reads.get(0).isReady()) {
			reads.remove(0);
		}
	}
	
	public void updateWrites() {
		if (writes.size() == 0)
			return;
		
		if (writes.get(0).startCycle > Bengo.CURRENT_CYCLE)
			return ;
		
		writes.get(0).update();
		if (writes.get(0).isReady()) {
			writes.remove(0);
		}
		
	}

	public boolean isReady() {
		if (startCycle + neededCyles - 1 == Bengo.CURRENT_CYCLE) {
			return true;
		}
		if (startCycle + neededCyles - 1  < Bengo.CURRENT_CYCLE) {
			System.out.println("WARNING: somthing is wrong, this check is bad." +
					" The fetch instructino of address " + address
					+ " was ready at  "  + (this.startCycle + this.neededCyles  - 1) +  " CURRENT CYCLE " + Bengo.CURRENT_CYCLE + " CYCLES " );
			return true;
		}
		return false;
	}

	public int getRemaingCycles() {
		return Bengo.CURRENT_CYCLE - startCycle - neededCyles;
	}

	public Short getData() {
		if (isReady()) {
			return word;
		}
		else {
			System.err.println("Data is not ready yet");
			return (Short) null;
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
		
		s += "\nreads: \n";
		for (ReadAction w : reads)
			s += "\t" +  w.toString() + "\n";
		
		s += "\nwrites: \n";
		for (WriteAction w : writes)
			s += "\t" +  w.toString() + "\n";


		return s;
	}

	public static void test() {
		DataAction d = new DataAction(123, 0, 2,(short) 1213, new ArrayList<WriteAction>(), new ArrayList<ReadAction>());
		if (d.getData() != null)
			System.out.println(d.getData());
		Bengo.CURRENT_CYCLE ++;
		if (d.getData() != null)
			System.out.println(d.getData());
		Bengo.CURRENT_CYCLE ++;
	}
}
