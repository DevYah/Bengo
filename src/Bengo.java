
public class Bengo {
	
	public static int CURRENT_CYCLE;
	static Memory memory; 
	// 	ROB, Reservation Station, Instruction, Cache & Memory
	Instruction[] instructs;
	public static void main(String[] abbas) {
		
	}
}

class CircularQueue <T> {
	int head, tail;
	T queue[];
	
	public CircularQueue(int size) {
		head = tail = 0;
		queue = (T[]) new Object[size];
	}
	public void enqueue(T val) {
		queue[tail++] = val;
		tail %= queue.length;
	}
	public T dequeue(int val) {
		T ret = queue[head++];
		head %= queue.length;
		return ret;
	}
}