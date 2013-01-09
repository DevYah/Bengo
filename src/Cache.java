public class Cache {
	
	int numLines;
	int hitTime;
	int penalty;
	int associativity;
	int policy; // 0 for write back, 1 for write through
	int[] map;
	int[] dirtyBits;
	
	public Cache(int numLines, int hitTime, int penalty, int assoc, int policy) {
		this.numLines 	= numLines;
		this.hitTime 	= hitTime;
		this.penalty 	= penalty;
		this.associativity = assoc;
		this.policy 	= policy;
		
		map = new int[numLines];
		dirtyBits = new int[numLines];
	}
	
	public Integer read(Integer address) {
		if (map[address] != 0) {
			return map[address];
		}else {
			// false
			return null;
		}
	}
}