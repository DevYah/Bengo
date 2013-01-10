
public class ReservationStation {
    String name;
    boolean busy;
    int operation;
    int vj, vk, A;
    //ROB Qj, Qk, dest;

    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	public int getOperation() {
		return operation;
	}
	public void setOperation(int operation) {
		this.operation = operation;
	}
	public int getVj() {
		return vj;
	}
	public void setVj(int vj) {
		this.vj = vj;
	}
	public int getVk() {
		return vk;
	}
	public void setVk(int vk) {
		this.vk = vk;
	}
	public int getA() {
        return A;
    }
    public void setA(int A) {
        this.A = A; 
    }
    
}
