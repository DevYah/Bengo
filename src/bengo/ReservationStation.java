package bengo;

public class ReservationStation {
	
	Instruction instruction;
	String name;
	boolean busy = false;
	String operation;
	String vj ="";
	String vk ="";
	String qj ="";
	String qk ="";
	int answer;
	String status;
	int ROBEntry;
	int remainingTime;
	boolean execReady = false;
	boolean isWritten = false;
	
	
	public ReservationStation(String name)
	{
		this.name = name;
	}
	public void setWritten(boolean written)
	{
		this.isWritten = written;
	}
	public void setROBIndex(int ROBEntry)
	{
		this.ROBEntry = ROBEntry;
	}
	public void setAnswer(int answer)
	{
		this.answer = answer;
	}
	public boolean isCompatible(String instructionType)
	{
		return this.name.contains(instructionType);
	}
	
	public void setOperation(String operation)
	{
		this.operation = operation;
	}
	public void setVj(String vj)
	{
		this.vj = vj;
	}
	public void setVk(String vk)
	{
		this.vk = vk;
	}
	public void setQj(String qj)
	{
		this.qj = qj;
	}
	public void setQk(String qk)
	{
		this.qk = qk;
	}
	public void use()
	{
		this.busy = true;
	}
	public void finish()
	{
		this.busy = false;
	}
	public void assignInstruction(Instruction instruction, int delay)
	{
		this.instruction = instruction;
		this.remainingTime = delay;
		this.use();
	//	System.err.println("Instruction " + instruction + " assigned to " + name + " with delay " + delay);
		
	}
	public boolean isFinished()
	{
		remainingTime--;
		if(remainingTime == 0)
		{
			//this.busy = false;
			execReady = true;
			return true;
		}
	//	System.out.println("REMAINING TIME " + remainingTime);
		return false;
	}
	public void reset()
	{
		// called when reservation station is done
		System.out.println("--------RESET  " + this.name);
		vj = qj = qk = vk = "";
		busy = execReady = isWritten = false;
		answer = remainingTime = ROBEntry = 0;
		this.instruction = null;
		
	}

	public String getVk() {
		return vk;
	}
	
	public String getVj() {
		return vj;
	}
	public boolean isBusy() {
		return this.busy;
	}
	public String toString()
	{
		if(this.instruction != null)
		return "Instruction : " + this.instruction.toString() + " QJ " + this.qj + " QK " + this.qk
				+ " VJ " + this.vj + " VK " + this.vk + " DEST " + this.ROBEntry + " Operation " + this.operation
				+ " name " + this.name + " BUSY " + this.busy; 
		return  "Instruction : null " + " QJ " + this.qj + " QK " + this.qk
				+ " VJ " + this.vj + " VK " + this.vk + " DEST " + this.ROBEntry + " Operation " + this.operation
				+ " name " + this.name + " BUSY " + this.busy; 
	}
}
