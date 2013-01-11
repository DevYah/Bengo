package bengo.data_fetcher;

import java.util.HashMap;

public class Memory {
	public int hitTime;
	HashMap<Integer, Integer> map;
	
	public Memory(int hitTime) {
		// number of bytes kb * 2^10
		// number of words is  bytes/4 (one word is one integer = 4bytes) 
		// number of entries kb * 2 ^ 8
		
		map = new HashMap<Integer, Integer>();
		this.hitTime = hitTime;
	}
	
	public Integer read(int address) {
		return map.get(address);
	}
	
	public void write(int address, int value) {
		map.put(address, value);
	}
}
