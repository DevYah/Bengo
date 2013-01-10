package Bengo;

public class Memory {
	int size; // in KB
	public int hitTime;
	int[] array;
	
	public Memory(int size, int hitTime) {
		// number of bytes kb * 2^10
		// number of words is  bytes/4 (one word is one integer = 4bytes) 
		// number of entries kb * 2 ^ 8
		
		int numEntries = size * (1 << 8);
		array = new int[numEntries];
	}
	
	public Integer read(int address) {
		return array[address];
	}
	
	public Integer write(int address, int value) {
		return array[address] = value;
	}
}
