package bengo.data_fetcher;

import java.util.HashMap;

public class Memory {
	public int hitTime;
	HashMap<Integer, Short> map;

	public Memory(int hitTime) {
		// number of bytes kb * 2^10
		// number of words is  bytes/4 (one word is one integer = 4bytes)
		// number of entries kb * 2 ^ 8

		map = new HashMap<Integer, Short>();
		this.hitTime = hitTime;
	}

	public short read(int address) {
		
		Short val = map.get(address);
		if (val == null)
			val = -1;
		return val;
			
	}

	public void write(int address, short value) {
		map.put(address, value);
	}
	
	public String toString() {
		return "Memory: " + map.toString();
	}
}
